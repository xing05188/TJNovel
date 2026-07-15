package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 作者注册DTO
 */
@Schema(description = "作者注册请求")
public class AuthorRegisterDto {
    
    @Schema(description = "作者用户名", example = "author123", required = true)
    private String authorName;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    public AuthorRegisterDto() {
    }
    
    public AuthorRegisterDto(String authorName, String password, String phone) {
        this.authorName = authorName;
        this.password = password;
        this.phone = phone;
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
