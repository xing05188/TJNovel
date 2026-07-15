package edu.tongji.common.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 存储服务接口
 */
public interface StorageService {
    
    /**
     * 上传文件
     * @param file 文件
     * @param folder 文件夹路径（可选，如 "covers", "avatars"）
     * @return 文件的访问URL
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * 删除文件
     * @param fileName 文件名或文件路径
     * @param folder 文件夹路径（可选）
     */
    void deleteFile(String fileName, String folder);
    
    /**
     * 上传文件（默认文件夹）
     * @param file 文件
     * @return 文件的访问URL
     */
    String uploadFile(MultipartFile file);
}

