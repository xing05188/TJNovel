package edu.tongji.transactionservice.service;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.transactionservice.dto.RechargeRequestDto;
import edu.tongji.transactionservice.entity.Transaction;
import edu.tongji.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 充值服务，封装充值业务逻辑
 */
@Service
public class RechargeService {
    
    private static final Logger logger = LoggerFactory.getLogger(RechargeService.class);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private WebClient userServiceWebClient;
    
    @Autowired
    private OrderEventProducer orderEventProducer;
    
    @Autowired
    private DistributedLockService distributedLockService;
    
    /**
     * 用户执行充值行为：创建交易，更新余额
     * @param rechargeRequest 充值请求
     * @return 是否成功
     */
    @Transactional
    public boolean processRecharge(RechargeRequestDto rechargeRequest) {
        Long readerId = rechargeRequest.getReaderId();
        BigDecimal amount = rechargeRequest.getAmount();

        // 幂等键：优先用支付宝回调单号 outTradeNo（同一笔支付只处理一次）；
        // 直充兜底用「读者+金额+分钟」防止重复点击
        String idempotentKey = buildRechargeIdempotentKey(rechargeRequest);
        // 分布式锁键：同一笔支付（或同一读者）串行化，避免并发重复加币
        String lockKey = "lock:recharge:" + (rechargeRequest.getOutTradeNo() != null
                && !rechargeRequest.getOutTradeNo().isBlank() ? rechargeRequest.getOutTradeNo() : readerId);

        if (!distributedLockService.tryLock(lockKey, 3, 10)) {
            throw new RuntimeException("充值处理繁忙，请稍后重试");
        }
        try {
            // 已处理过（支付宝重复回调 / 重复点击）直接返回成功，避免重复加币
            if (distributedLockService.idempotentExists(idempotentKey)) {
                logger.info("充值订单已处理(幂等拦截)，outTradeNo={}, readerId={}",
                        rechargeRequest.getOutTradeNo(), readerId);
                return true;
            }

            logger.info("开始处理充值请求，读者ID: {}, 充值金额: {}", readerId, amount);

            // 1. 创建"充值"类型的Transaction记录
            Transaction transaction = new Transaction(
                readerId,
                "充值",
                amount.multiply(BigDecimal.valueOf(100))
            );
            transaction = transactionRepository.save(transaction);
            logger.info("创建交易记录成功，交易ID: {}", transaction.getTransactionId());

            // 2. 增加读者的Balance余额（在事务外执行，避免阻塞）
            boolean updateSuccess = updateReaderBalance(readerId, amount.multiply(BigDecimal.valueOf(100)));
            if (!updateSuccess) {
                logger.error("更新读者余额失败，读者ID: {}, 增加金额: {}", readerId, amount);
                throw new RuntimeException("更新读者余额失败");
            }

            logger.info("充值处理成功，读者ID: {}, 充值金额: {}, 交易ID: {}",
                readerId, amount, transaction.getTransactionId());

            // 异步发布订单事件（订单回调）：通知读者充值结果
            try {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("amount", amount);
                orderData.put("transactionId", transaction.getTransactionId());
                orderEventProducer.publishOrderEvent(
                        readerId,
                        "充值成功",
                        "您已成功充值 " + amount + " 元，书币已到账",
                        orderData);
            } catch (Exception e) {
                logger.error("发布订单事件失败: {}", e.getMessage(), e);
            }

            // 成功后才写入幂等键（失败不写，允许重试），TTL 24h 覆盖支付宝重试窗口
            distributedLockService.markIdempotent(idempotentKey, 24 * 3600);
            return true;
        } finally {
            distributedLockService.unlock(lockKey);
        }
    }

    private String buildRechargeIdempotentKey(RechargeRequestDto rechargeRequest) {
        if (rechargeRequest.getOutTradeNo() != null && !rechargeRequest.getOutTradeNo().isBlank()) {
            return "recharge:done:" + rechargeRequest.getOutTradeNo();
        }
        // 直充兜底：同一读者、同一金额、同一分钟内视为重复点击
        long minuteBucket = System.currentTimeMillis() / 60000;
        return "recharge:direct:" + rechargeRequest.getReaderId() + ":" + rechargeRequest.getAmount() + ":" + minuteBucket;
    }
    
    /**
     * 更新读者余额
     * @param readerId 读者ID
     * @param amount 增加的金额
     * @return 是否成功
     */
    private boolean updateReaderBalance(Long readerId, BigDecimal amount) {
        try {
            logger.info("更新读者余额，读者ID: {}, 增加金额: {}", readerId, amount);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount);
            
            logger.info("准备调用 user-service，URL: /readers/{}/add-balance, 请求体: {}", readerId, requestBody);
            
            // 调用user-service增加读者余额（使用相对路径，baseUrl已在WebClient配置中设置）
            // 解析 ApiResponse<Boolean> 响应
            ApiResponse<Boolean> response = userServiceWebClient
                .post()
                .uri("/readers/{readerId}/add-balance", readerId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Boolean>>() {})
                .block();
            
            logger.info("收到 user-service 响应，读者ID: {}, 响应: status={}, data={}, message={}", 
                readerId, 
                response != null ? response.status() : "null",
                response != null && response.data() != null ? response.data() : "null",
                response != null ? response.message() : "null");
            
            if (response != null && "ok".equals(response.status()) && response.data() != null && response.data()) {
                logger.info("读者余额更新成功，读者ID: {}, 增加金额: {}", readerId, amount);
                return true;
            } else {
                logger.error("读者余额更新失败，读者ID: {}, 增加金额: {}, 响应状态: {}, 响应数据: {}, 响应消息: {}", 
                    readerId, amount, 
                    response != null ? response.status() : "null",
                    response != null && response.data() != null ? response.data() : "null",
                    response != null ? response.message() : "null");
                return false;
            }
        } catch (Exception e) {
            logger.error("更新读者余额异常，读者ID: {}, 增加金额: {}, 异常类型: {}, 异常消息: {}", 
                readerId, amount, e.getClass().getName(), e.getMessage(), e);
            return false;
        }
    }
}