package edu.tongji.adminservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "REPORT_MANAGEMENT")
public class ReportManagement {
    
    @Id
    @Column(name = "MANAGEMENT_ID")
    private Long managementId;
    
    @Column(name = "REPORT_ID", nullable = false)
    private Long reportId;
    
    // 默认构造函数
    public ReportManagement() {
    }
    
    // 带参数的构造函数
    public ReportManagement(Long managementId, Long reportId) {
        this.managementId = managementId;
        this.reportId = reportId;
    }
    
    // Getters and Setters
    public Long getManagementId() {
        return managementId;
    }
    
    public void setManagementId(Long managementId) {
        this.managementId = managementId;
    }
    
    public Long getReportId() {
        return reportId;
    }
    
    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
}