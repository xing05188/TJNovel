package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Likes;
import edu.tongji.contentservice.entity.LikesId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 点赞数据访问接口
 */
@Repository
public interface LikesRepository extends JpaRepository<Likes, LikesId> {
    
    /**
     * 根据评论ID和读者ID查找点赞记录
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 点赞记录
     */
    Likes findByCommentIdAndReaderId(Long commentId, Long readerId);
    
    /**
     * 统计某条评论的点赞数量
     * @param commentId 评论ID
     * @return 点赞数量
     */
    @Query("SELECT COUNT(l) FROM Likes l WHERE l.commentId = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 检查读者是否已点赞某条评论
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 是否已点赞
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Likes l WHERE l.commentId = :commentId AND l.readerId = :readerId")
    boolean existsByCommentIdAndReaderId(@Param("commentId") Long commentId, @Param("readerId") Long readerId);
    
    /**
     * 获取所有点赞记录（分页）
     * @param pageable 分页参数
     * @return 点赞记录分页结果
     */
    Page<Likes> findAll(Pageable pageable);
}