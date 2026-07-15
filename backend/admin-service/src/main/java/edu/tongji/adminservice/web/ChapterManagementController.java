package edu.tongji.adminservice.web;

import edu.tongji.adminservice.dto.ChapterManagementRequest;
import edu.tongji.adminservice.dto.ChapterManagementLogDto;
import edu.tongji.adminservice.entity.ChapterManagement;
import edu.tongji.adminservice.service.ChapterManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节管理API控制器
 */
@RestController
@RequestMapping("/api/ChapterManagement")
@Tag(name = "ChapterManagementAPI", description = "章节管理相关API")
public class ChapterManagementController {
    
    private final ChapterManagementService chapterManagementService;
    
    public ChapterManagementController(ChapterManagementService chapterManagementService) {
        this.chapterManagementService = chapterManagementService;
    }
    
    /**
     * 记录管理员对章节的管理操作
     * @param params 包含managerId, result, novelId, chapterId的参数
     * @return 创建的章节管理记录
     */
    @PostMapping
    @Operation(summary = "记录章节管理操作", description = "记录管理员对章节的管理操作")
    public ResponseEntity<ChapterManagement> recordManagement(
            @Parameter(description = "管理参数", required = true) @RequestBody ChapterManagementRequest request) {
        
        ChapterManagement chapterManagement = chapterManagementService.recordManagement(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(chapterManagement);
    }
    
    /**
     * 获取指定章节的管理日志列表
     * @param chapterId 章节ID
     * @param novelId 小说ID
     * @return 章节管理日志DTO列表
     */
    @GetMapping("/logs/{chapterId}")
    @Operation(summary = "获取指定章节的管理日志列表", description = "根据章节ID和小说ID获取章节管理日志")
    public ResponseEntity<List<ChapterManagementLogDto>> getChapterManagementLogs(
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId,
            @Parameter(description = "小说ID", required = false) @RequestParam(required = false) Long novelId) {
        
        List<ChapterManagementLogDto> logs = chapterManagementService.getChapterManagementLogs(chapterId, novelId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 获取所有章节的管理日志列表
     * @return 所有章节管理日志DTO列表
     */
    @GetMapping("/logs/all")
    @Operation(summary = "获取所有章节的管理日志列表", description = "获取所有章节的管理日志记录")
    public ResponseEntity<List<ChapterManagementLogDto>> getAllChapterManagementLogs() {
        List<ChapterManagementLogDto> logs = chapterManagementService.getAllChapterManagementLogs();
        return ResponseEntity.ok(logs);
    }
}