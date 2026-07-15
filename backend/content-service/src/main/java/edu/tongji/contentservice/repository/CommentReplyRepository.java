package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.CommentReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论回复数据访问接口
 */
@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {
    
    /**
     * 根据评论ID获取评论回复信息
     * @param commentId 评论ID
     * @return 评论回复信息
     */
    CommentReply findByCommentId(Long commentId);
    
    /**
     * 根据父评论ID获取所有子评论ID
     * @param preComId 父评论ID
     * @return 子评论ID列表
     */
    List<CommentReply> findByPreComId(Long preComId);
    
    /**
     * 获取所有评论回复联系集（分页）
     * @param pageable 分页参数
     * @return 评论回复联系集分页结果
     */
    Page<CommentReply> findAll(Pageable pageable);
}