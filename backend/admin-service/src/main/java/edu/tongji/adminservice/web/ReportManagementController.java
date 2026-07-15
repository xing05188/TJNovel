package edu.tongji.adminservice.web;

import edu.tongji.adminservice.dto.ReportManagementRequest;
import edu.tongji.adminservice.entity.ReportManagement;
import edu.tongji.adminservice.service.ReportManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报管理API控制器
 */
@RestController
@RequestMapping("/api/ReportManagement")
@Tag(name = "ReportManagementAPI", description = "举报管理相关API")
public class ReportManagementController {
    
    private final ReportManagementService reportManagementService;
    
    public ReportManagementController(ReportManagementService reportManagementService) {
        this.reportManagementService = reportManagementService;
    }
    
    /**
     * 记录管理员对举报的管理操作
     * @param params 包含reportId, managerId, result的参数
     * @return 创建的举报管理记录
     */
    @PostMapping
    @Operation(summary = "记录举报管理操作", description = "记录管理员对举报的管理操作")
    public ResponseEntity<ReportManagement> recordManagement(
            @Parameter(description = "管理参数", required = true) @RequestBody ReportManagementRequest request) {
        
        ReportManagement reportManagement = reportManagementService.recordManagement(
            request.getManagerId(), request.getResult(), request.getReportId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(reportManagement);
    }
    
    /**
     * 获取指定举报的所有管理处理日志
     * @param reportId 举报ID
     * @return 举报管理记录列表
     */
    @GetMapping("/{reportId}/logs")
    @Operation(summary = "获取指定举报的管理日志列表", description = "根据举报ID获取举报管理日志")
    public ResponseEntity<List<ReportManagement>> getReportManagementLogs(
            @Parameter(description = "举报ID", required = true) @PathVariable Long reportId) {
        
        List<ReportManagement> logs = reportManagementService.getReportManagementLogs(reportId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 获取所有举报的管理日志列表
     * @return 所有举报管理记录列表
     */
    @GetMapping("/logs/all")
    @Operation(summary = "获取所有举报的管理日志列表", description = "获取所有举报的管理日志记录")
    public ResponseEntity<List<ReportManagement>> getAllReportManagementLogs() {
        List<ReportManagement> logs = reportManagementService.getAllReportManagementLogs();
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 获取指定读者发布的所有举报及其管理处理进度
     * @param readerId 读者ID
     * @return 举报管理记录列表
     */
    @GetMapping("/reader/{readerId}/reports-with-logs")
    @Operation(summary = "获取指定读者的举报及其管理进度", description = "根据读者ID获取该读者发布的所有举报及其管理处理进度")
    public ResponseEntity<List<ReportManagement>> getReaderReportsWithLogs(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        try {
            if (readerId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            List<ReportManagement> reports = reportManagementService.getReaderReportsWithLogs(readerId);
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}