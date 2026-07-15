package edu.tongji.contentservice.web;

import edu.tongji.contentservice.dto.CommentCreateRequest;
import edu.tongji.contentservice.dto.CommentStatusRequest;
import edu.tongji.contentservice.dto.CommentWithRepliesResponseDto;
import edu.tongji.contentservice.entity.Comment;
import edu.tongji.contentservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论API控制器
 */
@RestController
@RequestMapping("/api/Comments")
@Tag(name = "CommentAPI", description = "评论相关API")
public class CommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    private final CommentService commentService;
    
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    
    /**
     * 设置评论状态
     * @param request 设置评论状态请求
     * @return 更新后的评论
     */
    @PostMapping("/Status")
    @Operation(summary = "设置评论状态", description = "设置评论状态")
    public ResponseEntity<Comment> setCommentStatus(
            @Parameter(description = "设置评论状态参数", required = true) @RequestBody CommentStatusRequest request) {
        
        try {
            // 验证请求参数
            if (request == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (request.getCommentId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (request.getManagerId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (request.getResult() == null || request.getResult().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            Comment comment = commentService.setCommentStatus(
                    request.getCommentId(), 
                    request.getStatus(), 
                    request.getManagerId(), 
                    request.getResult());
            
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取某章节下所有通过审核的评论
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 评论列表
     */
    @GetMapping("/ByChapter/{novelId}/{chapterId}")
    @Operation(summary = "获取某章节下所有通过审核的评论", description = "根据小说ID和章节ID获取通过审核的评论列表")
    public ResponseEntity<List<Comment>> getApprovedCommentsByChapter(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId,
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId) {
        
        List<Comment> comments = commentService.getApprovedCommentsByChapter(novelId, chapterId);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * 获取某小说下所有通过审核的评论
     * @param novelId 小说ID
     * @return 评论列表
     */
    @GetMapping("/ByNovel/{novelId}")
    @Operation(summary = "获取某小说下所有通过审核的评论", description = "根据小说ID获取通过审核的评论列表")
    public ResponseEntity<List<Comment>> getApprovedCommentsByNovel(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId) {
        
        List<Comment> comments = commentService.getApprovedCommentsByNovel(novelId);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * 获取某小说下通过审核的评论数量
     * @param novelId 小说ID
     * @return 评论数量
     */
    @GetMapping("/count-by-novel/{novelId}")
    @Operation(summary = "获取某小说下通过审核的评论数量", description = "根据小说ID获取通过审核的评论数量")
    public ResponseEntity<Long> getCommentCountByNovelId(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId) {
        
        Long count = commentService.getCommentCountByNovelId(novelId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 递归删除评论及其所有子评论
     * @param commentId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/DeleteRecursive/{commentId}")
    @Operation(summary = "递归删除评论及其所有子评论", description = "递归删除评论及其所有子评论")
    public ResponseEntity<Void> deleteCommentRecursive(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        
        commentService.deleteCommentRecursive(commentId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 处理 CORS 预检请求
     * @return 响应
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(summary = "处理 CORS 预检请求", description = "处理 CORS 预检请求")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取所有评论
     * @return 评论列表
     */
    @GetMapping
    @Operation(summary = "获取所有评论", description = "获取所有评论列表")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }
    
    /**
     * 根据ID获取评论
     * @param id 评论ID
     * @return 评论
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取评论", description = "根据ID获取评论详情")
    public ResponseEntity<Comment> getCommentById(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id) {
        
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }
    
    /**
     * 创建评论
     * @param request 创建评论请求
     * @return 创建的评论
     */
    @PostMapping
    @Operation(summary = "创建评论", description = "创建新评论")
    public ResponseEntity<Comment> createComment(
            @Parameter(description = "评论信息", required = true) @RequestBody CommentCreateRequest request) {
        
        Comment createdComment = commentService.createComment(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    
    /**
     * 更新评论
     * @param id 评论ID
     * @param comment 更新的评论信息
     * @return 更新后的评论
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新评论", description = "更新指定ID的评论信息")
    public ResponseEntity<Comment> updateComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的评论信息", required = true) @RequestBody Comment comment) {
        
        Comment updatedComment = commentService.updateComment(id, comment);
        
        return ResponseEntity.ok(updatedComment);
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除指定ID的评论")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id) {
        
        commentService.deleteComment(id);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 获取指定章节前N个点赞最多的通过审核的评论
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param topN 前N名
     * @return 评论列表
     */
    @GetMapping("/novel/{novelId}/chapter/{chapterId}/top-liked-comments/{topN}")
    @Operation(summary = "获取指定章节前N个点赞最多的评论", description = "根据小说ID和章节ID获取前N个点赞最多的通过审核的评论")
    public ResponseEntity<List<Comment>> getTopLikedCommentsByChapter(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId,
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId,
            @Parameter(description = "前N名", required = true) @PathVariable Integer topN) {
        
        try {
            List<Comment> comments = commentService.getTopLikedCommentsByChapter(novelId, chapterId, topN);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取指定小说前N个点赞最多的通过审核的评论
     * @param novelId 小说ID
     * @param topN 前N名
     * @return 评论列表
     */
    @GetMapping("/novel/{novelId}/top-liked-comments/{topN}")
    @Operation(summary = "获取指定小说前N个点赞最多的评论", description = "根据小说ID获取前N个点赞最多的通过审核的评论")
    public ResponseEntity<List<Comment>> getTopLikedCommentsByNovel(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId,
            @Parameter(description = "前N名", required = true) @PathVariable Integer topN) {
        
        try {
            List<Comment> comments = commentService.getTopLikedCommentsByNovel(novelId, topN);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取指定读者发布的所有评论及其子评论
     * @param readerId 读者ID
     * @return 评论列表（包含父评论和子评论的结构）
     */
    @GetMapping("/reader/{readerId}")
    @Operation(summary = "获取指定读者的评论", description = "根据读者ID获取该读者发布的所有评论及其子评论")
    public ResponseEntity<List<CommentWithRepliesResponseDto>> getCommentsByReaderId(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        
        try {
            List<CommentWithRepliesResponseDto> comments = commentService.getCommentsWithRepliesByReaderId(readerId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("获取读者评论失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}