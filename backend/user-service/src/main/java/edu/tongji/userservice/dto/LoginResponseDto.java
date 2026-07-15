package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应DTO
 */
@Schema(description = "登录响应")
public class LoginResponseDto {
    
    @Schema(description = "JWT Token")
    private String token;
    
    @Schema(description = "读者用户名")
    private String readerName;
    
    @Schema(description = "读者ID")
    private Long readerId;
    
    @Schema(description = "作者用户名")
    private String authorName;
    
    @Schema(description = "作者ID")
    private Long authorId;
    
    @Schema(description = "管理员用户名")
    private String managerName;
    
    @Schema(description = "管理员ID")
    private Long managerId;
    
    public LoginResponseDto() {
    }
    
    // 读者登录构造函数
    public LoginResponseDto(String token, String readerName, Long readerId) {
        this.token = token;
        this.readerName = readerName;
        this.readerId = readerId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getReaderName() {
        return readerName;
    }
    
    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }
    
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public Long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public Long getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
}

