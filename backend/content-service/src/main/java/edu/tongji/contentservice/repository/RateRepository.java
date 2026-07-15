package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Rate;
import edu.tongji.contentservice.entity.RateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, RateId> {
    
    @Query("SELECT r FROM Rate r WHERE r.readerId = :readerId")
    List<Rate> findByReaderId(@Param("readerId") Long readerId);
    
    @Query("SELECT r FROM Rate r WHERE r.novelId = :novelId")
    List<Rate> findByNovelId(@Param("novelId") Long novelId);
    
    boolean existsByNovelIdAndReaderId(Long novelId, Long readerId);
}