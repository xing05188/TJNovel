package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Likes;
import edu.tongji.contentservice.service.LikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点赞API控制器
 */
@RestController
@RequestMapping("/api/Likes")
@Tag(name = "LikesAPI", description = "点赞相关API")
public class LikesController {
    
    private final LikesService likesService;
    
    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }
    
    /**
     * 给指定评论点赞
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 点赞记录
     */
    @PostMapping("/Like")
    @Operation(summary = "给指定评论点赞", description = "给指定评论点赞")
    public ResponseEntity<Likes> likeComment(
            @Parameter(description = "评论ID", required = true) @RequestParam Long commentId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId) {
        
        Likes like = likesService.likeComment(commentId, readerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(like);
    }
    
    /**
     * 取消点赞某条评论
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 操作结果
     */
    @PostMapping("/Unlike")
    @Operation(summary = "取消点赞某条评论", description = "取消点赞某条评论")
    public ResponseEntity<Map<String, String>> unlikeComment(
            @Parameter(description = "评论ID", required = true) @RequestParam Long commentId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId) {
        
        likesService.unlikeComment(commentId, readerId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "取消点赞成功");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查读者是否已点赞某条评论
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 是否已点赞
     */
    @GetMapping("/IsLiked")
    @Operation(summary = "检查读者是否已点赞某条评论", description = "检查读者是否已点赞某条评论")
    public ResponseEntity<Map<String, Boolean>> isReaderLikedComment(
            @Parameter(description = "评论ID", required = true) @RequestParam Long commentId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId) {
        
        boolean isLiked = likesService.isReaderLikedComment(commentId, readerId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", isLiked);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取某条评论的点赞数量
     * @param commentId 评论ID
     * @return 点赞数量
     */
    @GetMapping("/Count/{commentId}")
    @Operation(summary = "获取某条评论的点赞数量", description = "获取某条评论的点赞数量")
    public ResponseEntity<Map<String, Long>> getLikesCountByCommentId(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        
        Long count = likesService.getLikesCountByCommentId(commentId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取所有点赞记录
     * @param page 页码，从1开始，默认为1
     * @param size 页大小，默认为10
     * @return 点赞记录分页结果
     */
    @GetMapping
    @Operation(summary = "获取所有点赞记录", description = "获取所有点赞记录")
    public ResponseEntity<Page<Likes>> getAllLikes(
            @Parameter(description = "页码，从1开始，默认为1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "页大小，默认为10") @RequestParam(defaultValue = "10") int size) {
        
        Page<Likes> likes = likesService.getAllLikes(page, size);
        
        return ResponseEntity.ok(likes);
    }
    
}