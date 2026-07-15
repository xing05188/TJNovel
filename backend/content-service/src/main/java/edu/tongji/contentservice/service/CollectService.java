package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Collect;
import edu.tongji.contentservice.entity.CollectId;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.repository.CollectRepository;
import edu.tongji.contentservice.repository.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CollectService {
    
    private final CollectRepository collectRepository;
    private final NovelRepository novelRepository;
    
    @Autowired
    public CollectService(CollectRepository collectRepository, NovelRepository novelRepository) {
        this.collectRepository = collectRepository;
        this.novelRepository = novelRepository;
    }
    
    /**
     * 添加或更新收藏记录
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @param isPublic 是否公开
     * @return 操作结果
     */
    @Transactional
    public boolean addOrUpdateCollect(Long novelId, Long readerId, String isPublic) {
        CollectId collectId = new CollectId(novelId, readerId);
        
        // 检查小说和读者是否存在
        Optional<Novel> novel = novelRepository.findById(novelId);
        if (novel.isEmpty()) {
            return false; // 小说不存在
        }
        
        // 检查收藏记录是否已存在
        Optional<Collect> existingCollect = collectRepository.findById(collectId);
        
        if (existingCollect.isPresent()) {
            // 更新现有记录
            Collect collect = existingCollect.get();
            collect.setIsPublic(isPublic);
            collectRepository.save(collect);
        } else {
            // 创建新记录
            Collect collect = new Collect(novelId, readerId, isPublic);
            collectRepository.save(collect);
        }
        
        // 更新小说的收藏数
        updateNovelCollectCount(novelId);
        
        return true;
    }
    
    /**
     * 取消收藏
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @return 操作结果
     */
    @Transactional
    public boolean cancelCollect(Long novelId, Long readerId) {
        CollectId collectId = new CollectId(novelId, readerId);
        
        if (collectRepository.existsById(collectId)) {
            collectRepository.deleteById(collectId);
            // 更新小说的收藏数
            updateNovelCollectCount(novelId);
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取某个读者收藏的所有小说记录
     * @param readerId 读者ID
     * @return 收藏记录列表
     */
    public List<Collect> getCollectsByReaderId(Long readerId) {
        return collectRepository.findByReaderId(readerId);
    }
    
    /**
     * 获取某部小说被哪些读者收藏
     * @param novelId 小说ID
     * @return 收藏记录列表
     */
    public List<Collect> getCollectsByNovelId(Long novelId) {
        return collectRepository.findByNovelId(novelId);
    }
    
    /**
     * 获取所有收藏记录
     * @return 所有收藏记录列表
     */
    public List<Collect> getAllCollects() {
        return collectRepository.findAll();
    }
    
    /**
     * 更新小说的收藏数
     * @param novelId 小说ID
     */
    private void updateNovelCollectCount(Long novelId) {
        Optional<Novel> novel = novelRepository.findById(novelId);
        if (novel.isPresent()) {
            int collectCount = collectRepository.findByNovelId(novelId).size();
            Novel novelEntity = novel.get();
            novelEntity.setCollectedCount(collectCount);
            novelRepository.save(novelEntity);
        }
    }
}