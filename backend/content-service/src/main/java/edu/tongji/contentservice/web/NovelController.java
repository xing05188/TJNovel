package edu.tongji.contentservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.contentservice.dto.LatestPublishedChapterDto;
import edu.tongji.contentservice.dto.NovelCreateDto;
import edu.tongji.contentservice.dto.NovelEditDto;
import edu.tongji.contentservice.dto.NovelEditRequestDto;
import edu.tongji.contentservice.dto.NovelReviewDto;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.service.NovelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/novels")
@Tag(name = "Novel API", description = "小说管理API")
public class NovelController {

    private static final Logger logger = LoggerFactory.getLogger(NovelController.class);
    
    private final NovelService novelService;

    @Autowired
    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }

    @GetMapping
    @Operation(summary = "获取所有小说", description = "获取系统中所有小说的列表")
    public ResponseEntity<ApiResponse<List<Novel>>> getAllNovels() {
        List<Novel> novels = novelService.getAllNovels();
        return ResponseEntity.ok(ApiResponse.ok("content-service", novels));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取小说", description = "通过小说ID获取特定小说的详细信息")
    public ResponseEntity<ApiResponse<Novel>> getNovelById(
            @Parameter(description = "小说ID") @PathVariable Long id) {
        Optional<Novel> novel = novelService.getNovelById(id);
        if (novel.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", novel.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + id));
        }
    }

    @PostMapping("/create")
    @Operation(summary = "创建小说", description = "根据作者ID、小说名称和简介创建新小说")
    public ResponseEntity<ApiResponse<Novel>> createNovelFromDto(@RequestBody NovelCreateDto novelCreateDto) {
        Novel createdNovel = novelService.createNovelFromDto(novelCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("content-service", "小说创建成功", createdNovel));
    }

    @PutMapping("/{id}/review")
    @Operation(summary = "审核小说", description = "管理员审核小说，更新状态并记录审核结果")
    public ResponseEntity<ApiResponse<Void>> reviewNovel(
            @Parameter(description = "小说ID") @PathVariable Long id,
            @RequestBody NovelReviewDto reviewDto) {
        boolean reviewed = novelService.reviewNovel(id, reviewDto.getNewStatus(), 
                reviewDto.getManagerId(), reviewDto.getResult());
        if (reviewed) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", "小说审核成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + id));
        }
    }

    @PostMapping("/UploadAvatar")
    @Operation(summary = "上传小说封面", description = "为指定小说上传封面图片")
    public ResponseEntity<ApiResponse<String>> uploadNovelCover(
            @RequestParam("novelId") Long novelId,
            @RequestParam("coverFile") MultipartFile coverFile) {
        String coverUrl = novelService.uploadNovelCover(novelId, coverFile);
        if (coverUrl != null) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", "封面上传成功", coverUrl));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + novelId));
        }
    }

    @PostMapping("/submit-edit")
    @Operation(summary = "修改小说信息", description = "提交小说信息修改请求")
    public ResponseEntity<ApiResponse<Novel>> submitEditNovel(@RequestBody NovelEditRequestDto editRequest) {
        try {
            // 验证请求参数
            if (editRequest == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("content-service", "请求参数不能为空"));
            }
            if (editRequest.getOriginalNovelId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("content-service", "原始小说ID不能为空"));
            }
            if (editRequest.getEditedDto() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("content-service", "编辑数据不能为空"));
            }
            
            Novel updatedNovel = novelService.editNovel(editRequest.getOriginalNovelId(), editRequest.getEditedDto());
            if (updatedNovel != null) {
                return ResponseEntity.ok(ApiResponse.ok("content-service", "小说信息修改成功", updatedNovel));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("content-service", "小说不存在，ID: " + editRequest.getOriginalNovelId()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        } catch (Exception e) {
            logger.error("修改小说信息失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "修改小说信息失败：" + e.getMessage()));
        }
    }

    @RequestMapping(value = "", method = RequestMethod.OPTIONS)
    @Operation(summary = "处理CORS预检请求", description = "处理跨域资源共享预检请求")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Operation(summary = "创建新小说", description = "在系统中创建一个新的小说")
    public ResponseEntity<ApiResponse<Novel>> createNovel(@RequestBody Novel novel) {
        Novel createdNovel = novelService.createNovel(novel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("content-service", "小说创建成功", createdNovel));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新小说", description = "更新指定ID的小说信息")
    public ResponseEntity<ApiResponse<Novel>> updateNovel(
            @Parameter(description = "小说ID") @PathVariable Long id,
            @RequestBody Novel novelDetails) {
        Novel updatedNovel = novelService.updateNovel(id, novelDetails);
        if (updatedNovel != null) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", "小说更新成功", updatedNovel));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + id));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除小说", description = "根据ID删除指定的小说")
    public ResponseEntity<ApiResponse<Void>> deleteNovel(
            @Parameter(description = "小说ID") @PathVariable Long id) {
        boolean deleted = novelService.deleteNovel(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", "小说删除成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + id));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索小说", description = "通过小说名模糊搜索小说")
    public ResponseEntity<ApiResponse<List<Novel>>> searchNovels(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        List<Novel> novels = novelService.searchNovelsByName(keyword);
        return ResponseEntity.ok(ApiResponse.ok("content-service", novels));
    }
    
    @GetMapping("/statistics/total-novels")
    @Operation(summary = "获取小说总数", description = "获取系统中小说的总数")
    public ResponseEntity<ApiResponse<Long>> getTotalNovelsCount() {
        Long count = novelService.getTotalNovelsCount();
        return ResponseEntity.ok(ApiResponse.ok("content-service", count));
    }
    
    @GetMapping("/statistics/pending-novels")
    @Operation(summary = "获取待审核小说数量", description = "获取系统中待审核小说的数量")
    public ResponseEntity<ApiResponse<Long>> getPendingNovelsCount() {
        Long count = novelService.getPendingNovelsCount();
        return ResponseEntity.ok(ApiResponse.ok("content-service", count));
    }
    
    // 新增的API接口
    
    @GetMapping("/wordcount/{novelId}")
    @Operation(summary = "获取小说总字数", description = "根据小说ID获取小说的总字数")
    public ResponseEntity<ApiResponse<Long>> getWordCountByNovelId(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        Long wordCount = novelService.getWordCountByNovelId(novelId);
        if (wordCount != null) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", wordCount));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + novelId));
        }
    }
    
    @GetMapping("/recommendcount/{novelId}")
    @Operation(summary = "获取小说推荐数", description = "根据小说ID获取小说的推荐数")
    public ResponseEntity<ApiResponse<Integer>> getRecommendCountByNovelId(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        Integer recommendCount = novelService.getRecommendCountByNovelId(novelId);
        if (recommendCount != null) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", recommendCount));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + novelId));
        }
    }
    
    @GetMapping("/collectcount/{novelId}")
    @Operation(summary = "获取小说收藏数", description = "根据小说ID获取小说的收藏数")
    public ResponseEntity<ApiResponse<Integer>> getCollectCountByNovelId(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        Integer collectCount = novelService.getCollectCountByNovelId(novelId);
        if (collectCount != null) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", collectCount));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + novelId));
        }
    }
    
    @GetMapping("/{novelId}/latest-published-chapter")
    @Operation(summary = "获取小说最新已发布章节", description = "根据小说ID获取最新已发布章节的ID和发布时间")
    public ResponseEntity<ApiResponse<LatestPublishedChapterDto>> getLatestPublishedChapter(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        LatestPublishedChapterDto chapter = novelService.getLatestPublishedChapter(novelId);
        if (chapter != null) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", chapter));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在或没有已发布章节，ID: " + novelId));
        }
    }
    
    @GetMapping("/published")
    @Operation(summary = "获取所有已发布的小说", description = "获取所有已发布的小说（Status == \"连载\" 或 \"完结\"）")
    public ResponseEntity<ApiResponse<List<Novel>>> getAllPublishedNovels() {
        List<Novel> novels = novelService.getAllPublishedNovels();
        return ResponseEntity.ok(ApiResponse.ok("content-service", novels));
    }
    
    @GetMapping("/published/by-id")
    @Operation(summary = "分页获取已发布的小说", description = "获取所有已发布的小说（分页，按novelID顺序）")
    public ResponseEntity<ApiResponse<Page<Novel>>> getPublishedNovelsOrderById(
            @Parameter(description = "页码（从1开始）") @RequestParam int page,
            @Parameter(description = "每页大小") @RequestParam int pageSize) {
        Page<Novel> novels = novelService.getPublishedNovelsOrderById(page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok("content-service", novels));
    }
    
    @GetMapping("/published/filter-by-id")
    @Operation(summary = "分页+条件筛选获取已发布小说", description = "获取已发布小说（分页+条件筛选，按NovelId顺序）")
    public ResponseEntity<ApiResponse<Page<Novel>>> getPublishedNovelsWithFilters(
            @Parameter(description = "页码（从1开始）") @RequestParam int page,
            @Parameter(description = "每页大小") @RequestParam int pageSize,
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "最小字数筛选") @RequestParam(required = false) Long minWordCount,
            @Parameter(description = "最大字数筛选") @RequestParam(required = false) Long maxWordCount,
            @Parameter(description = "是否完结筛选") @RequestParam(required = false) Boolean isFinished) {
        Page<Novel> novels = novelService.getPublishedNovelsWithFilters(page, pageSize, category, minWordCount, maxWordCount, isFinished);
        return ResponseEntity.ok(ApiResponse.ok("content-service", novels));
    }
    
    /**
     * 获取小说的作者ID
     * @param novelId 小说ID
     * @return 作者ID
     */
    @GetMapping("/{novelId}/author-id")
    @Operation(summary = "获取小说的作者ID", description = "根据小说ID获取该小说的作者ID")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getNovelAuthorId(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        Optional<Novel> novelOpt = novelService.getNovelById(novelId);
        if (novelOpt.isPresent()) {
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("authorId", novelOpt.get().getAuthorId());
            return ResponseEntity.ok(ApiResponse.ok("content-service", result));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "小说不存在，ID: " + novelId));
        }
    }
}