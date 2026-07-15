package edu.tongji.adminservice.service;

import edu.tongji.adminservice.entity.Management;
import edu.tongji.adminservice.repository.ManagementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 管理服务类
 */
@Service
public class ManagementService {
    
    private final ManagementRepository managementRepository;
    
    public ManagementService(ManagementRepository managementRepository) {
        this.managementRepository = managementRepository;
    }
    
    /**
     * 获取所有管理记录
     * @return 所有管理记录列表
     */
    public List<Management> getAllManagementRecords() {
        return managementRepository.findAll();
    }
    
    /**
     * 根据ID获取管理记录
     * @param id 管理记录ID
     * @return 管理记录
     */
    public Optional<Management> getManagementById(Long id) {
        return managementRepository.findById(id);
    }
    
    /**
     * 创建管理记录
     * @param management 管理记录
     * @return 创建的管理记录
     */
    public Management createManagement(Management management) {
        return managementRepository.save(management);
    }
    
    /**
     * 更新管理记录
     * @param id 管理记录ID
     * @param management 更新的管理记录
     * @return 更新后的管理记录
     */
    public Optional<Management> updateManagement(Long id, Management management) {
        if (!managementRepository.existsById(id)) {
            return Optional.empty();
        }
        
        management.setManagementId(id);
        return Optional.of(managementRepository.save(management));
    }
    
    /**
     * 删除管理记录
     * @param id 管理记录ID
     * @return 是否删除成功
     */
    public boolean deleteManagement(Long id) {
        if (!managementRepository.existsById(id)) {
            return false;
        }
        
        managementRepository.deleteById(id);
        return true;
    }
    
    /**
     * 根据管理员ID筛选管理记录
     * @param managerId 管理员ID
     * @return 管理记录列表
     */
    public List<Management> getManagementByManagerId(Long managerId) {
        return managementRepository.findByManagerId(managerId);
    }
}