package edu.tongji.adminservice.service;

import edu.tongji.adminservice.dto.ChapterManagementRequest;
import edu.tongji.adminservice.dto.ChapterManagementLogDto;
import edu.tongji.adminservice.entity.ChapterManagement;
import edu.tongji.adminservice.entity.Management;
import edu.tongji.adminservice.repository.ChapterManagementRepository;
import edu.tongji.adminservice.repository.ManagementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 章节管理服务类
 */
@Service
public class ChapterManagementService {
    
    private final ChapterManagementRepository chapterManagementRepository;
    private final ManagementRepository managementRepository;
    
    public ChapterManagementService(ChapterManagementRepository chapterManagementRepository, 
                                   ManagementRepository managementRepository) {
        this.chapterManagementRepository = chapterManagementRepository;
        this.managementRepository = managementRepository;
    }
    
    /**
     * 记录管理员对章节的管理操作（管理记录 + 桥表关联）
     * @param managerId 管理员ID
     * @param result 操作结果说明，如"通过"或"封禁"
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 创建的章节管理记录
     */
    @Transactional
    public ChapterManagement recordManagement(ChapterManagementRequest request) {
        // 创建管理记录
        Management management = new Management();
        management.setManagerId(request.getManagerId());
        management.setResult(request.getResult());
        management.setTime(LocalDateTime.now());
        
        management = managementRepository.save(management);
        
        // 创建章节管理关联记录
        ChapterManagement chapterManagement = new ChapterManagement();
        chapterManagement.setManagementId(management.getManagementId());
        chapterManagement.setNovelId(request.getNovelId());
        chapterManagement.setChapterId(request.getChapterId());
        
        return chapterManagementRepository.save(chapterManagement);
    }
    
    @Transactional
    public ChapterManagement recordManagement(Long managerId, String result, Long novelId, Long chapterId) {
        // 创建管理记录
        Management management = new Management();
        management.setManagerId(managerId);
        management.setResult(result);
        management.setTime(LocalDateTime.now());
        
        management = managementRepository.save(management);
        
        // 创建章节管理关联记录
        ChapterManagement chapterManagement = new ChapterManagement();
        chapterManagement.setManagementId(management.getManagementId());
        chapterManagement.setNovelId(novelId);
        chapterManagement.setChapterId(chapterId);
        
        return chapterManagementRepository.save(chapterManagement);
    }
    
    /**
     * 获取指定章节的管理日志列表
     * @param chapterId 章节ID
     * @param novelId 小说ID
     * @return 章节管理日志DTO列表
     */
    public List<ChapterManagementLogDto> getChapterManagementLogs(Long chapterId, Long novelId) {
        if (chapterId == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        
        List<ChapterManagement> chapterManagements;
        
        // 如果提供了小说ID，则同时根据章节ID和小说ID查询
        if (novelId != null) {
            chapterManagements = chapterManagementRepository.findByChapterIdAndNovelIdOrderByTimeDesc(chapterId, novelId);
        } else {
            // 只根据章节ID查询
            chapterManagements = chapterManagementRepository.findByChapterIdOrderByTimeDesc(chapterId);
        }
        
        return chapterManagements.stream()
                .map(cm -> {
                    Management management = managementRepository.findById(cm.getManagementId())
                            .orElseThrow(() -> new RuntimeException("管理记录不存在: " + cm.getManagementId()));
                    
                    ChapterManagementLogDto dto = new ChapterManagementLogDto();
                    dto.setManagementId(cm.getManagementId());
                    dto.setNovelId(cm.getNovelId());
                    dto.setChapterId(cm.getChapterId());
                    dto.setManagerId(management.getManagerId());
                    dto.setManagerName("管理员" + management.getManagerId()); // 暂时使用ID，后续可以调用用户服务获取名称
                    dto.setResult(management.getResult());
                    dto.setTime(management.getTime());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有章节的管理日志列表
     * @return 所有章节管理日志DTO列表
     */
    public List<ChapterManagementLogDto> getAllChapterManagementLogs() {
        List<ChapterManagement> chapterManagements = chapterManagementRepository.findAllOrderByTimeDesc();
        
        return chapterManagements.stream()
                .map(cm -> {
                    Management management = managementRepository.findById(cm.getManagementId())
                            .orElseThrow(() -> new RuntimeException("管理记录不存在: " + cm.getManagementId()));
                    
                    ChapterManagementLogDto dto = new ChapterManagementLogDto();
                    dto.setManagementId(cm.getManagementId());
                    dto.setNovelId(cm.getNovelId());
                    dto.setChapterId(cm.getChapterId());
                    dto.setManagerId(management.getManagerId());
                    dto.setManagerName("管理员" + management.getManagerId()); // 暂时使用ID，后续可以调用用户服务获取名称
                    dto.setResult(management.getResult());
                    dto.setTime(management.getTime());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
}