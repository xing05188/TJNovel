package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Recommend;
import edu.tongji.contentservice.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Recommend")
@Tag(name = "Recommend API", description = "小说推荐相关API")
public class RecommendController {
    
    private final RecommendService recommendService;
    
    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }
    
    /**
     * 推荐小说
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @param reason 推荐理由
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "推荐小说", description = "读者推荐小说并可以添加推荐理由")
    public ResponseEntity<Map<String, Object>> recommendNovel(
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "推荐理由", required = false) @RequestParam(required = false) String reason) {
        
        try {
            // 验证参数
            if (novelId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "小说ID不能为空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (readerId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "读者ID不能为空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            boolean success = recommendService.addRecommend(novelId, readerId, reason);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "推荐成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "推荐失败，小说不存在或已推荐过");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "推荐失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 取消推荐
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @return 操作结果
     */
    @DeleteMapping
    @Operation(summary = "取消推荐", description = "取消读者对小说的推荐")
    public ResponseEntity<Map<String, Object>> cancelRecommend(
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId) {
        
        boolean success = recommendService.cancelRecommend(novelId, readerId);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "取消推荐成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "取消推荐失败，推荐记录不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 获取某个读者推荐的所有小说记录
     * @param readerId 读者ID
     * @return 推荐记录列表
     */
    @GetMapping("/reader/{readerId}")
    @Operation(summary = "获取读者推荐列表", description = "根据读者ID获取其推荐的所有小说记录")
    public ResponseEntity<List<Recommend>> getRecommendsByReaderId(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        
        List<Recommend> recommends = recommendService.getRecommendsByReaderId(readerId);
        return ResponseEntity.ok(recommends);
    }
    
    /**
     * 获取某部小说被哪些读者推荐
     * @param novelId 小说ID
     * @return 推荐记录列表
     */
    @GetMapping("/novel/{novelId}")
    @Operation(summary = "获取小说推荐者列表", description = "根据小说ID获取推荐该小说的所有读者记录")
    public ResponseEntity<List<Recommend>> getRecommendsByNovelId(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId) {
        
        List<Recommend> recommends = recommendService.getRecommendsByNovelId(novelId);
        return ResponseEntity.ok(recommends);
    }
    
    /**
     * 获取所有推荐记录
     * @return 所有推荐记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有推荐记录", description = "获取系统中的所有推荐记录")
    public ResponseEntity<List<Recommend>> getAllRecommends() {
        List<Recommend> recommends = recommendService.getAllRecommends();
        return ResponseEntity.ok(recommends);
    }
}