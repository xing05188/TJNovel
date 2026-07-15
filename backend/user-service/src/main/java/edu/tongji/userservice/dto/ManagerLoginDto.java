package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 管理员登录DTO
 */
@Schema(description = "管理员登录请求")
public class ManagerLoginDto {
    
    @Schema(description = "管理员用户名", example = "manager123", required = true)
    private String managerName;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    public ManagerLoginDto() {
    }
    
    public ManagerLoginDto(String managerName, String password) {
        this.managerName = managerName;
        this.password = password;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
