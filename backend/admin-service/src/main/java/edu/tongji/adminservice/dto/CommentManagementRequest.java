package edu.tongji.adminservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 评论管理记录请求DTO
 */
@Schema(description = "评论管理记录请求")
public class CommentManagementRequest {
    
    @Schema(description = "管理员ID", example = "1", required = true)
    private Long managerId;
    
    @Schema(description = "操作结果", example = "通过", required = true)
    private String result;
    
    @Schema(description = "评论ID", example = "1", required = true)
    private Long commentId;
    
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
    
    public Long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}