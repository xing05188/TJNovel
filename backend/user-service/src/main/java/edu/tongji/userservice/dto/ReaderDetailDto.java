package edu.tongji.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 读者详情响应DTO
 * 用于返回读者信息，不包含敏感信息（如密码）
 */
@Schema(description = "读者详情响应")
public class ReaderDetailDto {
    
    @Schema(description = "读者ID", example = "1")
    private Long readerId;
    
    @Schema(description = "读者用户名", example = "reader123")
    private String readerName;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "性别", example = "男")
    private String gender;
    
    @Schema(description = "余额", example = "100.00")
    private BigDecimal balance;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
    
    @Schema(description = "背景图片URL", example = "https://example.com/background.jpg")
    private String backgroundUrl;
    
    @Schema(description = "收藏是否可见", example = "是")
    private String isCollectVisible;
    
    @Schema(description = "推荐是否可见", example = "是")
    private String isRecommendVisible;
    
    @Schema(description = "创建时间", example = "2025-12-06 06:03:57")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    public ReaderDetailDto() {
    }
    
    public ReaderDetailDto(Long readerId, String readerName, String phone, String gender,
                          BigDecimal balance, String avatarUrl, String backgroundUrl,
                          String isCollectVisible, String isRecommendVisible, LocalDateTime createTime) {
        this.readerId = readerId;
        this.readerName = readerName;
        this.phone = phone;
        this.gender = gender;
        this.balance = balance;
        this.avatarUrl = avatarUrl;
        this.backgroundUrl = backgroundUrl;
        this.isCollectVisible = isCollectVisible;
        this.isRecommendVisible = isRecommendVisible;
        this.createTime = createTime;
    }
    
    // Getters and Setters
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getBackgroundUrl() {
        return backgroundUrl;
    }
    
    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }
    
    public String getIsCollectVisible() {
        return isCollectVisible;
    }
    
    public void setIsCollectVisible(String isCollectVisible) {
        this.isCollectVisible = isCollectVisible;
    }
    
    public String getIsRecommendVisible() {
        return isRecommendVisible;
    }
    
    public void setIsRecommendVisible(String isRecommendVisible) {
        this.isRecommendVisible = isRecommendVisible;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}

