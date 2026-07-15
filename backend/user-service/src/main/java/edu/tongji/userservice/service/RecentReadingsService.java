package edu.tongji.userservice.service;

import edu.tongji.userservice.entity.RecentReadings;
import edu.tongji.userservice.entity.RecentReadingsId;
import edu.tongji.userservice.repository.RecentReadingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 最近阅读服务
 */
@Service
public class RecentReadingsService {
    
    @Autowired
    private RecentReadingsRepository recentReadingsRepository;
    
    /**
     * 根据读者ID获取所有最近阅读记录，按时间降序排列
     * @param readerId 读者ID
     * @return 最近阅读列表
     */
    public List<RecentReadings> getByReaderId(Long readerId) {
        return recentReadingsRepository.findByReaderIdOrderByRecentReadingTimeDesc(readerId);
    }
    
    /**
     * 添加或更新读者最近阅读记录
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 添加或更新后的最近阅读记录
     */
    @Transactional
    public RecentReadings addOrUpdateRecentReading(Long readerId, Long novelId, Long chapterId) {
        RecentReadingsId id = new RecentReadingsId(readerId, novelId);
        Optional<RecentReadings> existingReading = recentReadingsRepository.findById(id);
        
        if (existingReading.isPresent()) {
            // 更新现有记录
            RecentReadings reading = existingReading.get();
            reading.setChapterId(chapterId);
            reading.setRecentReadingTime(LocalDateTime.now());
            return recentReadingsRepository.save(reading);
        } else {
            // 创建新记录
            RecentReadings newReading = new RecentReadings();
            newReading.setReaderId(readerId);
            newReading.setNovelId(novelId);
            newReading.setChapterId(chapterId);
            newReading.setRecentReadingTime(LocalDateTime.now());
            return recentReadingsRepository.save(newReading);
        }
    }
    
    /**
     * 删除指定读者的某本小说的最近阅读记录
     * @param readerId 读者ID
     * @param novelId 小说ID
     */
    @Transactional
    public void deleteRecentReading(Long readerId, Long novelId) {
        RecentReadingsId id = new RecentReadingsId(readerId, novelId);
        recentReadingsRepository.deleteById(id);
    }
    
    /**
     * 获取读者最近阅读的章节ID
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @return 章节ID，如果不存在则返回null
     */
    public Long getLastReadChapterId(Long readerId, Long novelId) {
        RecentReadingsId id = new RecentReadingsId(readerId, novelId);
        Optional<RecentReadings> reading = recentReadingsRepository.findById(id);
        return reading.map(RecentReadings::getChapterId).orElse(null);
    }
}

