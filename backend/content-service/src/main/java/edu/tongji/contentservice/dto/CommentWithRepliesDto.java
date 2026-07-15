package edu.tongji.contentservice.dto;

import edu.tongji.contentservice.entity.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentWithRepliesDto {
    private Comment comment;
    private List<CommentWithRepliesDto> replies;
}
