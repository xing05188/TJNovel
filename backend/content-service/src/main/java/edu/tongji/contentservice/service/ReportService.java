package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Report;
import edu.tongji.contentservice.repository.CommentRepository;
import edu.tongji.contentservice.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private AdminServiceClient adminServiceClient;
    
    /**
     * 举报评论
     * @param commentId 评论ID
     * @param readerId 读者ID
     * @param reason 举报原因
     * @return 举报记录
     */
    @Transactional
    public Report reportComment(Long commentId, Long readerId, String reason) {
        // 检查评论是否存在
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查是否已经举报过
        Optional<Report> existingReport = reportRepository.findByCommentIdAndReaderId(commentId, readerId);
        if (existingReport.isPresent()) {
            throw new RuntimeException("您已经举报过该评论");
        }
        
        // 创建新的举报记录
        Report report = new Report();
        report.setCommentId(commentId);
        report.setReaderId(readerId);
        report.setReason(reason);
        report.setReportTime(LocalDateTime.now());
        report.setProgress("未处理");
        
        return reportRepository.save(report);
    }
    
    /**
     * 处理举报
     * @param reportId 举报ID
     * @param progress 处理状态
     * @param managerId 管理员ID
     * @param result 处理结果
     * @return 更新后的举报记录
     */
    @Transactional
    public Report processReport(Long reportId, String progress, Long managerId, String result) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            throw new RuntimeException("举报记录不存在");
        }
        
        Report report = reportOpt.get();
        
        // 验证progress值
        if (!"成功".equals(progress) && !"失败".equals(progress) && !"未处理".equals(progress)) {
            throw new RuntimeException("处理状态值无效，必须是'成功'、'失败'或'未处理'");
        }
        
        report.setProgress(progress);
        
        // 保存更新后的举报记录
        report = reportRepository.save(report);
        
        // 记录管理操作到admin-service
        if (managerId != null) {
            try {
                adminServiceClient.recordReportManagement(reportId, managerId, result != null ? result : progress)
                    .subscribe(success -> {
                        if (success) {
                            logger.info("成功记录举报管理操作: reportId={}, managerId={}, result={}", 
                                       reportId, managerId, result);
                        } else {
                            logger.warn("记录举报管理操作失败: reportId={}, managerId={}", reportId, managerId);
                        }
                    });
            } catch (Exception e) {
                logger.error("调用admin-service记录举报管理操作时发生异常: {}", e.getMessage(), e);
                // 不抛出异常，避免影响主要业务流程
            }
        }
        
        return report;
    }
    
    /**
     * 获取所有举报
     * @return 举报列表
     */
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    
    /**
     * 根据ID获取举报
     * @param id 举报ID
     * @return 举报记录
     */
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }
    
    /**
     * 创建举报
     * @param report 举报对象
     * @return 创建的举报记录
     */
    @Transactional
    public Report createReport(Report report) {
        // 检查评论是否存在
        if (report.getCommentId() != null && !commentRepository.existsById(report.getCommentId())) {
            throw new RuntimeException("评论不存在");
        }
        
        // 设置默认值
        if (report.getReportTime() == null) {
            report.setReportTime(LocalDateTime.now());
        }
        if (report.getProgress() == null) {
            report.setProgress("未处理");
        }
        
        return reportRepository.save(report);
    }
    
    /**
     * 更新举报
     * @param id 举报ID
     * @param report 更新的举报对象
     * @return 更新后的举报记录
     */
    @Transactional
    public Report updateReport(Long id, Report report) {
        Optional<Report> existingReportOpt = reportRepository.findById(id);
        if (existingReportOpt.isEmpty()) {
            throw new RuntimeException("举报记录不存在");
        }
        
        Report existingReport = existingReportOpt.get();
        
        // 更新字段
        if (report.getReason() != null) {
            existingReport.setReason(report.getReason());
        }
        if (report.getProgress() != null) {
            // 验证progress值
            if (!"成功".equals(report.getProgress()) && !"失败".equals(report.getProgress()) && !"未处理".equals(report.getProgress())) {
                throw new RuntimeException("处理状态值无效，必须是'成功'、'失败'或'未处理'");
            }
            existingReport.setProgress(report.getProgress());
        }
        if (report.getCommentId() != null) {
            // 检查评论是否存在
            if (!commentRepository.existsById(report.getCommentId())) {
                throw new RuntimeException("评论不存在");
            }
            existingReport.setCommentId(report.getCommentId());
        }
        if (report.getReaderId() != null) {
            existingReport.setReaderId(report.getReaderId());
        }
        
        return reportRepository.save(existingReport);
    }
    
    /**
     * 删除举报
     * @param id 举报ID
     */
    @Transactional
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("举报记录不存在");
        }
        reportRepository.deleteById(id);
    }
    
    /**
     * 获取待处理举报数量
     * @return 待处理举报数量
     */
    public Long getPendingReportsCount() {
        return reportRepository.countByProgress("未处理");
    }
}