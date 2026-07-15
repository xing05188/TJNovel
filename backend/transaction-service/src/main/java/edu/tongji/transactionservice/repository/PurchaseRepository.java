package edu.tongji.transactionservice.repository;

import edu.tongji.transactionservice.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Purchase实体仓库接口
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    /**
     * 根据读者ID、小说ID和章节ID查找购买记录
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 购买记录
     */
    @Query("SELECT p FROM Purchase p JOIN Transaction t ON p.transactionId = t.transactionId " +
           "WHERE t.readerId = :readerId AND p.novelId = :novelId AND p.chapterId = :chapterId")
    Optional<Purchase> findByReaderIdAndNovelIdAndChapterId(
            @Param("readerId") Long readerId, 
            @Param("novelId") Long novelId, 
            @Param("chapterId") Long chapterId);
    
    /**
     * 检查读者是否已购买指定章节
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 是否已购买
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Purchase p JOIN Transaction t ON p.transactionId = t.transactionId " +
           "WHERE t.readerId = :readerId AND p.novelId = :novelId AND p.chapterId = :chapterId")
    boolean existsByReaderIdAndNovelIdAndChapterId(
            @Param("readerId") Long readerId, 
            @Param("novelId") Long novelId, 
            @Param("chapterId") Long chapterId);
}