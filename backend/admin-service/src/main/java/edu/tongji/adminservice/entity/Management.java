package edu.tongji.adminservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 管理操作记录实体类
 * 对应数据库表 MANAGEMENT
 */
@Entity
@Table(name = "MANAGEMENT")
public class Management {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MANAGEMENT_ID")
    private Long managementId;
    
    @Column(name = "MANAGER_ID", nullable = false)
    private Long managerId;
    
    @Column(name = "RESULT", length = 200)
    private String result;
    
    @Column(name = "TIME", updatable = false)
    private LocalDateTime time;
    
    // 默认构造函数
    public Management() {
        this.time = LocalDateTime.now();
    }
    
    // 带参数的构造函数
    public Management(Long managerId, String result) {
        this.managerId = managerId;
        this.result = result;
        this.time = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getManagementId() {
        return managementId;
    }
    
    public void setManagementId(Long managementId) {
        this.managementId = managementId;
    }
    
    public Long getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "Management{" +
                "managementId=" + managementId +
                ", managerId=" + managerId +
                ", result='" + result + '\'' +
                ", time=" + time +
                '}';
    }
}