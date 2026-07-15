package edu.tongji.transactionservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.transactionservice.entity.Transaction;
import edu.tongji.transactionservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@Tag(name = "交易记录API", description = "提供交易记录的查询、创建、更新和删除功能")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 获取读者的打赏记录
     * @param readerId 读者ID
     * @return 打赏记录列表
     */
    @GetMapping("/reward/{readerId}")
    @Operation(summary = "获取读者的打赏记录", description = "根据读者ID获取该读者的所有打赏记录")
    public ResponseEntity<ApiResponse<List<Transaction>>> getRewardTransactions(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        try {
            List<Transaction> transactions = transactionService.getRewardTransactionsByReaderId(readerId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取打赏记录失败：" + e.getMessage()));
        }
    }

    /**
     * 获取读者的订阅记录（解锁章节记录）
     * @param readerId 读者ID
     * @return 订阅记录列表
     */
    @GetMapping("/subscription/{readerId}")
    @Operation(summary = "获取读者的订阅记录", description = "根据读者ID获取该读者的所有订阅（解锁章节）记录")
    public ResponseEntity<ApiResponse<List<Transaction>>> getSubscriptionTransactions(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        try {
            List<Transaction> transactions = transactionService.getSubscriptionTransactionsByReaderId(readerId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取订阅记录失败：" + e.getMessage()));
        }
    }

    /**
     * 获取读者的充值记录
     * @param readerId 读者ID
     * @return 充值记录列表
     */
    @GetMapping("/recharge/{readerId}")
    @Operation(summary = "获取读者的充值记录", description = "根据读者ID获取该读者的所有充值记录")
    public ResponseEntity<ApiResponse<List<Transaction>>> getRechargeTransactions(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId) {
        try {
            List<Transaction> transactions = transactionService.getRechargeTransactionsByReaderId(readerId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取充值记录失败：" + e.getMessage()));
        }
    }

    /**
     * 获取读者的所有交易记录
     * @param readerId 读者ID
     * @param transType 可选，交易类型（"充值"/"打赏"/"订阅"/"解锁章节"）
     * @param timeRange 可选，时间范围（"all"/"year"/"month"/"week"）
     * @return 所有交易记录列表
     */
    @GetMapping("/transaction/{readerId}")
    @Operation(summary = "获取读者的所有交易记录", description = "根据读者ID获取该读者的所有交易记录，支持按交易类型和时间范围筛选")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactionsByReaderId(
            @Parameter(description = "读者ID", required = true) @PathVariable Long readerId,
            @Parameter(description = "交易类型（可选）") @RequestParam(required = false) String transType,
            @Parameter(description = "时间范围（可选：all/year/month/week）") @RequestParam(required = false) String timeRange) {
        try {
            List<Transaction> transactions;
            
            // 如果指定了交易类型和时间范围
            if (transType != null && !transType.isEmpty() && timeRange != null && !timeRange.isEmpty()) {
                transactions = transactionService.getTransactionsByReaderIdAndTypeAndTimeRange(readerId, transType, timeRange);
            }
            // 如果只指定了交易类型
            else if (transType != null && !transType.isEmpty()) {
                transactions = transactionService.getTransactionsByReaderIdAndType(readerId, transType);
            }
            // 如果只指定了时间范围
            else if (timeRange != null && !timeRange.isEmpty()) {
                transactions = transactionService.getTransactionsByReaderIdAndTimeRange(readerId, timeRange);
            }
            // 都不指定，返回所有记录
            else {
                transactions = transactionService.getAllTransactionsByReaderId(readerId);
            }
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取交易记录失败：" + e.getMessage()));
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

    /**
     * 获取所有交易记录
     * @return 所有交易记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有交易记录", description = "获取系统中的所有交易记录")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取所有交易记录失败：" + e.getMessage()));
        }
    }

    /**
     * 根据ID获取交易记录
     * @param id 交易ID
     * @return 交易记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取交易记录", description = "根据交易ID获取指定的交易记录")
    public ResponseEntity<ApiResponse<Transaction>> getTransactionById(
            @Parameter(description = "交易ID", required = true) @PathVariable Long id) {
        try {
            Optional<Transaction> transaction = transactionService.getTransactionById(id);
            if (transaction.isPresent()) {
                return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", transaction.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("transaction-service", "交易记录不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "获取交易记录失败：" + e.getMessage()));
        }
    }

    /**
     * 创建交易记录
     * @param transaction 交易记录
     * @return 创建的交易记录
     */
    @PostMapping
    @Operation(summary = "创建交易记录", description = "创建新的交易记录")
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(
            @Parameter(description = "交易记录", required = true) @RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.okWithMessage("创建成功", createdTransaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "创建交易记录失败：" + e.getMessage()));
        }
    }

    /**
     * 更新交易记录
     * @param id 交易ID
     * @param transaction 更新的交易记录
     * @return 更新后的交易记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新交易记录", description = "根据交易ID更新指定的交易记录")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @Parameter(description = "交易ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的交易记录", required = true) @RequestBody Transaction transaction) {
        try {
            Optional<Transaction> updatedTransaction = transactionService.updateTransaction(id, transaction);
            if (updatedTransaction.isPresent()) {
                return ResponseEntity.ok(ApiResponse.okWithMessage("更新成功", updatedTransaction.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("transaction-service", "交易记录不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "更新交易记录失败：" + e.getMessage()));
        }
    }

    /**
     * 删除交易记录
     * @param id 交易ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易记录", description = "根据交易ID删除指定的交易记录")
    public ResponseEntity<ApiResponse<String>> deleteTransaction(
            @Parameter(description = "交易ID", required = true) @PathVariable Long id) {
        try {
            boolean deleted = transactionService.deleteTransaction(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.okWithMessage("删除成功", "交易记录已删除"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("transaction-service", "交易记录不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "删除交易记录失败：" + e.getMessage()));
        }
    }

    /**
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping({"/ping"})
    @Operation(summary = "健康检查", description = "检查交易服务是否正常运行")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("transaction-service", "hello");
    }
}