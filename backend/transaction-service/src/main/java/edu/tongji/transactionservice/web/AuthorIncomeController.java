package edu.tongji.transactionservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.transactionservice.entity.AuthorIncome;
import edu.tongji.transactionservice.service.AuthorIncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 作者收入控制器
 * 提供作者收入相关的API
 */
@RestController
@RequestMapping("/author-income")
@Tag(name = "作者收入管理", description = "作者收入相关接口")
public class AuthorIncomeController {
    
    @Autowired
    private AuthorIncomeService authorIncomeService;
    
    /**
     * 获取作者的收入记录
     * @param authorId 作者ID
     * @return 收入记录列表
     */
    @GetMapping("/list/{authorId}")
    @Operation(summary = "获取作者的收入记录", description = "根据作者ID获取该作者的所有收入记录")
    public ResponseEntity<ApiResponse<List<AuthorIncome>>> getIncomeRecordsByAuthorId(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId) {
        try {
            List<AuthorIncome> incomeRecords = authorIncomeService.getIncomeRecordsByAuthorId(authorId);
            return ResponseEntity.ok(ApiResponse.ok("transaction-service", incomeRecords));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取收入记录失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取作者的总收入
     * @param authorId 作者ID
     * @return 总收入
     */
    @GetMapping("/total/{authorId}")
    @Operation(summary = "获取作者的总收入", description = "根据作者ID获取该作者的总收入")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalIncomeByAuthorId(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId) {
        try {
            BigDecimal totalIncome = authorIncomeService.getTotalIncomeByAuthorId(authorId);
            return ResponseEntity.ok(ApiResponse.ok("transaction-service", totalIncome));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取总收入失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建作者收入记录
     * @param requestBody 包含作者收入信息的请求体
     * @return 创建的作者收入记录
     */
    @PostMapping
    @Operation(summary = "创建作者收入记录", description = "创建新的作者收入记录")
    public ResponseEntity<ApiResponse<AuthorIncome>> createAuthorIncome(
            @Parameter(description = "作者收入信息", required = true) @RequestBody Map<String, Object> requestBody) {
        try {
            Long authorId = Long.valueOf(requestBody.get("authorId").toString());
            String type = requestBody.get("type").toString();
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
            
            AuthorIncome authorIncome = new AuthorIncome(authorId, type, amount);
            
            // 如果提供了novelId，则设置
            if (requestBody.containsKey("novelId") && requestBody.get("novelId") != null) {
                Long novelId = Long.valueOf(requestBody.get("novelId").toString());
                authorIncome.setNovelId(novelId);
            }
            
            AuthorIncome savedIncome = authorIncomeService.createAuthorIncome(authorIncome);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.okWithMessage("创建成功", savedIncome));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "创建作者收入记录失败：" + e.getMessage()));
        }
    }
}