package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingRepository extends JpaRepository<Novel, Long> {
    
    @Query("SELECT n FROM Novel n WHERE n.status = :status ORDER BY n.collectedCount DESC")
    List<Novel> findTopByCollectCount(@Param("status") String status);
    
    @Query("SELECT n FROM Novel n WHERE n.status = :status ORDER BY n.recommendCount DESC")
    List<Novel> findTopByRecommendCount(@Param("status") String status);
    
    @Query("SELECT n FROM Novel n WHERE n.status = :status ORDER BY n.score DESC")
    List<Novel> findTopByScore(@Param("status") String status);
    
    @Query(value = "SELECT n FROM Novel n WHERE n.status = :status ORDER BY n.collectedCount DESC LIMIT :topN", 
           nativeQuery = false)
    List<Novel> findTopByCollectCountWithLimit(@Param("status") String status, @Param("topN") int topN);
    
    @Query(value = "SELECT n FROM Novel n WHERE n.status = :status ORDER BY n.recommendCount DESC LIMIT :topN", 
           nativeQuery = false)
    List<Novel> findTopByRecommendCountWithLimit(@Param("status") String status, @Param("topN") int topN);
    
    @Query(value = "SELECT n FROM Novel n WHERE n.status = :status ORDER BY n.score DESC LIMIT :topN", 
           nativeQuery = false)
    List<Novel> findTopByScoreWithLimit(@Param("status") String status, @Param("topN") int topN);
}