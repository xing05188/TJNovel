package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "小说审核DTO")
public class NovelReviewDto {
    
    @Schema(description = "新状态", example = "连载", allowableValues = {"待审核", "连载", "完结", "封禁"})
    private String newStatus;
    
    @Schema(description = "管理员ID", example = "1")
    private Long managerId;
    
    @Schema(description = "审核结果", example = "审核通过")
    private String result;
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
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