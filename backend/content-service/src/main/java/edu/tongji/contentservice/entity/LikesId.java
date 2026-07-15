package edu.tongji.contentservice.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Likes实体复合主键类
 */
public class LikesId implements Serializable {
    
    private Long commentId;
    private Long readerId;
    
    public LikesId() {
    }
    
    public LikesId(Long commentId, Long readerId) {
        this.commentId = commentId;
        this.readerId = readerId;
    }
    
    public Long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        LikesId likesId = (LikesId) o;
        return Objects.equals(commentId, likesId.commentId) &&
               Objects.equals(readerId, likesId.readerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(commentId, readerId);
    }
}