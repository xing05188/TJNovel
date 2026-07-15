package edu.tongji.adminservice.repository;

import edu.tongji.adminservice.entity.ReportManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 举报管理记录数据访问接口
 */
@Repository
public interface ReportManagementRepository extends JpaRepository<ReportManagement, Long> {
    
    /**
     * 根据举报ID获取举报管理记录列表
     * @param reportId 举报ID
     * @return 举报管理记录列表
     */
    List<ReportManagement> findByReportId(Long reportId);
    
    /**
     * 获取所有举报管理记录，按时间倒序排列
     * @return 所有举报管理记录列表
     */
    @Query("SELECT rm FROM ReportManagement rm, Management m WHERE rm.managementId = m.managementId ORDER BY m.time DESC")
    List<ReportManagement> findAllOrderByTimeDesc();
    
    /**
     * 根据举报ID获取举报管理记录，按时间倒序排列
     * @param reportId 举报ID
     * @return 举报管理记录列表
     */
    @Query("SELECT rm FROM ReportManagement rm, Management m WHERE rm.managementId = m.managementId AND rm.reportId = :reportId ORDER BY m.time DESC")
    List<ReportManagement> findByReportIdOrderByTimeDesc(@Param("reportId") Long reportId);
    
    /**
     * 根据读者ID获取举报管理记录，按时间倒序排列
     * 注意：这个查询需要与content-service的Report表关联，通过reportId获取对应的readerId
     * @param readerId 读者ID
     * @return 举报管理记录列表
     */
    @Query(value = "SELECT rm.* FROM REPORT_MANAGEMENT rm " +
                   "INNER JOIN MANAGEMENT m ON rm.MANAGEMENT_ID = m.MANAGEMENT_ID " +
                   "INNER JOIN REPORT r ON rm.REPORT_ID = r.REPORT_ID " +
                   "WHERE r.READER_ID = :readerId " +
                   "ORDER BY m.TIME DESC", nativeQuery = true)
    List<ReportManagement> findByReaderIdOrderByTimeDesc(@Param("readerId") Long readerId);
}