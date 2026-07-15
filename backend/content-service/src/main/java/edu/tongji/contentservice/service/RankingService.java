package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.repository.RankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {
    private final RankingRepository rankingRepository;
    
    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
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
}