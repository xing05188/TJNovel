package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Recommend;
import edu.tongji.contentservice.entity.RecommendId;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.repository.RecommendRepository;
import edu.tongji.contentservice.repository.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecommendService {
    
    private final RecommendRepository recommendRepository;
    private final NovelRepository novelRepository;
    
    @Autowired
    public RecommendService(RecommendRepository recommendRepository, NovelRepository novelRepository) {
        this.recommendRepository = recommendRepository;
        this.novelRepository = novelRepository;
    }
    
    /**
     * 添加推荐记录
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @param reason 推荐理由
     * @return 操作结果
     */
    @Transactional
    public boolean addRecommend(Long novelId, Long readerId, String reason) {
        // 检查小说和读者是否存在
        Optional<Novel> novel = novelRepository.findById(novelId);
        if (novel.isEmpty()) {
            return false; // 小说不存在
        }
        
        // 检查推荐记录是否已存在
        if (recommendRepository.existsByNovelIdAndReaderId(novelId, readerId)) {
            return false; // 推荐记录已存在
        }
        
        // 创建新记录
        Recommend recommend = new Recommend(novelId, readerId, reason);
        recommendRepository.save(recommend);
        
        // 更新小说的推荐数
        updateNovelRecommendCount(novelId);
        
        return true;
    }
    
    /**
     * 取消推荐
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @return 操作结果
     */
    @Transactional
    public boolean cancelRecommend(Long novelId, Long readerId) {
        RecommendId recommendId = new RecommendId(novelId, readerId);
        
        if (recommendRepository.existsById(recommendId)) {
            recommendRepository.deleteById(recommendId);
            // 更新小说的推荐数
            updateNovelRecommendCount(novelId);
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取某个读者推荐的所有小说记录
     * @param readerId 读者ID
     * @return 推荐记录列表
     */
    public List<Recommend> getRecommendsByReaderId(Long readerId) {
        return recommendRepository.findByReaderId(readerId);
    }
    
    /**
     * 获取某部小说被哪些读者推荐
     * @param novelId 小说ID
     * @return 推荐记录列表
     */
    public List<Recommend> getRecommendsByNovelId(Long novelId) {
        return recommendRepository.findByNovelId(novelId);
    }
    
    /**
     * 获取所有推荐记录
     * @return 所有推荐记录列表
     */
    public List<Recommend> getAllRecommends() {
        return recommendRepository.findAll();
    }
    
    /**
     * 更新小说的推荐数
     * @param novelId 小说ID
     */
    private void updateNovelRecommendCount(Long novelId) {
        Optional<Novel> novel = novelRepository.findById(novelId);
        if (novel.isPresent()) {
            int recommendCount = recommendRepository.findByNovelId(novelId).size();
            Novel novelEntity = novel.get();
            novelEntity.setRecommendCount(recommendCount);
            novelRepository.save(novelEntity);
        }
    }
}