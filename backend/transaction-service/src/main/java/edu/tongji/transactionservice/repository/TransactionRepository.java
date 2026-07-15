package edu.tongji.transactionservice.repository;

import edu.tongji.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * 根据读者ID获取打赏记录
     * @param readerId 读者ID
     * @return 打赏记录列表
     */
    @Query("SELECT t FROM Transaction t WHERE t.readerId = :readerId AND t.transType = '打赏' ORDER BY t.time DESC")
    List<Transaction> findRewardTransactionsByReaderId(@Param("readerId") Long readerId);
    
    /**
     * 根据读者ID获取订阅记录（解锁章节记录）
     * @param readerId 读者ID
     * @return 订阅记录列表
     */
    @Query("SELECT t FROM Transaction t WHERE t.readerId = :readerId AND t.transType = '解锁章节' ORDER BY t.time DESC")
    List<Transaction> findSubscriptionTransactionsByReaderId(@Param("readerId") Long readerId);
    
    /**
     * 根据读者ID获取充值记录
     * @param readerId 读者ID
     * @return 充值记录列表
     */
    @Query("SELECT t FROM Transaction t WHERE t.readerId = :readerId AND t.transType = '充值' ORDER BY t.time DESC")
    List<Transaction> findRechargeTransactionsByReaderId(@Param("readerId") Long readerId);
    
    /**
     * 根据读者ID获取所有交易记录
     * @param readerId 读者ID
     * @return 所有交易记录列表
     */
    List<Transaction> findByReaderIdOrderByTimeDesc(Long readerId);
    
    /**
     * 根据读者ID和交易类型获取交易记录
     * @param readerId 读者ID
     * @param transType 交易类型
     * @return 交易记录列表
     */
    @Query("SELECT t FROM Transaction t WHERE t.readerId = :readerId AND t.transType = :transType ORDER BY t.time DESC")
    List<Transaction> findByReaderIdAndTransTypeOrderByTimeDesc(@Param("readerId") Long readerId, @Param("transType") String transType);
    
    /**
     * 根据读者ID和时间范围获取交易记录
     * @param readerId 读者ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易记录列表
     */
    @Query("SELECT t FROM Transaction t WHERE t.readerId = :readerId AND t.time >= :startTime AND t.time <= :endTime ORDER BY t.time DESC")
    List<Transaction> findByReaderIdAndTimeBetweenOrderByTimeDesc(
            @Param("readerId") Long readerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据读者ID、交易类型和时间范围获取交易记录
     * @param readerId 读者ID
     * @param transType 交易类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易记录列表
     */
    @Query("SELECT t FROM Transaction t WHERE t.readerId = :readerId AND t.transType = :transType AND t.time >= :startTime AND t.time <= :endTime ORDER BY t.time DESC")
    List<Transaction> findByReaderIdAndTransTypeAndTimeBetweenOrderByTimeDesc(
            @Param("readerId") Long readerId,
            @Param("transType") String transType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}