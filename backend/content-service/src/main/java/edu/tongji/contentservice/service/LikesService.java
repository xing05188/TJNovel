package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Likes;
import edu.tongji.contentservice.repository.LikesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 点赞服务类
 */
@Service
public class LikesService {
    
    private static final Logger logger = LoggerFactory.getLogger(LikesService.class);
    
    private final LikesRepository likesRepository;
    
    @Autowired
    public LikesService(LikesRepository likesRepository) {
        this.likesRepository = likesRepository;
    }
    
    /**
     * 给指定评论点赞
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 点赞记录
     */
    @Transactional
    public Likes likeComment(Long commentId, Long readerId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        if (readerId == null) {
            throw new IllegalArgumentException("读者ID不能为空");
        }
        
        // 检查是否已经点赞
        if (likesRepository.existsByCommentIdAndReaderId(commentId, readerId)) {
            throw new IllegalStateException("读者已经对该评论点赞");
        }
        
        Likes like = new Likes(commentId, readerId);
        Likes savedLike = likesRepository.save(like);
        
        logger.info("读者 {} 给评论 {} 点赞成功", readerId, commentId);
        
        return savedLike;
    }
    
    /**
     * 取消点赞某条评论
     * @param commentId 评论ID
     * @param readerId 读者ID
     */
    @Transactional
    public void unlikeComment(Long commentId, Long readerId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        if (readerId == null) {
            throw new IllegalArgumentException("读者ID不能为空");
        }
        
        // 检查是否已经点赞
        Likes like = likesRepository.findByCommentIdAndReaderId(commentId, readerId);
        if (like == null) {
            throw new IllegalStateException("读者未对该评论点赞");
        }
        
        likesRepository.delete(like);
        
        logger.info("读者 {} 取消对评论 {} 的点赞成功", readerId, commentId);
    }
    
    /**
     * 检查读者是否已点赞某条评论
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @return 是否已点赞
     */
    public boolean isReaderLikedComment(Long commentId, Long readerId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        if (readerId == null) {
            throw new IllegalArgumentException("读者ID不能为空");
        }
        
        return likesRepository.existsByCommentIdAndReaderId(commentId, readerId);
    }
    
    /**
     * 获取某条评论的点赞数量
     * @param commentId 评论ID
     * @return 点赞数量
     */
    public Long getLikesCountByCommentId(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        return likesRepository.countByCommentId(commentId);
    }
    
    /**
     * 获取所有点赞记录（分页，页码从1开始）
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 点赞记录分页结果
     */
    public Page<Likes> getAllLikes(int page, int pageSize) {
        // 将从1开始的页码转换为从0开始的页码
        int zeroBasedPage = page > 0 ? page - 1 : 0;
        logger.info("获取所有点赞记录，页码: {}, 页大小: {}", page, pageSize);
        
        return likesRepository.findAll(org.springframework.data.domain.PageRequest.of(zeroBasedPage, pageSize));
    }
    
}