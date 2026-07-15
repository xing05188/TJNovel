package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * 根据评论ID和读者ID查找举报记录
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 举报记录
     */
    Optional<Report> findByCommentIdAndReaderId(Long commentId, Long readerId);
    
    /**
     * 根据处理状态查找举报记录
     * @param progress 处理状态
     * @return 举报记录列表
     */
    List<Report> findByProgress(String progress);
    
    /**
     * 根据处理状态查找举报记录数量
     * @param progress 处理状态
     * @return 举报记录数量
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.progress = :progress")
    Long countByProgress(@Param("progress") String progress);
}