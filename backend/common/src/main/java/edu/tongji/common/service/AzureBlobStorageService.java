package edu.tongji.common.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import edu.tongji.common.config.AzureStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Azure Blob Storage 服务实现
 */
@Service
public class AzureBlobStorageService implements StorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(AzureBlobStorageService.class);
    
    private final BlobServiceClient blobServiceClient;
    private final String containerName;
    
    @Autowired
    public AzureBlobStorageService(AzureStorageProperties properties) {
        // 放宽构造函数约束：如果本地开发环境没有正确配置 Azure，
        // 不让整个服务启动失败，而是在真正上传/删除文件时再报错。
        if (properties.getConnectionString() == null || properties.getConnectionString().isEmpty()
                || properties.getContainer() == null || properties.getContainer().isEmpty()) {
            logger.warn("Azure Storage 未正确配置（connection-string 或 container 为空），相关文件上传/删除功能将不可用");
            this.blobServiceClient = null;
            this.containerName = null;
        } else {
            this.blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(properties.getConnectionString())
                    .buildClient();
            this.containerName = properties.getContainer();
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (blobServiceClient == null || containerName == null) {
            throw new RuntimeException("Azure Storage 未配置，无法上传文件");
        }
        
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 构建完整路径
            String blobName = folder != null && !folder.isEmpty() 
                    ? folder + "/" + fileName 
                    : fileName;
            
            // 获取容器客户端
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            // 上传文件
            blobClient.upload(BinaryData.fromStream(file.getInputStream()), true);
            
            // 返回文件的访问URL
            return blobClient.getBlobUrl();
            
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteFile(String fileName, String folder) {
        if (blobServiceClient == null || containerName == null) {
            throw new RuntimeException("Azure Storage 未配置，无法删除文件");
        }
        try {
            // 构建完整路径
            String blobName = folder != null && !folder.isEmpty() 
                    ? folder + "/" + fileName 
                    : fileName;
            
            // 获取容器客户端
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            // 删除文件
            if (blobClient.exists()) {
                blobClient.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }
}

