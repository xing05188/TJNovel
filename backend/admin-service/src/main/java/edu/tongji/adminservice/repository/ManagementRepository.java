package edu.tongji.adminservice.repository;

import edu.tongji.adminservice.entity.Management;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 管理记录数据访问接口
 */
@Repository
public interface ManagementRepository extends JpaRepository<Management, Long> {
    
    /**
     * 根据管理员ID获取管理记录列表
     * @param managerId 管理员ID
     * @return 管理记录列表
     */
    List<Management> findByManagerId(Long managerId);
}