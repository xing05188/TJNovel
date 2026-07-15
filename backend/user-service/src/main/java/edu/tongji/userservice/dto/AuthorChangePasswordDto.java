package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 作者修改密码DTO
 */
@Schema(description = "作者修改密码请求")
public class AuthorChangePasswordDto {
    
    @Schema(description = "作者用户名", example = "author123", required = true)
    private String authorName;
    
    @Schema(description = "旧密码", example = "oldpassword123", required = true)
    private String oldPassword;
    
    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;
    
    public AuthorChangePasswordDto() {
    }
    
    public AuthorChangePasswordDto(String authorName, String oldPassword, String newPassword) {
        this.authorName = authorName;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getOldPassword() {
        return oldPassword;
    }
    
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}