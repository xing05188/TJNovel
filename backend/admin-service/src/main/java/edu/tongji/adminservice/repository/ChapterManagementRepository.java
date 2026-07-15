package edu.tongji.adminservice.repository;

import edu.tongji.adminservice.entity.ChapterManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 章节管理记录数据访问接口
 */
@Repository
public interface ChapterManagementRepository extends JpaRepository<ChapterManagement, Long> {
    
    /**
     * 根据章节ID获取章节管理记录列表
     * @param chapterId 章节ID
     * @return 章节管理记录列表
     */
    List<ChapterManagement> findByChapterId(Long chapterId);
    
    /**
     * 根据章节ID和小说ID获取章节管理记录列表
     * @param chapterId 章节ID
     * @param novelId 小说ID
     * @return 章节管理记录列表
     */
    List<ChapterManagement> findByChapterIdAndNovelId(Long chapterId, Long novelId);
    
    /**
     * 获取所有章节管理记录，按时间倒序排列
     * @return 所有章节管理记录列表
     */
    @Query("SELECT cm FROM ChapterManagement cm, Management m WHERE cm.managementId = m.managementId ORDER BY m.time DESC")
    List<ChapterManagement> findAllOrderByTimeDesc();
    
    /**
     * 根据章节ID获取章节管理记录，按时间倒序排列
     * @param chapterId 章节ID
     * @return 章节管理记录列表
     */
    @Query("SELECT cm FROM ChapterManagement cm, Management m WHERE cm.managementId = m.managementId AND cm.chapterId = :chapterId ORDER BY m.time DESC")
    List<ChapterManagement> findByChapterIdOrderByTimeDesc(@Param("chapterId") Long chapterId);
    
    /**
     * 根据章节ID和小说ID获取章节管理记录，按时间倒序排列
     * @param chapterId 章节ID
     * @param novelId 小说ID
     * @return 章节管理记录列表
     */
    @Query("SELECT cm FROM ChapterManagement cm, Management m WHERE cm.managementId = m.managementId AND cm.chapterId = :chapterId AND cm.novelId = :novelId ORDER BY m.time DESC")
    List<ChapterManagement> findByChapterIdAndNovelIdOrderByTimeDesc(@Param("chapterId") Long chapterId, @Param("novelId") Long novelId);
}