package edu.tongji.contentservice.web;

import edu.tongji.contentservice.document.ChapterDocument;
import edu.tongji.contentservice.document.NovelDocument;
import edu.tongji.contentservice.service.SearchService;
import edu.tongji.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 全文搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 搜索小说（按名称和简介）
     */
    @GetMapping("/novels")
    public ApiResponse<Page<NovelDocument>> searchNovels(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NovelDocument> result = searchService.searchNovels(keyword, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 按分类搜索小说
     */
    @GetMapping("/novels/category")
    public ApiResponse<Page<NovelDocument>> searchNovelsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NovelDocument> result = searchService.searchNovelsByCategory(category, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 高级搜索（多条件组合）
     */
    @GetMapping("/novels/advanced")
    public ApiResponse<Page<NovelDocument>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long minWordCount,
            @RequestParam(required = false) Long maxWordCount,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NovelDocument> result = searchService.advancedSearch(
                keyword, category, minWordCount, maxWordCount, status, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 搜索章节内容
     */
    @GetMapping("/chapters")
    public ApiResponse<Page<ChapterDocument>> searchChapters(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChapterDocument> result = searchService.searchChapters(keyword, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 搜索章节标题
     */
    @GetMapping("/chapters/titles")
    public ApiResponse<Page<ChapterDocument>> searchChapterTitles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChapterDocument> result = searchService.searchChapterTitles(keyword, page, size);
        return ApiResponse.ok(result);
    }

    /**
     * 获取某部小说的所有章节
     */
    @GetMapping("/novels/{novelId}/chapters")
    public ApiResponse<List<ChapterDocument>> getNovelChapters(@PathVariable Long novelId) {
        List<ChapterDocument> chapters = searchService.getNovelChapters(novelId);
        return ApiResponse.ok(chapters);
    }
}