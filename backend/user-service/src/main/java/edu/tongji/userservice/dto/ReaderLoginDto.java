package edu.tongji.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 读者登录DTO
 */
@Schema(description = "读者登录请求")
public class ReaderLoginDto {
    
    @Schema(description = "读者用户名", example = "reader123", required = true)
    private String readerName;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    public ReaderLoginDto() {
    }
    
    public ReaderLoginDto(String readerName, String password) {
        this.readerName = readerName;
        this.password = password;
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
}