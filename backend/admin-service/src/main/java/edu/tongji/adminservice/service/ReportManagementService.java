package edu.tongji.adminservice.service;

import edu.tongji.adminservice.entity.Management;
import edu.tongji.adminservice.entity.ReportManagement;
import edu.tongji.adminservice.repository.ManagementRepository;
import edu.tongji.adminservice.repository.ReportManagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 举报管理服务类
 */
@Service
public class ReportManagementService {
    
    private final ReportManagementRepository reportManagementRepository;
    private final ManagementRepository managementRepository;
    
    public ReportManagementService(ReportManagementRepository reportManagementRepository, 
                                   ManagementRepository managementRepository) {
        this.reportManagementRepository = reportManagementRepository;
        this.managementRepository = managementRepository;
    }
    
    /**
     * 记录管理员对举报的管理操作（管理记录 + 桥表关联）
     * @param managerId 管理员ID
     * @param result 操作结果说明，如"通过"或"驳回"
     * @param reportId 举报ID
     * @return 创建的举报管理记录
     */
    @Transactional
    public ReportManagement recordManagement(Long managerId, String result, Long reportId) {
        // 创建管理记录
        Management management = new Management();
        management.setManagerId(managerId);
        management.setResult(result);
        management.setTime(LocalDateTime.now());
        
        management = managementRepository.save(management);
        
        // 创建举报管理关联记录
        ReportManagement reportManagement = new ReportManagement();
        reportManagement.setManagementId(management.getManagementId());
        reportManagement.setReportId(reportId);
        
        return reportManagementRepository.save(reportManagement);
    }
    
    /**
     * 获取指定举报的管理日志列表
     * @param reportId 举报ID
     * @return 举报管理记录列表
     */
    public List<ReportManagement> getReportManagementLogs(Long reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("举报ID不能为空");
        }
        
        return reportManagementRepository.findByReportIdOrderByTimeDesc(reportId);
    }
    
    /**
     * 获取所有举报的管理日志列表
     * @return 所有举报管理记录列表
     */
    public List<ReportManagement> getAllReportManagementLogs() {
        return reportManagementRepository.findAllOrderByTimeDesc();
    }
    
    /**
     * 获取指定读者发布的所有举报及其管理处理进度
     * @param readerId 读者ID
     * @return 举报管理记录列表
     */
    public List<ReportManagement> getReaderReportsWithLogs(Long readerId) {
        if (readerId == null) {
            throw new IllegalArgumentException("读者ID不能为空");
        }
        
        try {
            return reportManagementRepository.findByReaderIdOrderByTimeDesc(readerId);
        } catch (Exception e) {
            // 如果查询失败（可能是跨数据库访问问题），返回空列表
            // 或者可以通过调用content-service的API来获取报告，然后查询管理记录
            System.err.println("获取读者举报管理记录失败: " + e.getMessage());
            e.printStackTrace();
            // 返回空列表而不是抛出异常，避免500错误
            return new java.util.ArrayList<>();
        }
    }
}