package edu.tongji.adminservice.entity;

import jakarta.persistence.*;

/**
 * 章节管理记录实体类
 * 对应数据库表 CHAPTER_MANAGEMENT
 */
@Entity
@Table(name = "CHAPTER_MANAGEMENT")
public class ChapterManagement {
    
    @Id
    @Column(name = "MANAGEMENT_ID")
    private Long managementId;
    
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    @Column(name = "CHAPTER_ID", nullable = false)
    private Long chapterId;
    
    // 默认构造函数
    public ChapterManagement() {
    }
    
    // 带参数的构造函数
    public ChapterManagement(Long managementId, Long novelId, Long chapterId) {
        this.managementId = managementId;
        this.novelId = novelId;
        this.chapterId = chapterId;
    }
    
    // Getters and Setters
    public Long getManagementId() {
        return managementId;
    }
    
    public void setManagementId(Long managementId) {
        this.managementId = managementId;
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
    
    @Override
    public String toString() {
        return "ChapterManagement{" +
                "managementId=" + managementId +
                ", novelId=" + novelId +
                ", chapterId=" + chapterId +
                '}';
    }
}