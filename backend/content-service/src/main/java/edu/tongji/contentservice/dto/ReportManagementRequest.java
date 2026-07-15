package edu.tongji.contentservice.dto;

/**
 * 举报管理请求DTO
 */
public class ReportManagementRequest {
    
    private Long reportId;
    private Long managerId;
    private String result;
    
    public ReportManagementRequest() {
    }
    
    public ReportManagementRequest(Long reportId, Long managerId, String result) {
        this.reportId = reportId;
        this.managerId = managerId;
        this.result = result;
    }
    
    public Long getReportId() {
        return reportId;
    }
    
    public void setReportId(Long reportId) {
        this.reportId = reportId;
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
}