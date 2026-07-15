package edu.tongji.userservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 最近阅读实体
 * 表示读者最近阅读的小说记录
 */
@Entity
@Table(name = "RECENT_READINGS")
@IdClass(RecentReadingsId.class)
public class RecentReadings {
    
    @Id
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    @Id
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    @Column(name = "CHAPTER_ID")
    private Long chapterId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "RECENT_READING_TIME", nullable = false)
    private LocalDateTime recentReadingTime;
    
    public RecentReadings() {
    }
    
    public RecentReadings(Long readerId, Long novelId, Long chapterId, LocalDateTime recentReadingTime) {
        this.readerId = readerId;
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.recentReadingTime = recentReadingTime;
    }
    
    // Getters and Setters
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    public Long getChapterId() {
        return chapterId;
    }
    
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }
    
    public LocalDateTime getRecentReadingTime() {
        return recentReadingTime;
    }
    
    public void setRecentReadingTime(LocalDateTime recentReadingTime) {
        this.recentReadingTime = recentReadingTime;
    }
}

