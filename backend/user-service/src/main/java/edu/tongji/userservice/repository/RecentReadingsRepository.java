package edu.tongji.userservice.repository;

import edu.tongji.userservice.entity.RecentReadings;
import edu.tongji.userservice.entity.RecentReadingsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 最近阅读Repository
 */
@Repository
public interface RecentReadingsRepository extends JpaRepository<RecentReadings, RecentReadingsId> {
    
    /**
     * 根据读者ID查找所有最近阅读记录，按时间降序排列
     * @param readerId 读者ID
     * @return 最近阅读列表
     */
    List<RecentReadings> findByReaderIdOrderByRecentReadingTimeDesc(Long readerId);
}

