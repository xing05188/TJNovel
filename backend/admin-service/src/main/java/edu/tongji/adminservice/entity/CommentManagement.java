package edu.tongji.adminservice.entity;

import jakarta.persistence.*;

/**
 * 评论管理记录实体类
 * 对应数据库表 COMMENT_MANAGEMENT
 */
@Entity
@Table(name = "COMMENT_MANAGEMENT")
public class CommentManagement {
    
    @Id
    @Column(name = "MANAGEMENT_ID")
    private Long managementId;
    
    @Column(name = "COMMENT_ID", nullable = false)
    private Long commentId;
    
    // 默认构造函数
    public CommentManagement() {
    }
    
    // 带参数的构造函数
    public CommentManagement(Long managementId, Long commentId) {
        this.managementId = managementId;
        this.commentId = commentId;
    }
    
    // Getters and Setters
    public Long getManagementId() {
        return managementId;
    }
    
    public void setManagementId(Long managementId) {
        this.managementId = managementId;
    }
    
    public Long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    
    @Override
    public String toString() {
        return "CommentManagement{" +
                "managementId=" + managementId +
                ", commentId=" + commentId +
                '}';
    }
}