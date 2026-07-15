package edu.tongji.contentservice.dto;

import edu.tongji.contentservice.entity.Comment;
import lombok.Data;

import java.util.List;

/**
 * 评论及其回复响应DTO
 * 用于返回包含父评论和子评论的结构
 */
@Data
public class CommentWithRepliesResponseDto {
    /**
     * 父评论
     */
    private Comment parentComment;
    
    /**
     * 子评论列表
     */
    private List<Comment> childComments;
}

