package edu.tongji.adminservice.entity;

import jakarta.persistence.*;

/**
 * 小说管理记录实体类
 * 对应数据库表 NOVEL_MANAGEMENT
 */
@Entity
@Table(name = "NOVEL_MANAGEMENT")
public class NovelManagement {
    
    @Id
    @Column(name = "MANAGEMENT_ID")
    private Long managementId;
    
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    // 默认构造函数
    public NovelManagement() {
    }
    
    // 带参数的构造函数
    public NovelManagement(Long managementId, Long novelId) {
        this.managementId = managementId;
        this.novelId = novelId;
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
    
    @Override
    public String toString() {
        return "NovelManagement{" +
                "managementId=" + managementId +
                ", novelId=" + novelId +
                '}';
    }
}