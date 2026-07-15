package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.document.NovelDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 小说 Elasticsearch 搜索仓库
 */
@Repository
public interface NovelSearchRepository extends ElasticsearchRepository<NovelDocument, Long> {

    /**
     * 按小说名模糊搜索
     */
    Page<NovelDocument> findByNovelNameContaining(String keyword, Pageable pageable);

    /**
     * 按小说名或简介模糊搜索
     */
    Page<NovelDocument> findByNovelNameContainingOrIntroductionContaining(String name, String introduction, Pageable pageable);

    /**
     * 按分类搜索
     */
    Page<NovelDocument> findByCategoryName(String categoryName, Pageable pageable);

    /**
     * 按状态搜索
     */
    Page<NovelDocument> findByStatus(String status, Pageable pageable);

    /**
     * 按分类和状态搜索
     */
    Page<NovelDocument> findByCategoryNameAndStatus(String categoryName, String status, Pageable pageable);

    /**
     * 按作者ID搜索
     */
    Page<NovelDocument> findByAuthorId(Long authorId, Pageable pageable);
}