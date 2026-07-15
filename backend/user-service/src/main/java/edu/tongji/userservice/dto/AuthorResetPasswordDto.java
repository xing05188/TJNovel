package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 作者重置密码DTO
 */
@Schema(description = "作者重置密码请求")
public class AuthorResetPasswordDto {
    
    @Schema(description = "作者用户名", example = "author123", required = true)
    private String authorName;
    
    @Schema(description = "手机号", example = "13800138000", required = true)
    private String phone;
    
    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;
    
    public AuthorResetPasswordDto() {
    }
    
    public AuthorResetPasswordDto(String authorName, String phone, String newPassword) {
        this.authorName = authorName;
        this.phone = phone;
        this.newPassword = newPassword;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}