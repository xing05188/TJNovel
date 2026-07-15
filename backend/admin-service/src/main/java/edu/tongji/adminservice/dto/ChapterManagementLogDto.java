package edu.tongji.adminservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 章节管理日志DTO
 * 包含完整的管理日志信息，包括Management表的详细信息
 */
@Schema(description = "章节管理日志")
public class ChapterManagementLogDto {
    
    @Schema(description = "管理记录ID", example = "1")
    private Long managementId;
    
    @Schema(description = "小说ID", example = "1")
    private Long novelId;
    
    @Schema(description = "章节ID", example = "1")
    private Long chapterId;
    
    @Schema(description = "管理员ID", example = "1")
    private Long managerId;
    
    @Schema(description = "管理员名称", example = "管理员")
    private String managerName;
    
    @Schema(description = "处理结果", example = "通过审核")
    private String result;
    
    @Schema(description = "处理时间", example = "2024-01-01 12:00:00")
    private LocalDateTime time;
    
    public ChapterManagementLogDto() {
    }
    
    public ChapterManagementLogDto(Long managementId, Long novelId, Long chapterId, Long managerId, String managerName, String result, LocalDateTime time) {
        this.managementId = managementId;
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.managerId = managerId;
        this.managerName = managerName;
        this.result = result;
        this.time = time;
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
    
    public Long getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
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
}

