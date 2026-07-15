package edu.tongji.contentservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.contentservice.entity.Report;
import edu.tongji.contentservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
@Tag(name = "ReportAPI", description = "举报相关API")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @PostMapping("/comment/{commentId}/report")
    @Operation(summary = "举报评论", description = "读者举报评论")
    public ResponseEntity<ApiResponse<Report>> reportComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @Parameter(description = "读者ID") @RequestParam Long readerId,
            @Parameter(description = "举报原因") @RequestParam String reason) {
        try {
            Report report = reportService.reportComment(commentId, readerId, reason);
            return ResponseEntity.ok(ApiResponse.ok("content-service", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        }
    }
    
    @PostMapping("/report/{reportId}/process")
    @Operation(summary = "处理举报", description = "管理员处理举报")
    public ResponseEntity<ApiResponse<Report>> processReport(
            @Parameter(description = "举报ID") @PathVariable Long reportId,
            @Parameter(description = "处理状态") @RequestParam String progress,
            @Parameter(description = "管理员ID") @RequestParam Long managerId,
            @Parameter(description = "处理结果") @RequestParam(required = false) String result) {
        try {
            Report report = reportService.processReport(reportId, progress, managerId, result);
            return ResponseEntity.ok(ApiResponse.ok("content-service", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取所有举报", description = "获取系统中所有举报记录")
    public ResponseEntity<ApiResponse<List<Report>>> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(ApiResponse.ok("content-service", reports));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "获取举报列表失败：" + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取举报", description = "根据举报ID获取举报详情")
    public ResponseEntity<ApiResponse<Report>> getReportById(
            @Parameter(description = "举报ID") @PathVariable Long id) {
        try {
            Optional<Report> report = reportService.getReportById(id);
            if (report.isPresent()) {
                return ResponseEntity.ok(ApiResponse.ok("content-service", report.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("content-service", "举报记录不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "获取举报详情失败：" + e.getMessage()));
        }
    }
    
    @PostMapping
    @Operation(summary = "创建举报", description = "创建新的举报记录")
    public ResponseEntity<ApiResponse<Report>> createReport(
            @Parameter(description = "举报对象") @RequestBody Report report) {
        try {
            Report createdReport = reportService.createReport(report);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("content-service", createdReport));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新举报", description = "更新举报记录")
    public ResponseEntity<ApiResponse<Report>> updateReport(
            @Parameter(description = "举报ID") @PathVariable Long id,
            @Parameter(description = "举报对象") @RequestBody Report report) {
        try {
            Report updatedReport = reportService.updateReport(id, report);
            return ResponseEntity.ok(ApiResponse.ok("content-service", updatedReport));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除举报", description = "删除举报记录")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @Parameter(description = "举报ID") @PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok(ApiResponse.ok("content-service", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        }
    }
    
    @RequestMapping(value = "/api/Reports", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/statistics/pending-reports")
    @Operation(summary = "获取待处理举报数量", description = "获取系统中待处理举报的数量")
    public ResponseEntity<ApiResponse<Long>> getPendingReportsCount() {
        try {
            Long count = reportService.getPendingReportsCount();
            return ResponseEntity.ok(ApiResponse.ok("content-service", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "获取待处理举报数量失败：" + e.getMessage()));
        }
    }
}