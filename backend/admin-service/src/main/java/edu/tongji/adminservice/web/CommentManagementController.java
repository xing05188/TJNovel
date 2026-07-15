package edu.tongji.adminservice.web;

import edu.tongji.adminservice.dto.CommentManagementRequest;
import edu.tongji.adminservice.entity.CommentManagement;
import edu.tongji.adminservice.service.CommentManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理API控制器
 */
@RestController
@RequestMapping("/api/CommentManagement")
@Tag(name = "CommentManagementAPI", description = "评论管理相关API")
public class CommentManagementController {
    
    private final CommentManagementService commentManagementService;
    
    public CommentManagementController(CommentManagementService commentManagementService) {
        this.commentManagementService = commentManagementService;
    }
    
    /**
     * 记录管理员对评论的管理操作
     * @param request 评论管理请求参数
     * @return 创建的评论管理记录
     */
    @PostMapping
    @Operation(summary = "记录评论管理操作", description = "记录管理员对评论的管理操作")
    public ResponseEntity<CommentManagement> recordManagement(
            @Parameter(description = "管理参数", required = true) @RequestBody CommentManagementRequest request) {
        
        CommentManagement commentManagement = commentManagementService.recordManagement(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(commentManagement);
    }
    
    /**
     * 获取指定评论的管理日志列表
     * @param commentId 评论ID
     * @return 评论管理记录列表
     */
    @GetMapping("/logs/{commentId}")
    @Operation(summary = "获取指定评论的管理日志列表", description = "根据评论ID获取评论管理日志")
    public ResponseEntity<List<CommentManagement>> getCommentManagementLogs(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        
        List<CommentManagement> logs = commentManagementService.getCommentManagementLogs(commentId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 获取所有评论的管理日志列表
     * @return 所有评论管理记录列表
     */
    @GetMapping("/logs/all")
    @Operation(summary = "获取所有评论的管理日志列表", description = "获取所有评论的管理日志记录")
    public ResponseEntity<List<CommentManagement>> getAllCommentManagementLogs() {
        List<CommentManagement> logs = commentManagementService.getAllCommentManagementLogs();
        return ResponseEntity.ok(logs);
    }
}