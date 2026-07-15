package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovelRepository extends JpaRepository<Novel, Long> {
    
    /**
     * 通过小说名模糊搜索小说
     * @param keyword 关键词
     * @return 匹配的小说列表
     */
    @Query("SELECT n FROM Novel n WHERE n.novelName LIKE %:keyword%")
    List<Novel> findByNovelNameContaining(@Param("keyword") String keyword);
    
    /**
     * 获取小说总数
     * @return 小说总数
     */
    @Query("SELECT COUNT(n) FROM Novel n")
    Long countTotalNovels();
    
    /**
     * 获取待审核小说数量
     * @return 待审核小说数量
     */
    @Query("SELECT COUNT(n) FROM Novel n WHERE n.status = '待审核'")
    Long countPendingNovels();
    
    /**
     * 根据作者ID获取小说数量
     * @param authorId 作者ID
     * @return 小说数量
     */
    @Query("SELECT COUNT(n) FROM Novel n WHERE n.authorId = :authorId")
    Long countByAuthorId(@Param("authorId") Long authorId);
    
    /**
     * 根据作者ID获取所有小说
     * @param authorId 作者ID
     * @return 小说列表
     */
    List<Novel> findByAuthorId(Long authorId);
    
    /**
     * 根据作者ID计算总字数
     * @param authorId 作者ID
     * @return 总字数
     */
    @Query("SELECT COALESCE(SUM(n.totalWordCount), 0) FROM Novel n WHERE n.authorId = :authorId")
    Long sumWordCountByAuthorId(@Param("authorId") Long authorId);
    
    /**
     * 根据小说ID获取小说总字数
     * @param novelId 小说ID
     * @return 小说总字数
     */
    @Query("SELECT n.totalWordCount FROM Novel n WHERE n.novelId = :novelId")
    Long getWordCountByNovelId(@Param("novelId") Long novelId);
    
    /**
     * 根据小说ID获取小说推荐数
     * @param novelId 小说ID
     * @return 小说推荐数
     */
    @Query("SELECT n.recommendCount FROM Novel n WHERE n.novelId = :novelId")
    Integer getRecommendCountByNovelId(@Param("novelId") Long novelId);
    
    /**
     * 根据小说ID获取小说收藏数
     * @param novelId 小说ID
     * @return 小说收藏数
     */
    @Query("SELECT n.collectedCount FROM Novel n WHERE n.novelId = :novelId")
    Integer getCollectCountByNovelId(@Param("novelId") Long novelId);
    
    /**
     * 获取所有已发布的小说（状态为"连载"或"完结"）
     * @return 已发布小说列表
     */
    @Query("SELECT n FROM Novel n WHERE n.status = '连载' OR n.status = '完结'")
    List<Novel> findAllPublishedNovels();
    
    /**
     * 分页获取所有已发布的小说（状态为"连载"或"完结"），按novelID顺序
     * @param pageable 分页参数
     * @return 已发布小说分页结果
     */
    @Query("SELECT n FROM Novel n WHERE n.status = '连载' OR n.status = '完结' ORDER BY n.novelId")
    Page<Novel> findPublishedNovelsOrderById(Pageable pageable);
    
    /**
     * 分页获取已发布小说（状态为"连载"或"完结"），按NovelId顺序，支持条件筛选
     * @param category 分类筛选
     * @param minWordCount 最小字数筛选
     * @param maxWordCount 最大字数筛选
     * @param isFinished 是否完结筛选
     * @param pageable 分页参数
     * @return 已发布小说分页结果
     */
    @Query("SELECT DISTINCT n FROM Novel n " +
           "LEFT JOIN NovelCategory nc ON n.novelId = nc.novelId " +
           "WHERE (n.status = '连载' OR n.status = '完结') " +
           "AND (:category IS NULL OR nc.categoryName = :category) " +
           "AND (:minWordCount IS NULL OR n.totalWordCount >= :minWordCount) " +
           "AND (:maxWordCount IS NULL OR n.totalWordCount <= :maxWordCount) " +
           "AND (:isFinished IS NULL OR (:isFinished = true AND n.status = '完结') OR (:isFinished = false AND n.status = '连载')) " +
           "ORDER BY n.novelId")
    Page<Novel> findPublishedNovelsWithFilters(
        @Param("category") String category,
        @Param("minWordCount") Long minWordCount,
        @Param("maxWordCount") Long maxWordCount,
        @Param("isFinished") Boolean isFinished,
        Pageable pageable);
}