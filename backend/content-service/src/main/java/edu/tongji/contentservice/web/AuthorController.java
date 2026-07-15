package edu.tongji.contentservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.service.NovelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/author")
@Tag(name = "作者相关API", description = "提供作者相关的小说数据查询接口")
public class AuthorController {

    @Autowired
    private NovelService novelService;

    /**
     * 获取作者小说数量
     * @param authorId 作者ID
     * @return 小说数量
     */
    @GetMapping("/{authorId}/novel-count")
    @Operation(summary = "获取作者小说数量", description = "根据作者ID获取该作者的小说数量")
    public ResponseEntity<ApiResponse<Long>> getAuthorNovelCount(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId) {
        try {
            Long count = novelService.getNovelCountByAuthorId(authorId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "获取作者小说数量失败：" + e.getMessage()));
        }
    }

    /**
     * 获取作者总字数
     * @param authorId 作者ID
     * @return 总字数
     */
    @GetMapping("/{authorId}/total-wordcount")
    @Operation(summary = "获取作者总字数", description = "根据作者ID获取该作者所有小说的总字数")
    public ResponseEntity<ApiResponse<Long>> getAuthorTotalWordCount(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId) {
        try {
            Long totalWordCount = novelService.getTotalWordCountByAuthorId(authorId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", totalWordCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "获取作者总字数失败：" + e.getMessage()));
        }
    }

    /**
     * 获取作者所有小说
     * @param authorId 作者ID
     * @return 小说列表
     */
    @GetMapping("/{authorId}/novels")
    @Operation(summary = "获取作者所有小说", description = "根据作者ID获取该作者的所有小说")
    public ResponseEntity<ApiResponse<List<Novel>>> getAuthorNovels(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId) {
        try {
            List<Novel> novels = novelService.getNovelsByAuthorId(authorId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", novels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "获取作者小说列表失败：" + e.getMessage()));
        }
    }
}