package edu.tongji.userservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "READER")
public class Reader {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "READER_ID")
    private Long readerId;
    
    @Column(name = "READER_NAME", nullable = false, length = 20, unique = true)
    private String readerName;
    
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;
    
    @Column(name = "PHONE", length = 11)
    private String phone;
    
    @Column(name = "GENDER", length = 2)
    private String gender;
    
    @Column(name = "BALANCE", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "AVATAR_URL", length = 255)
    private String avatarUrl;
    
    @Column(name = "BACKGROUND_URL", length = 255)
    private String backgroundUrl;
    
    @Column(name = "IS_COLLECT_VISIBLE", length = 2)
    private String isCollectVisible = "是";
    
    @Column(name = "IS_RECOMMEND_VISIBLE", length = 2)
    private String isRecommendVisible = "是";
    
    @Column(name = "CREATE_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
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

