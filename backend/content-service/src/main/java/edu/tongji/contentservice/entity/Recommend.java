package edu.tongji.contentservice.entity;

import jakarta.persistence.*;

/**
 * 推荐实体
 * 表示读者推荐的小说
 */
@Entity
@Table(name = "RECOMMEND")
@IdClass(RecommendId.class)
public class Recommend {
    
    @Id
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    @Id
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    @Column(name = "REASON", length = 200)
    private String reason;
    
    public Recommend() {
    }
    
    public Recommend(Long novelId, Long readerId, String reason) {
        this.novelId = novelId;
        this.readerId = readerId;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}

