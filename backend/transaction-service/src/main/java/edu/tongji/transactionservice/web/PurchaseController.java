package edu.tongji.transactionservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.transactionservice.dto.ChapterPurchaseDto;
import edu.tongji.transactionservice.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Purchase")
@Tag(name = "章节购买API", description = "提供章节购买和查询功能")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService purchaseService;

    /**
     * 购买章节
     * @param chapterPurchaseDto 章节购买请求
     * @return 购买结果
     */
    @PostMapping
    @Operation(summary = "购买章节", description = "用户购买单个付费章节")
    public ResponseEntity<ApiResponse<String>> purchaseChapter(
            @Parameter(description = "章节购买请求", required = true) 
            @Valid @RequestBody ChapterPurchaseDto chapterPurchaseDto) {
        try {
            logger.info("收到章节购买请求，读者ID: {}, 小说ID: {}, 章节ID: {}", 
                chapterPurchaseDto.getReaderId(), 
                chapterPurchaseDto.getNovelId(), 
                chapterPurchaseDto.getChapterId());
            
            // 验证请求参数
            if (chapterPurchaseDto.getReaderId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("transaction-service", "读者ID不能为空"));
            }
            if (chapterPurchaseDto.getNovelId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("transaction-service", "小说ID不能为空"));
            }
            if (chapterPurchaseDto.getChapterId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("transaction-service", "章节ID不能为空"));
            }
            
            boolean success = purchaseService.processChapterPurchase(chapterPurchaseDto);
            if (success) {
                return ResponseEntity.ok(ApiResponse.okWithMessage("购买成功", "章节购买成功"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("transaction-service", "章节购买失败，请检查余额是否充足或是否已购买"));
            }
        } catch (IllegalArgumentException e) {
            logger.error("参数错误: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("transaction-service", "参数错误：" + e.getMessage()));
        } catch (Exception e) {
            logger.error("章节购买失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "章节购买失败：" + e.getMessage()));
        }
    }
    
    /**
     * 处理验证异常
     * @param ex 验证异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("参数验证失败：");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        });
        logger.error("参数验证失败: {}", errorMessage.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("transaction-service", errorMessage.toString()));
    }

    /**
     * 查询读者是否已购买指定章节
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 是否已购买
     */
    @GetMapping("/check")
    @Operation(summary = "查询章节购买状态", description = "查询某读者是否已购买指定小说章节")
    public ResponseEntity<ApiResponse<Boolean>> checkChapterPurchase(
            @Parameter(description = "读者ID", required = true) @RequestParam Long readerId,
            @Parameter(description = "小说ID", required = true) @RequestParam Long novelId,
            @Parameter(description = "章节ID", required = true) @RequestParam Long chapterId) {
        try {
            boolean isPurchased = purchaseService.checkIfChapterPurchased(readerId, novelId, chapterId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("查询成功", isPurchased));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "查询章节购买状态失败：" + e.getMessage()));
        }
    }

    /**
     * 处理CORS预检请求
     * @return CORS响应
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(summary = "处理CORS预检请求", description = "处理跨域资源共享预检请求")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}