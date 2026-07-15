package edu.tongji.adminservice.repository;

import edu.tongji.adminservice.entity.NovelManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 小说管理记录数据访问接口
 */
@Repository
public interface NovelManagementRepository extends JpaRepository<NovelManagement, Long> {
    
    /**
     * 根据小说ID获取小说管理记录列表
     * @param novelId 小说ID
     * @return 小说管理记录列表
     */
    List<NovelManagement> findByNovelId(Long novelId);
    
    /**
     * 获取所有小说管理记录，按时间倒序排列
     * @return 所有小说管理记录列表
     */
    @Query("SELECT nm FROM NovelManagement nm, Management m WHERE nm.managementId = m.managementId ORDER BY m.time DESC")
    List<NovelManagement> findAllOrderByTimeDesc();
    
    /**
     * 根据小说ID获取小说管理记录，按时间倒序排列
     * @param novelId 小说ID
     * @return 小说管理记录列表
     */
    @Query("SELECT nm FROM NovelManagement nm, Management m WHERE nm.managementId = m.managementId AND nm.novelId = :novelId ORDER BY m.time DESC")
    List<NovelManagement> findByNovelIdOrderByTimeDesc(@Param("novelId") Long novelId);
}