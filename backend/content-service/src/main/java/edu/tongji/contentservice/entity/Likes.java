package edu.tongji.contentservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "LIKES")
@IdClass(LikesId.class)
public class Likes implements Serializable {
    
    @Id
    @Column(name = "COMMENT_ID", nullable = false)
    private Long commentId;
    
    @Id
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    // 默认构造函数
    public Likes() {
    }
    
    // 带参数的构造函数
    public Likes(Long commentId, Long readerId) {
        this.commentId = commentId;
        this.readerId = readerId;
    }
    
    // Getters and Setters
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
        
        Likes likes = (Likes) o;
        return Objects.equals(commentId, likes.commentId) &&
               Objects.equals(readerId, likes.readerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(commentId, readerId);
    }
}