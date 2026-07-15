package edu.tongji.adminservice.service;

import edu.tongji.adminservice.dto.NovelManagementRequest;
import edu.tongji.adminservice.dto.NovelManagementLogDto;
import edu.tongji.adminservice.entity.NovelManagement;
import edu.tongji.adminservice.entity.Management;
import edu.tongji.adminservice.repository.NovelManagementRepository;
import edu.tongji.adminservice.repository.ManagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小说管理服务类
 */
@Service
public class NovelManagementService {
    
    private final NovelManagementRepository novelManagementRepository;
    private final ManagementRepository managementRepository;
    
    public NovelManagementService(NovelManagementRepository novelManagementRepository, 
                                 ManagementRepository managementRepository) {
        this.novelManagementRepository = novelManagementRepository;
        this.managementRepository = managementRepository;
    }
    
    /**
     * 记录管理员对小说的管理操作（管理记录 + 桥表关联）
     * @param request 小说管理请求
     * @return 创建的小说管理记录
     */
    @Transactional
    public NovelManagement recordManagement(NovelManagementRequest request) {
        // 创建管理记录
        Management management = new Management();
        management.setManagerId(request.getManagerId());
        management.setResult(request.getResult());
        management.setTime(LocalDateTime.now());
        
        management = managementRepository.save(management);
        
        // 创建小说管理关联记录
        NovelManagement novelManagement = new NovelManagement();
        novelManagement.setManagementId(management.getManagementId());
        novelManagement.setNovelId(request.getNovelId());
        
        return novelManagementRepository.save(novelManagement);
    }
    
    /**
     * 记录管理员对小说的管理操作（管理记录 + 桥表关联）
     * @param managerId 管理员ID
     * @param result 操作结果说明，如"通过"或"封禁"
     * @param novelId 小说ID
     * @return 创建的小说管理记录
     */
    @Transactional
    public NovelManagement recordManagement(Long managerId, String result, Long novelId) {
        // 创建管理记录
        Management management = new Management();
        management.setManagerId(managerId);
        management.setResult(result);
        management.setTime(LocalDateTime.now());
        
        management = managementRepository.save(management);
        
        // 创建小说管理关联记录
        NovelManagement novelManagement = new NovelManagement();
        novelManagement.setManagementId(management.getManagementId());
        novelManagement.setNovelId(novelId);
        
        return novelManagementRepository.save(novelManagement);
    }
    
    /**
     * 获取指定小说的管理日志列表
     * @param novelId 小说ID
     * @return 小说管理日志DTO列表
     */
    public List<NovelManagementLogDto> getNovelManagementLogs(Long novelId) {
        if (novelId == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }
        
        List<NovelManagement> novelManagements = novelManagementRepository.findByNovelIdOrderByTimeDesc(novelId);
        
        return novelManagements.stream()
                .map(nm -> {
                    Management management = managementRepository.findById(nm.getManagementId())
                            .orElseThrow(() -> new RuntimeException("管理记录不存在: " + nm.getManagementId()));
                    
                    NovelManagementLogDto dto = new NovelManagementLogDto();
                    dto.setManagementId(nm.getManagementId());
                    dto.setNovelId(nm.getNovelId());
                    dto.setManagerId(management.getManagerId());
                    dto.setManagerName("管理员" + management.getManagerId()); // 暂时使用ID，后续可以调用用户服务获取名称
                    dto.setResult(management.getResult());
                    dto.setTime(management.getTime());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有小说的管理日志列表
     * @return 所有小说管理日志DTO列表
     */
    public List<NovelManagementLogDto> getAllNovelManagementLogs() {
        List<NovelManagement> novelManagements = novelManagementRepository.findAllOrderByTimeDesc();
        
        return novelManagements.stream()
                .map(nm -> {
                    Management management = managementRepository.findById(nm.getManagementId())
                            .orElseThrow(() -> new RuntimeException("管理记录不存在: " + nm.getManagementId()));
                    
                    NovelManagementLogDto dto = new NovelManagementLogDto();
                    dto.setManagementId(nm.getManagementId());
                    dto.setNovelId(nm.getNovelId());
                    dto.setManagerId(management.getManagerId());
                    dto.setManagerName("管理员" + management.getManagerId()); // 暂时使用ID，后续可以调用用户服务获取名称
                    dto.setResult(management.getResult());
                    dto.setTime(management.getTime());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
}