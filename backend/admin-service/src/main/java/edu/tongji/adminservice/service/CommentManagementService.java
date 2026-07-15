package edu.tongji.adminservice.service;

import edu.tongji.adminservice.dto.CommentManagementRequest;
import edu.tongji.adminservice.entity.CommentManagement;
import edu.tongji.adminservice.entity.Management;
import edu.tongji.adminservice.repository.CommentManagementRepository;
import edu.tongji.adminservice.repository.ManagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论管理服务类
 */
@Service
public class CommentManagementService {
    
    private final CommentManagementRepository commentManagementRepository;
    private final ManagementRepository managementRepository;
    
    public CommentManagementService(CommentManagementRepository commentManagementRepository, 
                                   ManagementRepository managementRepository) {
        this.commentManagementRepository = commentManagementRepository;
        this.managementRepository = managementRepository;
    }
    
    /**
     * 记录管理员对评论的管理操作（管理记录 + 桥表关联）
     * @param request 评论管理请求
     * @return 创建的评论管理记录
     */
    @Transactional
    public CommentManagement recordManagement(CommentManagementRequest request) {
        // 创建管理记录
        Management management = new Management();
        management.setManagerId(request.getManagerId());
        management.setResult(request.getResult());
        management.setTime(LocalDateTime.now());
        
        management = managementRepository.save(management);
        
        // 创建评论管理关联记录
        CommentManagement commentManagement = new CommentManagement();
        commentManagement.setManagementId(management.getManagementId());
        commentManagement.setCommentId(request.getCommentId());
        
        return commentManagementRepository.save(commentManagement);
    }
    
    /**
     * 获取指定评论的管理日志列表
     * @param commentId 评论ID
     * @return 评论管理记录列表
     */
    public List<CommentManagement> getCommentManagementLogs(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论ID不能为空");
        }
        
        return commentManagementRepository.findByCommentIdOrderByTimeDesc(commentId);
    }
    
    /**
     * 获取所有评论的管理日志列表
     * @return 所有评论管理记录列表
     */
    public List<CommentManagement> getAllCommentManagementLogs() {
        return commentManagementRepository.findAllOrderByTimeDesc();
    }
}