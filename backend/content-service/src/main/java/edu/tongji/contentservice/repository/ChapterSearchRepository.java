package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.document.ChapterDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 章节 Elasticsearch 搜索仓库
 */
@Repository
public interface ChapterSearchRepository extends ElasticsearchRepository<ChapterDocument, String> {

    /**
     * 按章节标题模糊搜索
     */
    Page<ChapterDocument> findByTitleContaining(String keyword, Pageable pageable);

    /**
     * 按章节内容模糊搜索
     */
    Page<ChapterDocument> findByContentContaining(String keyword, Pageable pageable);

    /**
     * 按小说ID搜索章节
     */
    Page<ChapterDocument> findByNovelId(Long novelId, Pageable pageable);

    /**
     * 按小说ID和状态搜索章节
     */
    Page<ChapterDocument> findByNovelIdAndStatus(Long novelId, String status, Pageable pageable);
}