package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Comment;
import edu.tongji.contentservice.entity.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问接口
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 根据小说ID和章节ID获取所有通过审核的评论
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 评论列表
     */
    List<Comment> findByNovelIdAndChapterIdAndStatus(Long novelId, Long chapterId, String status);
    
    /**
     * 根据小说ID获取所有通过审核的评论
     * @param novelId 小说ID
     * @return 评论列表
     */
    List<Comment> findByNovelIdAndStatus(Long novelId, String status);
    
    /**
     * 根据评论ID获取所有子评论ID（递归查询）
     * @param commentId 评论ID
     * @return 子评论ID列表
     */
    @Query(value = "WITH RECURSIVE CommentTree AS (" +
           "  SELECT COMMENT_ID FROM COMMENT_REPLY WHERE PRE_COM_ID = :commentId " +
           "  UNION ALL " +
           "  SELECT cr.COMMENT_ID FROM COMMENT_REPLY cr " +
           "  JOIN CommentTree ct ON cr.PRE_COM_ID = ct.COMMENT_ID" +
           ") SELECT COMMENT_ID FROM CommentTree", nativeQuery = true)
    List<Long> findChildCommentIdsRecursive(@Param("commentId") Long commentId);
    
    /**
     * 根据评论ID列表删除评论
     * @param commentIds 评论ID列表
     */
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.commentId IN :commentIds")
    void deleteByCommentIds(@Param("commentIds") List<Long> commentIds);
    
    /**
     * 根据评论ID列表删除评论回复关系
     * @param commentIds 评论ID列表
     */
    @Modifying
    @Query("DELETE FROM CommentReply cr WHERE cr.commentId IN :commentIds OR cr.preComId IN :commentIds")
    void deleteCommentReplyByCommentIds(@Param("commentIds") List<Long> commentIds);
    
    /**
     * 根据小说ID统计通过审核的评论数量
     * @param novelId 小说ID
     * @return 评论数量
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.novelId = :novelId AND c.status = '通过'")
    Long countByNovelIdAndStatus(@Param("novelId") Long novelId);
    
    /**
     * 根据小说ID和章节ID获取前N个点赞最多的通过审核的评论
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param status 状态
     * @return 评论列表
     */
    @Query("SELECT c FROM Comment c WHERE c.novelId = :novelId AND c.chapterId = :chapterId AND c.status = :status ORDER BY c.likes DESC")
    List<Comment> findTopLikedCommentsByChapter(@Param("novelId") Long novelId, @Param("chapterId") Long chapterId, @Param("status") String status);
    
    /**
     * 根据小说ID获取前N个点赞最多的通过审核的评论
     * @param novelId 小说ID
     * @param status 状态
     * @return 评论列表
     */
    @Query("SELECT c FROM Comment c WHERE c.novelId = :novelId AND c.status = :status ORDER BY c.likes DESC")
    List<Comment> findTopLikedCommentsByNovel(@Param("novelId") Long novelId, @Param("status") String status);
    
    /**
     * 根据读者ID获取所有评论
     * @param readerId 读者ID
     * @return 评论列表
     */
    List<Comment> findByReaderId(Long readerId);
}