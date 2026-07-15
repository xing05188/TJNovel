package edu.tongji.adminservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 章节管理记录请求DTO
 */
@Schema(description = "章节管理记录请求")
public class ChapterManagementRequest {
    
    @Schema(description = "管理员ID", example = "1", required = true)
    private Long managerId;
    
    @Schema(description = "操作结果", example = "通过", required = true)
    private String result;
    
    @Schema(description = "小说ID", example = "1", required = true)
    private Long novelId;
    
    @Schema(description = "章节ID", example = "1", required = true)
    private Long chapterId;
    
    // 默认构造函数
    public ChapterManagementRequest() {
    }
    
    // 带参数的构造函数
    public ChapterManagementRequest(Long managerId, String result, Long novelId, Long chapterId) {
        this.managerId = managerId;
        this.result = result;
        this.novelId = novelId;
        this.chapterId = chapterId;
    }
    
    // Getters and Setters
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
}