package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Rate;
import edu.tongji.contentservice.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Rate")
@Tag(name = "Rate API", description = "小说评分相关API")
public class RateController {
    private final RateService rateService;
    
    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }
    
    @PostMapping
    @Operation(summary = "添加评分", description = "读者对小说进行评分")
    public ResponseEntity<Map<String, Object>> addRate(
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "评分", required = true) @RequestParam BigDecimal score) {
        
        boolean success = rateService.addRate(novelId, readerId, score);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "评分添加成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "评分添加失败，小说不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping
    @Operation(summary = "删除评分记录", description = "读者删除对小说的评分")
    public ResponseEntity<Map<String, Object>> deleteRate(
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId) {
        
        boolean success = rateService.deleteRate(novelId, readerId);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "评分删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "评分删除失败，评分记录不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/novel/{novelId}")
    @Operation(summary = "获取某本小说的全部评分记录", description = "查询某本小说的所有评分")
    public ResponseEntity<List<Rate>> getRatesByNovelId(
            @Parameter(description = "小说ID", required = true) @PathVariable Long novelId) {
        
        List<Rate> rates = rateService.getRatesByNovelId(novelId);
        return ResponseEntity.ok(rates);
    }
    
    @GetMapping("/reader/{readerId}")
    @Operation(summary = "获取某位读者对小说的评分记录", description = "查询某位读者对所有小说的评分")
    public ResponseEntity<List<Rate>> getRatesByReaderId(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        
        List<Rate> rates = rateService.getRatesByReaderId(readerId);
        return ResponseEntity.ok(rates);
    }
    
    @GetMapping
    @Operation(summary = "获取全部评分记录", description = "查询系统中所有的评分记录")
    public ResponseEntity<List<Rate>> getAllRates() {
        List<Rate> rates = rateService.getAllRates();
        return ResponseEntity.ok(rates);
    }
}