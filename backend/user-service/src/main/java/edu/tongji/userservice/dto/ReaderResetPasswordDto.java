package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 读者重置密码DTO
 */
@Schema(description = "读者重置密码请求")
public class ReaderResetPasswordDto {
    
    @Schema(description = "读者用户名", example = "reader123", required = true)
    private String readerName;
    
    @Schema(description = "手机号", example = "13800138000", required = true)
    private String phone;
    
    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;
    
    public ReaderResetPasswordDto() {
    }
    
    public ReaderResetPasswordDto(String readerName, String phone, String newPassword) {
        this.readerName = readerName;
        this.phone = phone;
        this.newPassword = newPassword;
    }
    
    public String getReaderName() {
        return readerName;
    }
    
    public void setReaderName(String readerName) {
        this.readerName = readerName;
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