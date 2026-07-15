package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 评论管理请求DTO
 */
@Schema(description = "评论管理请求")
public class CommentManagementRequest {
    
    @Schema(description = "评论ID", example = "1", required = true)
    private Long commentId;
    
    @Schema(description = "管理员ID", example = "1", required = true)
    private Long managerId;
    
    @Schema(description = "操作结果", example = "审核通过", required = true)
    private String result;
    
    public Long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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