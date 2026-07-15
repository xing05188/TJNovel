package edu.tongji.contentservice.entity;

import jakarta.persistence.*;

/**
 * 收藏实体
 * 表示读者收藏的小说
 */
@Entity
@Table(name = "COLLECT")
@IdClass(CollectId.class)
public class Collect {
    
    @Id
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    @Id
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    @Column(name = "IS_PUBLIC", length = 10)
    private String isPublic = "是";
    
    public Collect() {
    }
    
    public Collect(Long novelId, Long readerId, String isPublic) {
        this.novelId = novelId;
        this.readerId = readerId;
        this.isPublic = isPublic;
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
    
    public String getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
}

