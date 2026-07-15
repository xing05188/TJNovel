package edu.tongji.userservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.userservice.entity.RecentReadings;
import edu.tongji.userservice.service.RecentReadingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 最近阅读控制器
 * 提供最近阅读相关的API
 */
@RestController
@RequestMapping("/recent-readings")
@Tag(name = "最近阅读管理", description = "读者最近阅读记录相关接口")
public class RecentReadingsController {
    
    @Autowired
    private RecentReadingsService recentReadingsService;
    
    /**
     * 添加或更新读者最近阅读记录
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 添加或更新后的最近阅读记录
     */
    @PostMapping("/add-or-update")
    @Operation(summary = "添加或更新最近阅读记录", description = "添加或更新读者最近阅读记录")
    public ResponseEntity<ApiResponse<RecentReadings>> addOrUpdateRecentReading(
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "章节ID", required = true) @RequestParam Long chapterId) {
        try {
            RecentReadings recentReading = recentReadingsService.addOrUpdateRecentReading(readerId, novelId, chapterId);
            return ResponseEntity.ok(ApiResponse.ok("user-service", recentReading));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("user-service", e.getMessage()));
        }
    }
    
    /**
     * 删除指定读者的某本小说的最近阅读记录
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除最近阅读记录", description = "删除指定读者的某本小说的最近阅读记录")
    public ResponseEntity<ApiResponse<Void>> deleteRecentReading(
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId) {
        try {
            recentReadingsService.deleteRecentReading(readerId, novelId);
            return ResponseEntity.ok(ApiResponse.ok("user-service", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("user-service", e.getMessage()));
        }
    }
    
    /**
     * 获取指定读者的最近阅读记录列表，按时间降序排列
     * @param readerId 读者ID
     * @return 最近阅读列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取读者最近阅读列表", description = "根据读者ID获取该读者的最近阅读记录，按时间降序排列")
    public ResponseEntity<ApiResponse<List<RecentReadings>>> getByReaderId(
            @Parameter(description = "读者ID", required = true, example = "1", name = "readerId", in = ParameterIn.QUERY)
            @RequestParam(value = "readerId", required = true) Long readerId) {
        try {
            if (readerId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("user-service", "readerId参数不能为空"));
            }
            List<RecentReadings> readings = recentReadingsService.getByReaderId(readerId);
            return ResponseEntity.ok(ApiResponse.ok("user-service", readings));
        } catch (Exception e) {
            // 打印详细错误信息到控制台
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取最近阅读列表失败：" + e.getMessage() + " | " + e.getClass().getName()));
        }
    }
    
    /**
     * 获取读者最近阅读的章节ID
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @return 章节ID
     */
    @GetMapping("/last-read-chapter")
    @Operation(summary = "获取最近阅读的章节ID", description = "获取读者最近阅读的章节ID")
    public ResponseEntity<ApiResponse<Long>> getLastReadChapterId(
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId) {
        try {
            Long chapterId = recentReadingsService.getLastReadChapterId(readerId, novelId);
            return ResponseEntity.ok(ApiResponse.ok("user-service", chapterId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("user-service", e.getMessage()));
        }
    }
}

