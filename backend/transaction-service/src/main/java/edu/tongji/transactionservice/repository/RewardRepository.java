package edu.tongji.transactionservice.repository;

import edu.tongji.transactionservice.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 打赏记录数据访问接口
 */
@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    
    /**
     * 根据小说ID获取打赏记录列表
     * @param novelId 小说ID
     * @return 打赏记录列表
     */
    List<Reward> findByNovelIdOrderByTransactionTimeDesc(Long novelId);
    
    /**
     * 根据读者ID获取打赏记录列表
     * @param readerId 读者ID
     * @return 打赏记录列表
     */
    @Query("SELECT r FROM Reward r JOIN r.transaction t WHERE t.readerId = :readerId ORDER BY t.time DESC")
    List<Reward> findByReaderIdOrderByTransactionTimeDesc(@Param("readerId") Long readerId);
    
    /**
     * 根据小说ID获取打赏记录数量
     * @param novelId 小说ID
     * @return 打赏记录数量
     */
    long countByNovelId(Long novelId);
}