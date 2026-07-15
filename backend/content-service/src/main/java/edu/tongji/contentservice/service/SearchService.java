package edu.tongji.contentservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import edu.tongji.contentservice.document.ChapterDocument;
import edu.tongji.contentservice.document.NovelDocument;
import edu.tongji.contentservice.repository.ChapterSearchRepository;
import edu.tongji.contentservice.repository.NovelSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 全文搜索服务
 */
@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final NovelSearchRepository novelSearchRepository;
    private final ChapterSearchRepository chapterSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public SearchService(NovelSearchRepository novelSearchRepository,
                         ChapterSearchRepository chapterSearchRepository,
                         ElasticsearchTemplate elasticsearchTemplate) {
        this.novelSearchRepository = novelSearchRepository;
        this.chapterSearchRepository = chapterSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    /**
     * 搜索小说（按名称和简介模糊搜索）
     */
    public Page<NovelDocument> searchNovels(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
        return novelSearchRepository.findByNovelNameContainingOrIntroductionContaining(keyword, keyword, pageable);
    }

    /**
     * 按分类搜索小说
     */
    public Page<NovelDocument> searchNovelsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "recommendCount"));
        return novelSearchRepository.findByCategoryName(category, pageable);
    }

    /**
     * 高级搜索（多条件组合）
     */
    public Page<NovelDocument> advancedSearch(String keyword, String category,
                                               Long minWordCount, Long maxWordCount,
                                               String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        var boolQuery = QueryBuilders.bool();

        // 关键词搜索（名称或简介）
        if (keyword != null && !keyword.isEmpty()) {
            boolQuery.must(m -> m.multiMatch(mt -> mt
                    .fields("novelName^3", "introduction")
                    .query(keyword)));
        }

        // 分类筛选
        if (category != null && !category.isEmpty()) {
            boolQuery.filter(f -> f.term(t -> t.field("categoryName").value(category)));
        }

        // 字数范围筛选
        if (minWordCount != null) {
            boolQuery.filter(f -> f.range(r -> r.field("totalWordCount").gte(minWordCount)));
        }
        if (maxWordCount != null) {
            boolQuery.filter(f -> f.range(r -> r.field("totalWordCount").lte(maxWordCount)));
        }

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            boolQuery.filter(f -> f.term(t -> t.field("status").value(status)));
        }

        var nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery.build()))
                .withPageable(pageable)
                .build();

        return elasticsearchTemplate.search(nativeQuery, NovelDocument.class)
                .map(searchHit -> searchHit.getContent());
    }

    /**
     * 搜索章节内容
     */
    public Page<ChapterDocument> searchChapters(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chapterSearchRepository.findByContentContaining(keyword, pageable);
    }

    /**
     * 搜索章节标题
     */
    public Page<ChapterDocument> searchChapterTitles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chapterSearchRepository.findByTitleContaining(keyword, pageable);
    }

    /**
     * 获取某部小说的所有章节
     */
    public List<ChapterDocument> getNovelChapters(Long novelId) {
        Page<ChapterDocument> result = chapterSearchRepository.findByNovelIdAndStatus(novelId, "已发布", Pageable.unpaged());
        return result.getContent();
    }
}