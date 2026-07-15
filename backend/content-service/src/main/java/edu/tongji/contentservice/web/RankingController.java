package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Ranking")
@Tag(name = "Ranking API", description = "小说排行榜相关API")
public class RankingController {
    private final RankingService rankingService;
    
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }
    
    @GetMapping("/collect")
    @Operation(summary = "获取收藏榜单前 n 名", description = "根据收藏数量获取小说排行榜")
    public ResponseEntity<List<Novel>> getCollectRanking(
            @Parameter(description = "排名数量", required = true) @RequestParam int topN,
            @Parameter(description = "小说状态", required = false) @RequestParam(required = false) String status) {
        
        List<Novel> ranking = rankingService.getCollectRanking(topN, status);
        return ResponseEntity.ok(ranking);
    }
    
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐榜单前 n 名", description = "根据推荐数量获取小说排行榜")
    public ResponseEntity<List<Novel>> getRecommendRanking(
            @Parameter(description = "排名数量", required = true) @RequestParam int topN,
            @Parameter(description = "小说状态", required = false) @RequestParam(required = false) String status) {
        
        List<Novel> ranking = rankingService.getRecommendRanking(topN, status);
        return ResponseEntity.ok(ranking);
    }
    
    @GetMapping("/score")
    @Operation(summary = "获取评分榜单前 n 名", description = "根据评分获取小说排行榜")
    public ResponseEntity<List<Novel>> getScoreRanking(
            @Parameter(description = "排名数量", required = true) @RequestParam int topN,
            @Parameter(description = "小说状态", required = false) @RequestParam(required = false) String status) {
        
        List<Novel> ranking = rankingService.getScoreRanking(topN, status);
        return ResponseEntity.ok(ranking);
    }
}