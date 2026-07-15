package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 设置评论状态请求DTO
 */
@Schema(description = "设置评论状态请求")
public class CommentStatusRequest {
    
    @Schema(description = "评论ID", example = "1", required = true)
    private Long commentId;
    
    @Schema(description = "状态", example = "通过", required = true)
    private String status;
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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