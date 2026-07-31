package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.repository.NovelRepository;
import edu.tongji.contentservice.repository.RankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final RankingRepository rankingRepository;
    private final NovelRepository novelRepository;
    private final ReadingRankingService readingRankingService;

    public RankingService(RankingRepository rankingRepository, NovelRepository novelRepository,
                          ReadingRankingService readingRankingService) {
        this.rankingRepository = rankingRepository;
        this.novelRepository = novelRepository;
        this.readingRankingService = readingRankingService;
    }
    
    public List<Novel> getCollectRanking(int topN, String status) {
        if (status == null || status.trim().isEmpty()) {
            status = "连载"; // 默认状态
        }
        
        List<Novel> novels = rankingRepository.findTopByCollectCountWithLimit(status, topN);
        
        // 如果没有找到指定状态的小说，尝试查找所有状态的小说
        if (novels.isEmpty()) {
            novels = rankingRepository.findTopByCollectCountWithLimit("", topN);
        }
        
        return novels;
    }
    
    public List<Novel> getRecommendRanking(int topN, String status) {
        if (status == null || status.trim().isEmpty()) {
            status = "连载"; // 默认状态
        }
        
        List<Novel> novels = rankingRepository.findTopByRecommendCountWithLimit(status, topN);
        
        // 如果没有找到指定状态的小说，尝试查找所有状态的小说
        if (novels.isEmpty()) {
            novels = rankingRepository.findTopByRecommendCountWithLimit("", topN);
        }
        
        return novels;
    }
    
    public List<Novel> getScoreRanking(int topN, String status) {
        if (status == null || status.trim().isEmpty()) {
            status = "连载"; // 默认状态
        }
        
        List<Novel> novels = rankingRepository.findTopByScoreWithLimit(status, topN);
        
        // 如果没有找到指定状态的小说，尝试查找所有状态的小说
        if (novels.isEmpty()) {
            novels = rankingRepository.findTopByScoreWithLimit("", topN);
        }
        
        return novels;
    }

    /**
     * 阅读排行榜：数据来自 Redis ZSet（实时累计阅读次数），而非 MySQL 全表排序。
     * 先取 ZSet 中阅读次数最多的候选，再回查 Novel 详情并（可选）按状态过滤，
     * 结果保持 ZSet 的排名顺序。
     */
    public List<Novel> getReadRanking(int topN, String status) {
        // 多拉取一些候选，便于在状态过滤后仍满足 topN
        Map<Long, Double> top = readingRankingService.getTopReads(Math.max(topN, 1) * 3);
        List<Long> ids = new ArrayList<>(top.keySet());
        if (ids.isEmpty()) {
            return List.of();
        }

        List<Novel> novels = novelRepository.findAllById(ids);
        Map<Long, Novel> novelMap = novels.stream()
                .collect(Collectors.toMap(Novel::getNovelId, n -> n, (a, b) -> a));

        List<Novel> result = new ArrayList<>();
        for (Long id : ids) {
            Novel novel = novelMap.get(id);
            if (novel == null) {
                continue;
            }
            if (status != null && !status.trim().isEmpty() && !status.equals(novel.getStatus())) {
                continue;
            }
            result.add(novel);
            if (result.size() >= topN) {
                break;
            }
        }
        return result;
    }
}