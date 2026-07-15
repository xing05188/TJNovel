package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Chapter;
import edu.tongji.contentservice.entity.ChapterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, ChapterId> {
    
    /**
     * 获取所有状态为"首次审核"或"审核中"的章节
     * @return 章节列表
     */
    @Query("SELECT c FROM Chapter c WHERE c.status IN ('首次审核', '审核中')")
    List<Chapter> findChaptersInReviewStatus();
    
    /**
     * 根据小说ID获取所有章节
     * @param novelId 小说ID
     * @return 章节列表
     */
    List<Chapter> findByNovelIdOrderByChapterId(Long novelId);
    
    /**
     * 根据小说ID和章节ID获取特定章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 章节对象
     */
    Chapter findByNovelIdAndChapterId(Long novelId, Long chapterId);
    
    /**
     * 根据小说ID和章节ID删除章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     */
    void deleteByNovelIdAndChapterId(Long novelId, Long chapterId);
    
    /**
     * 获取指定小说的章节总数
     * @param novelId 小说ID
     * @return 章章节数量
     */
    @Query("SELECT COUNT(c) FROM Chapter c WHERE c.novelId = :novelId")
    Long countByNovelId(@Param("novelId") Long novelId);
    
    /**
     * 获取待审核章节数量
     * @return 待审核章节数量
     */
    @Query("SELECT COUNT(c) FROM Chapter c WHERE c.status IN ('首次审核', '审核中')")
    Long countPendingChapters();
    
    /**
     * 获取指定小说最新已发布章节
     * @param novelId 小说ID
     * @return 最新已发布章节
     */
    @Query("SELECT c FROM Chapter c WHERE c.novelId = :novelId AND c.status = '已发布' ORDER BY c.chapterId DESC")
    List<Chapter> findLatestPublishedChapter(@Param("novelId") Long novelId);

    /**
     * 计算指定小说所有已发布章节的总字数
     * @param novelId 小说ID
     * @return 总字数
     */
    @Query("SELECT COALESCE(SUM(c.wordCount), 0) FROM Chapter c WHERE c.novelId = :novelId AND c.status = '已发布'")
    Long sumWordCountByNovelId(@Param("novelId") Long novelId);
}