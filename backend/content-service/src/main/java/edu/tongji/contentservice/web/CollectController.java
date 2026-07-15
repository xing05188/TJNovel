package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Collect;
import edu.tongji.contentservice.service.CollectService;
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
@RequestMapping("/api/Collect")
@Tag(name = "Collect API", description = "小说收藏相关API")
public class CollectController {
    
    private final CollectService collectService;
    
    @Autowired
    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }
    
    /**
     * 添加或更新收藏记录
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @param isPublic 是否公开
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "添加或更新收藏记录", description = "添加新的收藏记录或更新现有记录的公开状态")
    public ResponseEntity<Map<String, Object>> addOrUpdateCollect(
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "是否公开", required = false) @RequestParam(required = false, defaultValue = "是") String isPublic) {
        
        boolean success = collectService.addOrUpdateCollect(novelId, readerId, isPublic);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "收藏成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "收藏失败，小说不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 取消收藏
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @return 操作结果
     */
    @DeleteMapping
    @Operation(summary = "取消收藏", description = "删除指定的收藏记录")
    public ResponseEntity<Map<String, Object>> cancelCollect(
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId) {
        
        boolean success = collectService.cancelCollect(novelId, readerId);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "取消收藏成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "取消收藏失败，收藏记录不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 获取某个读者收藏的所有小说记录
     * @param readerId 读者ID
     * @return 收藏记录列表
     */
    @GetMapping("/reader/{readerId}")
    @Operation(summary = "获取读者收藏列表", description = "根据读者ID获取其收藏的所有小说记录")
    public ResponseEntity<List<Collect>> getCollectsByReaderId(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        
        List<Collect> collects = collectService.getCollectsByReaderId(readerId);
        return ResponseEntity.ok(collects);
    }
    
    /**
     * 获取某部小说被哪些读者收藏
     * @param novelId 小说ID
     * @return 收藏记录列表
     */
    @GetMapping("/novel/{novelId}")
    @Operation(summary = "获取小说收藏者列表", description = "根据小说ID获取收藏该小说的所有读者记录")
    public ResponseEntity<List<Collect>> getCollectsByNovelId(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId) {
        
        List<Collect> collects = collectService.getCollectsByNovelId(novelId);
        return ResponseEntity.ok(collects);
    }
    
    /**
     * 获取所有收藏记录
     * @return 所有收藏记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有收藏记录", description = "获取系统中的所有收藏记录")
    public ResponseEntity<List<Collect>> getAllCollects() {
        List<Collect> collects = collectService.getAllCollects();
        return ResponseEntity.ok(collects);
    }
}