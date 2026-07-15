package edu.tongji.transactionservice.repository;

import edu.tongji.transactionservice.entity.AuthorIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者收入Repository
 */
@Repository
public interface AuthorIncomeRepository extends JpaRepository<AuthorIncome, Long> {
    
    /**
     * 根据作者ID查找所有收入记录，按时间降序排列
     * @param authorId 作者ID
     * @return 收入记录列表
     */
    List<AuthorIncome> findByAuthorIdOrderByCreateTimeDesc(Long authorId);
    
    /**
     * 计算作者的总收入
     * @param authorId 作者ID
     * @return 总收入
     */
    @Query("SELECT COALESCE(SUM(ai.amount), 0) FROM AuthorIncome ai WHERE ai.authorId = :authorId")
    BigDecimal getTotalIncomeByAuthorId(@Param("authorId") Long authorId);
}