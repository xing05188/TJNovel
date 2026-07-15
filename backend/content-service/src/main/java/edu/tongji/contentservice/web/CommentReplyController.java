package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.CommentReply;
import edu.tongji.contentservice.service.CommentReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论回复API控制器
 */
@RestController
@RequestMapping("/api/CommentReply")
@Tag(name = "CommentReplyAPI", description = "评论回复相关API")
public class CommentReplyController {
    
    private final CommentReplyService commentReplyService;
    
    public CommentReplyController(CommentReplyService commentReplyService) {
        this.commentReplyService = commentReplyService;
    }
    
    /**
     * 添加一条评论回复
     * @param commentReply 评论回复信息
     * @return 保存后的评论回复信息
     */
    @PostMapping
    @Operation(summary = "添加一条评论回复", description = "添加一条评论回复")
    public ResponseEntity<CommentReply> addCommentReply(
            @Parameter(description = "评论回复信息", required = true) @RequestBody CommentReply commentReply) {
        
        CommentReply savedReply = commentReplyService.addCommentReply(commentReply);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReply);
    }
    
    /**
     * 根据评论ID获取它的回复关系
     * @param commentId 评论ID
     * @return 评论回复信息
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "根据评论ID获取它的回复关系", description = "根据评论ID获取它的回复关系")
    public ResponseEntity<CommentReply> getCommentReplyByCommentId(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        
        CommentReply commentReply = commentReplyService.getCommentReplyByCommentId(commentId);
        
        if (commentReply == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(commentReply);
    }
    
    /**
     * 获取某条评论下的所有直接回复
     * @param parentId 父评论ID
     * @return 直接回复列表
     */
    @GetMapping("/parent/{parentId}")
    @Operation(summary = "获取某条评论下的所有直接回复", description = "获取某条评论下的所有直接回复")
    public ResponseEntity<List<CommentReply>> getDirectRepliesByParentId(
            @Parameter(description = "父评论ID", required = true) @PathVariable Long parentId) {
        
        List<CommentReply> replies = commentReplyService.getDirectRepliesByParentId(parentId);
        return ResponseEntity.ok(replies);
    }
    
    /**
     * 获取所有评论回复联系集
     * @param page 页码，从1开始，默认为1
     * @param size 页大小，默认为10
     * @return 评论回复联系集分页结果
     */
    @GetMapping
    @Operation(summary = "获取所有评论回复联系集", description = "获取所有评论回复联系集")
    public ResponseEntity<Page<CommentReply>> getAllCommentReplies(
            @Parameter(description = "页码，从1开始，默认为1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "页大小，默认为10") @RequestParam(defaultValue = "10") int size) {
        
        Page<CommentReply> commentReplies = commentReplyService.getAllCommentReplies(page, size);
        
        return ResponseEntity.ok(commentReplies);
    }
}