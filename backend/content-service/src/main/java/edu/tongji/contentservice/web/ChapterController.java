package edu.tongji.contentservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.contentservice.entity.Chapter;
import edu.tongji.contentservice.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chapters")
@Tag(name = "Chapter API", description = "章节管理API")
public class ChapterController {

    private final ChapterService chapterService;

    @Autowired
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    /**
     * 获取所有"首次审核"、"审核中"的章节
     * @return 章节列表
     */
    @GetMapping
    @Operation(summary = "获取审核中的章节", description = "获取所有状态为\"首次审核\"或\"审核中\"的章节")
    public ResponseEntity<ApiResponse<List<Chapter>>> getChaptersInReviewStatus() {
        List<Chapter> chapters = chapterService.getChaptersInReviewStatus();
        return ResponseEntity.ok(ApiResponse.ok("content-service", chapters));
    }

    /**
     * 获取指定小说下的所有章节
     * @param novelId 小说ID
     * @return 章节列表
     */
    @GetMapping("/novel/{novelId}")
    @Operation(summary = "获取小说章节", description = "获取指定小说下的所有章节")
    public ResponseEntity<ApiResponse<List<Chapter>>> getChaptersByNovelId(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        List<Chapter> chapters = chapterService.getChaptersByNovelId(novelId);
        return ResponseEntity.ok(ApiResponse.ok("content-service", chapters));
    }

    /**
     * 获取小说的所有章节（与上一个方法相同，但API路径不同）
     * @param novelId 小说ID
     * @return 章节列表
     */
    @GetMapping("/novels/{novelId}/chapters")
    @Operation(summary = "获取小说所有章节", description = "获取指定小说的所有章节")
    public ResponseEntity<ApiResponse<List<Chapter>>> getAllChaptersByNovelId(
            @Parameter(description = "小说ID") @PathVariable Long novelId) {
        List<Chapter> chapters = chapterService.getAllChaptersByNovelId(novelId);
        return ResponseEntity.ok(ApiResponse.ok("content-service", chapters));
    }

    /**
     * 获取指定章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 章节对象
     */
    @GetMapping("/{novelId}/{chapterId}")
    @Operation(summary = "获取章节详情", description = "获取指定小说的指定章节")
    public ResponseEntity<ApiResponse<Chapter>> getChapterById(
            @Parameter(description = "小说ID") @PathVariable Long novelId,
            @Parameter(description = "章节ID") @PathVariable Long chapterId) {
        Optional<Chapter> chapter = chapterService.getChapterById(novelId, chapterId);
        if (chapter.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", chapter.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "章节不存在，小说ID: " + novelId + ", 章节ID: " + chapterId));
        }
    }

    /**
     * 添加章节
     * @param chapter 章节对象
     * @return 保存后的章节对象
     */
    @PostMapping
    @Operation(summary = "添加章节", description = "添加新的章节")
    public ResponseEntity<ApiResponse<Chapter>> addChapter(@RequestBody Chapter chapter) {
        try {
            Chapter savedChapter = chapterService.addChapter(chapter);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("content-service", "章节添加成功", savedChapter));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "添加章节失败：" + e.getMessage()));
        }
    }

    /**
     * 更新章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param chapter 更新的章节对象
     * @return 更新后的章节对象
     */
    @PutMapping("/{novelId}/{chapterId}")
    @Operation(summary = "更新章节", description = "更新指定小说的指定章节")
    public ResponseEntity<ApiResponse<Chapter>> updateChapter(
            @Parameter(description = "小说ID") @PathVariable Long novelId,
            @Parameter(description = "章节ID") @PathVariable Long chapterId,
            @RequestBody Chapter chapter) {
        Optional<Chapter> updatedChapter = chapterService.updateChapter(novelId, chapterId, chapter);
        if (updatedChapter.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", "章节更新成功", updatedChapter.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "章节不存在，小说ID: " + novelId + ", 章节ID: " + chapterId));
        }
    }

    /**
     * 删除章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 删除结果
     */
    @DeleteMapping("/{novelId}/{chapterId}")
    @Operation(summary = "删除章节", description = "删除指定小说的指定章节")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(
            @Parameter(description = "小说ID") @PathVariable Long novelId,
            @Parameter(description = "章节ID") @PathVariable Long chapterId) {
        boolean deleted = chapterService.deleteChapter(novelId, chapterId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.ok("content-service", "章节删除成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "章节不存在，小说ID: " + novelId + ", 章节ID: " + chapterId));
        }
    }
    
    @GetMapping("/statistics/pending-chapters")
    @Operation(summary = "获取待审核章节数量", description = "获取系统中待审核章节的数量")
    public ResponseEntity<ApiResponse<Long>> getPendingChaptersCount() {
        Long count = chapterService.getPendingChaptersCount();
        return ResponseEntity.ok(ApiResponse.ok("content-service", count));
    }
    
    /**
     * 获取章节价格
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 章节价格信息
     */
    @GetMapping("/novels/{novelId}/chapters/{chapterId}/price")
    @Operation(summary = "获取章节价格", description = "获取指定章节的计算价格")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getChapterPrice(
            @Parameter(description = "小说ID") @PathVariable Long novelId,
            @Parameter(description = "章节ID") @PathVariable Long chapterId) {
        Optional<Chapter> chapterOpt = chapterService.getChapterById(novelId, chapterId);
        if (chapterOpt.isPresent()) {
            Chapter chapter = chapterOpt.get();
            java.util.Map<String, Object> priceInfo = new java.util.HashMap<>();
            priceInfo.put("calculatedPrice", chapter.getCalculatedPrice());
            priceInfo.put("isCharged", chapter.getIsCharged());
            priceInfo.put("wordCount", chapter.getWordCount());
            priceInfo.put("pricePerKilo", chapter.getPricePerKilo());
            return ResponseEntity.ok(ApiResponse.ok("content-service", priceInfo));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("content-service", "章节不存在，小说ID: " + novelId + ", 章节ID: " + chapterId));
        }
    }
    
    /**
     * 审核章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param newStatus 新状态
     * @param managerId 管理员ID
     * @param result 审核结果
     * @return 更新后的章节对象
     */
    @PutMapping("/novels/{novelId}/chapters/{chapterId}/review")
    @Operation(summary = "审核章节", description = "管理员审核章节，更新章节状态")
    public ResponseEntity<ApiResponse<Chapter>> reviewChapter(
            @Parameter(description = "小说ID") @PathVariable Long novelId,
            @Parameter(description = "章节ID") @PathVariable Long chapterId,
            @Parameter(description = "新状态") @RequestParam String newStatus,
            @Parameter(description = "管理员ID") @RequestParam(required = false) Long managerId,
            @Parameter(description = "审核结果") @RequestParam(required = false) String result) {
        try {
            Optional<Chapter> chapterOpt = chapterService.reviewChapter(novelId, chapterId, newStatus, managerId, result);
            if (chapterOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.ok("content-service", "审核成功", chapterOpt.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("content-service", "章节不存在，小说ID: " + novelId + ", 章节ID: " + chapterId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "审核章节失败：" + e.getMessage()));
        }
    }
}