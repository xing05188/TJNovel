package edu.tongji.common.service;

import edu.tongji.common.config.MinioStorageProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * MinIO 对象存储服务实现（S3 兼容协议）
 */
@Service
@ConditionalOnProperty(prefix = "minio", name = "access-key")
public class MinioStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(MinioStorageService.class);

    private final MinioStorageProperties properties;
    private S3Client s3Client;

    @Autowired
    public MinioStorageService(MinioStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    properties.getAccessKey(), properties.getSecretKey());

            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create(properties.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.US_EAST_1) // Region 对于 MinIO 无意义，但 SDK 需要
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true) // MinIO 使用路径风格
                            .build())
                    .build();

            // 确保存储桶存在
            ensureBucketExists(properties.getBucket());

            logger.info("MinIO 客户端初始化成功: endpoint={}, bucket={}",
                    properties.getEndpoint(), properties.getBucket());
        } catch (Exception e) {
            logger.error("MinIO 客户端初始化失败: {}", e.getMessage());
            // 不抛出异常，允许应用在不依赖 MinIO 时启动
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            s3Client.listBuckets();
            try {
                s3Client.headBucket(b -> b.bucket(bucketName));
            } catch (NoSuchBucketException e) {
                s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());
                logger.info("创建 MinIO 存储桶: {}", bucketName);
            }
        } catch (S3Exception e) {
            logger.warn("检查/创建存储桶失败: {}", e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (s3Client == null) {
            throw new RuntimeException("MinIO 客户端未初始化，请检查配置");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;

            // 构建完整路径
            String objectKey = folder != null && !folder.isEmpty()
                    ? folder + "/" + fileName
                    : fileName;

            // 上传文件
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 返回文件访问 URL
            String publicUrl = properties.getPublicUrl();
            if (publicUrl != null && !publicUrl.isEmpty()) {
                return publicUrl + "/" + properties.getBucket() + "/" + objectKey;
            }
            return properties.getEndpoint() + "/" + properties.getBucket() + "/" + objectKey;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileName, String folder) {
        if (s3Client == null) {
            throw new RuntimeException("MinIO 客户端未初始化，请检查配置");
        }

        try {
            String objectKey = folder != null && !folder.isEmpty()
                    ? folder + "/" + fileName
                    : fileName;

            s3Client.deleteObject(b -> b
                    .bucket(properties.getBucket())
                    .key(objectKey));
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }
}