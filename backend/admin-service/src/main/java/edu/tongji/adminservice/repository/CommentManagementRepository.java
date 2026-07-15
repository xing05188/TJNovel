package edu.tongji.adminservice.repository;

import edu.tongji.adminservice.entity.CommentManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论管理记录数据访问接口
 */
@Repository
public interface CommentManagementRepository extends JpaRepository<CommentManagement, Long> {
    
    /**
     * 根据评论ID获取评论管理记录列表
     * @param commentId 评论ID
     * @return 评论管理记录列表
     */
    List<CommentManagement> findByCommentId(Long commentId);
    
    /**
     * 获取所有评论管理记录，按时间倒序排列
     * @return 所有评论管理记录列表
     */
    @Query("SELECT cm FROM CommentManagement cm, Management m WHERE cm.managementId = m.managementId ORDER BY m.time DESC")
    List<CommentManagement> findAllOrderByTimeDesc();
    
    /**
     * 根据评论ID获取评论管理记录，按时间倒序排列
     * @param commentId 评论ID
     * @return 评论管理记录列表
     */
    @Query("SELECT cm FROM CommentManagement cm, Management m WHERE cm.managementId = m.managementId AND cm.commentId = :commentId ORDER BY m.time DESC")
    List<CommentManagement> findByCommentIdOrderByTimeDesc(@Param("commentId") Long commentId);
}