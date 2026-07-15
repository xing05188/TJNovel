package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.WholePurchase;
import edu.tongji.contentservice.entity.WholePurchaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WholePurchaseRepository extends JpaRepository<WholePurchase, WholePurchaseId> {
    
    /**
     * 根据读者ID和小说ID查找整本购买记录
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @return 整本购买记录
     */
    Optional<WholePurchase> findByReaderIdAndNovelId(Long readerId, Long novelId);
    
    /**
     * 检查读者是否已购买某本小说
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @return 是否已购买
     */
    boolean existsByReaderIdAndNovelIdAndIsBought(Long readerId, Long novelId, String isBought);
}