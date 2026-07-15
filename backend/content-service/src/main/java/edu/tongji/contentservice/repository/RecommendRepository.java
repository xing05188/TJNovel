package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Recommend;
import edu.tongji.contentservice.entity.RecommendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, RecommendId> {
    
    /**
     * 根据读者ID查找推荐记录
     * @param readerId 读者ID
     * @return 推荐记录列表
     */
    @Query("SELECT r FROM Recommend r WHERE r.readerId = :readerId")
    List<Recommend> findByReaderId(@Param("readerId") Long readerId);
    
    /**
     * 根据小说ID查找推荐记录
     * @param novelId 小说ID
     * @return 推荐记录列表
     */
    @Query("SELECT r FROM Recommend r WHERE r.novelId = :novelId")
    List<Recommend> findByNovelId(@Param("novelId") Long novelId);
    
    /**
     * 检查推荐记录是否存在
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @return 是否存在
     */
    boolean existsByNovelIdAndReaderId(Long novelId, Long readerId);
}