package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Collect;
import edu.tongji.contentservice.entity.CollectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectRepository extends JpaRepository<Collect, CollectId> {
    
    /**
     * 根据读者ID查找收藏记录
     * @param readerId 读者ID
     * @return 收藏记录列表
     */
    @Query("SELECT c FROM Collect c WHERE c.readerId = :readerId")
    List<Collect> findByReaderId(@Param("readerId") Long readerId);
    
    /**
     * 根据小说ID查找收藏记录
     * @param novelId 小说ID
     * @return 收藏记录列表
     */
    @Query("SELECT c FROM Collect c WHERE c.novelId = :novelId")
    List<Collect> findByNovelId(@Param("novelId") Long novelId);
    
    /**
     * 检查收藏记录是否存在
     * @param novelId 小说ID
     * @param readerId 读者ID
     * @return 是否存在
     */
    boolean existsByNovelIdAndReaderId(Long novelId, Long readerId);
}