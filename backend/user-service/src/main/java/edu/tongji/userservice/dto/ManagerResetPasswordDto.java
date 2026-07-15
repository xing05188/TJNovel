package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 管理员重置密码DTO
 */
@Schema(description = "管理员重置密码请求")
public class ManagerResetPasswordDto {
    
    @Schema(description = "管理员用户名", example = "manager123", required = true)
    private String managerName;
    
    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;
    
    public ManagerResetPasswordDto() {
    }
    
    public ManagerResetPasswordDto(String managerName, String newPassword) {
        this.managerName = managerName;
        this.newPassword = newPassword;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}