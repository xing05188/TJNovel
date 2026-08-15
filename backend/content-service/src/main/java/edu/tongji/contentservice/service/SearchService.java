package edu.tongji.contentservice.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import edu.tongji.contentservice.document.ChapterDocument;
import edu.tongji.contentservice.document.NovelDocument;
import edu.tongji.contentservice.repository.ChapterSearchRepository;
import edu.tongji.contentservice.repository.NovelSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
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
     * 搜索小说（标题/简介/作者名 模糊搜索）
     * 字段使用 IK 分词，必须用 multiMatch 查询（Containing 派生查询生成的 wildcard 在分词字段上无法命中）
     */
    public Page<NovelDocument> searchNovels(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));

        var boolQuery = QueryBuilders.bool();
        boolQuery.must(m -> m.multiMatch(mt -> mt
                .fields("novelName^3", "authorName^2", "introduction")
                .query(keyword)));
        // 只展示已发布状态的小说
        boolQuery.filter(f -> f.bool(b -> b
                .should(s -> s.term(t -> t.field("status").value("连载")))
                .should(s -> s.term(t -> t.field("status").value("完结")))));

        var nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery.build()))
                .withPageable(pageable)
                .build();

        SearchHits<NovelDocument> hits = elasticsearchTemplate.search(nativeQuery, NovelDocument.class);
        List<NovelDocument> list = hits.getSearchHits().stream().map(SearchHit::getContent).toList();
        return new PageImpl<>(list, pageable, hits.getTotalHits());
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
                    .fields("novelName^3", "authorName^2", "introduction")
                    .query(keyword)));
        }

        // 分类筛选
        if (category != null && !category.isEmpty()) {
            boolQuery.filter(f -> f.term(t -> t.field("categoryName").value(category)));
        }

        // 字数范围筛选
        if (minWordCount != null) {
            boolQuery.filter(f -> f.range(r -> r.field("totalWordCount").gte(JsonData.of(minWordCount))));
        }
        if (maxWordCount != null) {
            boolQuery.filter(f -> f.range(r -> r.field("totalWordCount").lte(JsonData.of(maxWordCount))));
        }

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            boolQuery.filter(f -> f.term(t -> t.field("status").value(status)));
        }

        var nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery.build()))
                .withPageable(pageable)
                .build();

        SearchHits<NovelDocument> hits = elasticsearchTemplate.search(nativeQuery, NovelDocument.class);
        List<NovelDocument> list = hits.getSearchHits().stream().map(SearchHit::getContent).toList();
        return new PageImpl<>(list, pageable, hits.getTotalHits());
    }

    /**
     * 搜索章节内容（带关键词高亮：命中片段用 <em> 标签包裹返回）
     */
    public Page<ChapterDocument> searchChapters(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        HighlightField highlightField = new HighlightField("content",
                HighlightFieldParameters.builder()
                        .withPreTags("<em>")
                        .withPostTags("</em>")
                        .build());
        HighlightQuery highlightQuery = new HighlightQuery(new Highlight(List.of(highlightField)), ChapterDocument.class);

        var query = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.field("content").query(keyword)))
                .withPageable(pageable)
                .build();
        query.setHighlightQuery(highlightQuery);
        SearchHits<ChapterDocument> hits = elasticsearchTemplate.search(query, ChapterDocument.class);
        List<ChapterDocument> list = hits.getSearchHits().stream().map(hit -> {
            ChapterDocument doc = hit.getContent();
            if (hit.getHighlightFields() != null && hit.getHighlightFields().containsKey("content")) {
                doc.setHighlight(String.join(" ... ", hit.getHighlightFields().get("content")));
            }
            return doc;
        }).toList();
        return new PageImpl<>(list, pageable, hits.getTotalHits());
    }

    /**
     * 搜索章节标题（IK 分词字段使用 match 查询）
     */
    public Page<ChapterDocument> searchChapterTitles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.field("title").query(keyword)))
                .withPageable(pageable)
                .build();
        SearchHits<ChapterDocument> hits = elasticsearchTemplate.search(nativeQuery, ChapterDocument.class);
        List<ChapterDocument> list = hits.getSearchHits().stream().map(SearchHit::getContent).toList();
        return new PageImpl<>(list, pageable, hits.getTotalHits());
    }

    /**
     * 获取某部小说的所有章节
     */
    public List<ChapterDocument> getNovelChapters(Long novelId) {
        Page<ChapterDocument> result = chapterSearchRepository.findByNovelIdAndStatus(novelId, "已发布", Pageable.unpaged());
        return result.getContent();
    }
}