package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 读者注册DTO
 */
@Schema(description = "读者注册请求")
public class ReaderRegisterDto {
    
    @Schema(description = "读者用户名", example = "reader123", required = true)
    private String readerName;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    public ReaderRegisterDto() {
    }
    
    public ReaderRegisterDto(String readerName, String password, String phone) {
        this.readerName = readerName;
        this.password = password;
        this.phone = phone;
    }
    
    public String getReaderName() {
        return readerName;
    }
    
    public void setReaderName(String readerName) {
        this.readerName = readerName;
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

