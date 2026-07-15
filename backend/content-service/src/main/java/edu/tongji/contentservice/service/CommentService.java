package edu.tongji.contentservice.service;

import edu.tongji.contentservice.dto.CommentCreateRequest;
import edu.tongji.contentservice.dto.CommentWithRepliesResponseDto;
import edu.tongji.contentservice.entity.Comment;
import edu.tongji.contentservice.entity.CommentReply;
import edu.tongji.contentservice.repository.CommentRepository;
import edu.tongji.contentservice.repository.CommentReplyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 评论服务类
 */
@Service
public class CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    
    private final CommentRepository commentRepository;
    private final CommentReplyRepository commentReplyRepository;
    private final AdminServiceClient adminServiceClient;
    
    @Autowired
    public CommentService(CommentRepository commentRepository, 
                         CommentReplyRepository commentReplyRepository,
                         AdminServiceClient adminServiceClient) {
        this.commentRepository = commentRepository;
        this.commentReplyRepository = commentReplyRepository;
        this.adminServiceClient = adminServiceClient;
    }
    
    /**
     * 设置评论状态
     * @param commentId 评论ID
     * @param status 状态
     * @param managerId 管理员ID
     * @param result 操作结果
     * @return 更新后的评论
     */
    @Transactional
    public Comment setCommentStatus(Long commentId, String status, Long managerId, String result) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在，ID: " + commentId));
        
        comment.setStatus(status);
        
        // 保存评论状态更新
        Comment updatedComment = commentRepository.save(comment);
        
        // 记录管理操作到admin-service
        if (managerId != null) {
            try {
                adminServiceClient.recordCommentManagement(commentId, managerId, result)
                    .subscribe(success -> {
                        if (success) {
                            logger.info("成功记录评论管理操作: commentId={}, managerId={}", commentId, managerId);
                        } else {
                            logger.warn("记录评论管理操作失败: commentId={}, managerId={}", commentId, managerId);
                        }
                    });
            } catch (Exception e) {
                logger.error("调用admin-service记录评论管理操作时发生异常: {}", e.getMessage(), e);
                // 不抛出异常，避免影响主要业务流程
            }
        }
        
        return updatedComment;
    }
    
    /**
     * 获取某章节下所有通过审核的评论
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 评论列表
     */
    public List<Comment> getApprovedCommentsByChapter(Long novelId, Long chapterId) {
        if (novelId == null || chapterId == null) {
            throw new IllegalArgumentException("小说ID和章节ID不能为空");
        }
        
        return commentRepository.findByNovelIdAndChapterIdAndStatus(novelId, chapterId, "通过");
    }
    
    /**
     * 获取某小说下所有通过审核的评论
     * @param novelId 小说ID
     * @return 评论列表
     */
    public List<Comment> getApprovedCommentsByNovel(Long novelId) {
        if (novelId == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }
        
        return commentRepository.findByNovelIdAndStatus(novelId, "通过");
    }
    
    /**
     * 获取某小说下通过审核的评论数量
     * @param novelId 小说ID
     * @return 评论数量
     */
    public Long getCommentCountByNovelId(Long novelId) {
        if (novelId == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }
        
        Long count = commentRepository.countByNovelIdAndStatus(novelId);
        return count != null ? count : 0L;
    }
    
    /**
     * 递归删除评论及其所有子评论
     * @param commentId 评论ID
     */
    @Transactional
    public void deleteCommentRecursive(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        // 获取所有子评论ID（递归查询）
        List<Long> childCommentIds = commentRepository.findChildCommentIdsRecursive(commentId);
        
        // 将当前评论ID也加入要删除的列表
        childCommentIds.add(commentId);
        
        // 先删除评论回复关系
        commentRepository.deleteCommentReplyByCommentIds(childCommentIds);
        
        // 再删除评论
        commentRepository.deleteByCommentIds(childCommentIds);
    }
    
    /**
     * 获取所有评论
     * @return 评论列表
     */
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    
    /**
     * 根据ID获取评论
     * @param id 评论ID
     * @return 评论
     */
    public Comment getCommentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在，ID: " + id));
    }
    
    /**
     * 创建评论
     * @param comment 评论信息
     * @return 创建的评论
     */
    @Transactional
    public Comment createComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("评论信息不能为空");
        }
        
        // 设置创建时间
        comment.setCreateTime(new Date());
        
        // 如果没有设置状态，默认为"通过"
        if (comment.getStatus() == null || comment.getStatus().isEmpty()) {
            comment.setStatus("通过");
        }
        
        // 如果没有设置点赞数，默认为0
        if (comment.getLikes() == null) {
            comment.setLikes(0);
        }
        
        return commentRepository.save(comment);
    }
    
    /**
     * 创建评论
     * @param request 创建评论请求
     * @return 创建的评论
     */
    @Transactional
    public Comment createComment(CommentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("评论信息不能为空");
        }
        
        // 创建评论对象
        Comment comment = new Comment();
        comment.setReaderId(request.getReaderId());
        comment.setNovelId(request.getNovelId());
        comment.setChapterId(request.getChapterId());
        comment.setTitle(request.getTitle());
        comment.setContent(request.getContent());
        comment.setStatus(request.getStatus());
        
        // 设置创建时间
        comment.setCreateTime(new Date());
        
        // 如果没有设置状态，默认为"通过"
        if (comment.getStatus() == null || comment.getStatus().isEmpty()) {
            comment.setStatus("通过");
        }
        
        // 设置点赞数为0
        comment.setLikes(0);
        
        return commentRepository.save(comment);
    }
    
    /**
     * 更新评论
     * @param id 评论ID
     * @param comment 更新的评论信息
     * @return 更新后的评论
     */
    @Transactional
    public Comment updateComment(Long id, Comment comment) {
        if (id == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        if (comment == null) {
            throw new IllegalArgumentException("评论信息不能为空");
        }
        
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在，ID: " + id));
        
        // 更新字段
        if (comment.getTitle() != null) {
            existingComment.setTitle(comment.getTitle());
        }
        if (comment.getContent() != null) {
            existingComment.setContent(comment.getContent());
        }
        if (comment.getStatus() != null) {
            existingComment.setStatus(comment.getStatus());
        }
        if (comment.getLikes() != null) {
            existingComment.setLikes(comment.getLikes());
        }
        
        return commentRepository.save(existingComment);
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     */
    @Transactional
    public void deleteComment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("评论不存在，ID: " + id);
        }
        
        commentRepository.deleteById(id);
    }
    
    /**
     * 获取指定章节前N个点赞最多的通过审核的评论
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param topN 前N名
     * @return 评论列表
     */
    public List<Comment> getTopLikedCommentsByChapter(Long novelId, Long chapterId, Integer topN) {
        if (novelId == null || chapterId == null) {
            throw new IllegalArgumentException("小说ID和章节ID不能为空");
        }
        if (topN == null || topN <= 0) {
            topN = 10; // 默认返回前10个
        }
        
        List<Comment> comments = commentRepository.findTopLikedCommentsByChapter(novelId, chapterId, "通过");
        
        // 限制返回数量
        if (comments.size() > topN) {
            return comments.subList(0, topN);
        }
        
        return comments;
    }
    
    /**
     * 获取指定小说前N个点赞最多的通过审核的评论
     * @param novelId 小说ID
     * @param topN 前N名
     * @return 评论列表
     */
    public List<Comment> getTopLikedCommentsByNovel(Long novelId, Integer topN) {
        if (novelId == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }
        if (topN == null || topN <= 0) {
            topN = 10; // 默认返回前10个
        }
        
        List<Comment> comments = commentRepository.findTopLikedCommentsByNovel(novelId, "通过");
        
        // 限制返回数量
        if (comments.size() > topN) {
            return comments.subList(0, topN);
        }
        
        return comments;
    }
    
    /**
     * 获取指定读者发布的所有评论及其子评论
     * @param readerId 读者ID
     * @return 评论列表（包含父评论和子评论）
     */
    public List<Comment> getCommentsByReaderId(Long readerId) {
        if (readerId == null) {
            throw new IllegalArgumentException("读者ID不能为空");
        }
        
        // 获取该读者发布的所有评论
        List<Comment> comments = commentRepository.findByReaderId(readerId);
        
        return comments;
    }
    
    /**
     * 获取指定读者发布的所有评论及其子评论（包含父子关系）
     * @param readerId 读者ID
     * @return 评论列表（包含父评论和子评论的结构化数据）
     */
    public List<CommentWithRepliesResponseDto> getCommentsWithRepliesByReaderId(Long readerId) {
        if (readerId == null) {
            throw new IllegalArgumentException("读者ID不能为空");
        }
        
        // 获取该读者发布的所有评论
        List<Comment> readerComments = commentRepository.findByReaderId(readerId);
        
        // 构建包含父子关系的结构
        List<CommentWithRepliesResponseDto> result = new ArrayList<>();
        
        // 用于存储已经处理过的评论ID
        Set<Long> processedCommentIds = new HashSet<>();
        
        for (Comment comment : readerComments) {
            if (processedCommentIds.contains(comment.getCommentId())) {
                continue; // 已经处理过
            }
            
            // 检查这个评论是否是回复（有父评论）
            CommentReply reply = commentReplyRepository.findByCommentId(comment.getCommentId());
            
            if (reply != null && reply.getPreComId() != null) {
                // 这是子评论，需要找到它的父评论
                Optional<Comment> parentCommentOpt = commentRepository.findById(reply.getPreComId());
                if (parentCommentOpt.isPresent()) {
                    Comment parentComment = parentCommentOpt.get();
                    
                    // 检查父评论是否已经存在于结果中
                    CommentWithRepliesResponseDto existingDto = null;
                    for (CommentWithRepliesResponseDto dto : result) {
                        if (dto.getParentComment().getCommentId().equals(parentComment.getCommentId())) {
                            existingDto = dto;
                            break;
                        }
                    }
                    
                    if (existingDto != null) {
                        // 父评论已存在，添加子评论
                        if (existingDto.getChildComments() == null) {
                            existingDto.setChildComments(new ArrayList<>());
                        }
                        existingDto.getChildComments().add(comment);
                    } else {
                        // 父评论不存在，创建新的DTO
                        CommentWithRepliesResponseDto dto = new CommentWithRepliesResponseDto();
                        dto.setParentComment(parentComment);
                        List<Comment> childComments = new ArrayList<>();
                        childComments.add(comment);
                        dto.setChildComments(childComments);
                        result.add(dto);
                    }
                    processedCommentIds.add(comment.getCommentId());
                }
            } else {
                // 这是一级评论（父评论）
                CommentWithRepliesResponseDto dto = new CommentWithRepliesResponseDto();
                dto.setParentComment(comment);
                
                // 获取所有子评论（包括该读者和其他读者的回复）
                List<CommentReply> childReplies = commentReplyRepository.findByPreComId(comment.getCommentId());
                List<Comment> childComments = new ArrayList<>();
                for (CommentReply childReply : childReplies) {
                    Optional<Comment> childComment = commentRepository.findById(childReply.getCommentId());
                    childComment.ifPresent(childComments::add);
                }
                dto.setChildComments(childComments);
                
                result.add(dto);
                processedCommentIds.add(comment.getCommentId());
            }
        }
        
        return result;
    }
}