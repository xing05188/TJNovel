package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 作者登录DTO
 */
@Schema(description = "作者登录请求")
public class AuthorLoginDto {
    
    @Schema(description = "作者用户名", example = "author123", required = true)
    private String authorName;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    public AuthorLoginDto() {
    }
    
    public AuthorLoginDto(String authorName, String password) {
        this.authorName = authorName;
        this.password = password;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}