package edu.tongji.adminservice.web;

import edu.tongji.adminservice.dto.NovelManagementRequest;
import edu.tongji.adminservice.dto.NovelManagementLogDto;
import edu.tongji.adminservice.entity.NovelManagement;
import edu.tongji.adminservice.service.NovelManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小说管理API控制器
 */
@RestController
@RequestMapping("/api/NovelManagement")
@Tag(name = "NovelManagementAPI", description = "小说管理相关API")
public class NovelManagementController {
    
    private final NovelManagementService novelManagementService;
    
    public NovelManagementController(NovelManagementService novelManagementService) {
        this.novelManagementService = novelManagementService;
    }
    
    /**
     * 记录管理员对小说的管理操作
     * @param request 小说管理请求参数
     * @return 创建的小说管理记录
     */
    @PostMapping
    @Operation(summary = "记录小说管理操作", description = "记录管理员对小说的管理操作")
    public ResponseEntity<NovelManagement> recordManagement(
            @Parameter(description = "管理参数", required = true) @RequestBody NovelManagementRequest request) {
        
        NovelManagement novelManagement = novelManagementService.recordManagement(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(novelManagement);
    }
    
    /**
     * 获取指定小说的管理日志列表
     * @param novelId 小说ID
     * @return 小说管理日志DTO列表
     */
    @GetMapping("/logs/{novelId}")
    @Operation(summary = "获取指定小说的管理日志列表", description = "根据小说ID获取小说管理日志")
    public ResponseEntity<List<NovelManagementLogDto>> getNovelManagementLogs(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId) {
        
        List<NovelManagementLogDto> logs = novelManagementService.getNovelManagementLogs(novelId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 获取所有小说的管理日志列表
     * @return 所有小说管理日志DTO列表
     */
    @GetMapping("/logs/all")
    @Operation(summary = "获取所有小说的管理日志列表", description = "获取所有小说的管理日志记录")
    public ResponseEntity<List<NovelManagementLogDto>> getAllNovelManagementLogs() {
        List<NovelManagementLogDto> logs = novelManagementService.getAllNovelManagementLogs();
        return ResponseEntity.ok(logs);
    }
}