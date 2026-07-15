package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.CommentReply;
import edu.tongji.contentservice.repository.CommentReplyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论回复服务类
 */
@Service
public class CommentReplyService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentReplyService.class);
    
    private final CommentReplyRepository commentReplyRepository;
    
    @Autowired
    public CommentReplyService(CommentReplyRepository commentReplyRepository) {
        this.commentReplyRepository = commentReplyRepository;
    }
    
    /**
     * 添加一条评论回复
     * @param commentReply 评论回复信息
     * @return 保存后的评论回复信息
     */
    @Transactional
    public CommentReply addCommentReply(CommentReply commentReply) {
        if (commentReply == null) {
            throw new IllegalArgumentException("评论回复信息不能为空");
        }
        
        if (commentReply.getCommentId() == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        // 检查评论ID是否已存在
        if (commentReplyRepository.existsById(commentReply.getCommentId())) {
            throw new IllegalArgumentException("评论ID已存在: " + commentReply.getCommentId());
        }
        
        logger.info("添加评论回复: commentId={}, preComId={}, commentLevel={}", 
                   commentReply.getCommentId(), commentReply.getPreComId(), commentReply.getCommentLevel());
        
        return commentReplyRepository.save(commentReply);
    }
    
    /**
     * 根据评论ID获取它的回复关系
     * @param commentId 评论ID
     * @return 评论回复信息
     */
    public CommentReply getCommentReplyByCommentId(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        logger.info("根据评论ID获取回复关系: commentId={}", commentId);
        
        return commentReplyRepository.findByCommentId(commentId);
    }
    
    /**
     * 获取某条评论下的所有直接回复
     * @param parentId 父评论ID
     * @return 直接回复列表
     */
    public List<CommentReply> getDirectRepliesByParentId(Long parentId) {
        if (parentId == null) {
            throw new IllegalArgumentException("父评论ID不能为空");
        }
        
        logger.info("获取父评论下的所有直接回复: parentId={}", parentId);
        
        return commentReplyRepository.findByPreComId(parentId);
    }
    
    /**
     * 获取所有评论回复联系集（分页，页码从1开始）
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 评论回复联系集分页结果
     */
    public Page<CommentReply> getAllCommentReplies(int page, int pageSize) {
        // 将从1开始的页码转换为从0开始的页码
        int zeroBasedPage = page > 0 ? page - 1 : 0;
        logger.info("获取所有评论回复联系集，页码: {}, 页大小: {}", page, pageSize);
        
        return commentReplyRepository.findAll(org.springframework.data.domain.PageRequest.of(zeroBasedPage, pageSize));
    }
}