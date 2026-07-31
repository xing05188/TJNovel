package edu.tongji.transactionservice.service;

import edu.tongji.transactionservice.dto.RewardRequestDto;
import edu.tongji.transactionservice.entity.AuthorIncome;
import edu.tongji.transactionservice.entity.Reward;
import edu.tongji.transactionservice.entity.Transaction;
import edu.tongji.transactionservice.repository.AuthorIncomeRepository;
import edu.tongji.transactionservice.repository.RewardRepository;
import edu.tongji.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 打赏服务类，实现打赏业务逻辑
 */
@Service
public class RewardService {
    
    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private RewardRepository rewardRepository;
    
    @Autowired
    private AuthorIncomeRepository authorIncomeRepository;
    
    @Autowired
    private WebClient userServiceWebClient;
    
    @Autowired
    private WebClient contentServiceWebClient;
    
    @Autowired
    private DistributedLockService distributedLockService;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * 执行打赏操作
     * 流程：
     * 1. 检查读者余额是否足够
     * 2. 创建"打赏"类型的Transaction记录
     * 3. 扣除读者余额
     * 4. 创建Reward记录（关联Transaction、小说ID）
     * 5. 创建"打赏"类型的AuthorIncome记录
     * 6. 增加作者收益和累计收入
     * 
     * @param rewardRequest 打赏请求
     * @return 是否成功
     */
    @Transactional
    public boolean processReward(RewardRequestDto rewardRequest) {
        Long readerId = rewardRequest.getReaderId();
        Long novelId = rewardRequest.getNovelId();
        BigDecimal amount = rewardRequest.getAmount();

        // 防重复打赏：同一「读者+小说+金额」在 5 秒内重复请求视为重复点击
        String idempotentKey = "reward:idempotent:" + readerId + ":" + novelId + ":" + amount;
        // 分布式锁：串行化同一「读者+小说」的并发打赏，避免并发重复扣币
        String lockKey = "lock:reward:" + readerId + ":" + novelId;

        if (!distributedLockService.tryLock(lockKey, 3, 10)) {
            throw new RuntimeException("打赏处理繁忙，请稍后重试");
        }
        try {
            if (distributedLockService.idempotentExists(idempotentKey)) {
                logger.warn("打赏请求重复(幂等拦截)，readerId={}, novelId={}, amount={}", readerId, novelId, amount);
                throw new RuntimeException("操作过于频繁，请勿重复打赏");
            }

            boolean success = doProcessReward(rewardRequest);
            // 仅成功时写入幂等键，失败（如余额不足）不写，允许重试
            if (success) {
                distributedLockService.markIdempotent(idempotentKey, 5);
            }
            return success;
        } finally {
            distributedLockService.unlock(lockKey);
        }
    }

    /**
     * 实际打赏业务逻辑（已在外层加锁与幂等保护）
     */
    private boolean doProcessReward(RewardRequestDto rewardRequest) {
        try {
            // 1. 检查读者余额是否足够
            BigDecimal readerBalance = getReaderBalance(rewardRequest.getReaderId());
            if (readerBalance == null || readerBalance.compareTo(rewardRequest.getAmount()) < 0) {
                logger.error("读者余额不足，读者ID: {}, 当前余额: {}, 打赏金额: {}", 
                    rewardRequest.getReaderId(), readerBalance, rewardRequest.getAmount());
                return false;
            }
            
            // 2. 创建"打赏"类型的Transaction记录
            Transaction transaction = new Transaction(
                rewardRequest.getReaderId(), 
                "打赏", 
                rewardRequest.getAmount()
            );
            transaction = transactionRepository.save(transaction);
            logger.info("创建交易记录成功，交易ID: {}", transaction.getTransactionId());
            
            // 3. 扣除读者余额
            boolean deductSuccess = deductReaderBalance(rewardRequest.getReaderId(), rewardRequest.getAmount());
            if (!deductSuccess) {
                logger.error("扣除读者余额失败，读者ID: {}, 扣除金额: {}", 
                    rewardRequest.getReaderId(), rewardRequest.getAmount());
                throw new RuntimeException("扣除读者余额失败");
            }
            logger.info("扣除读者余额成功，读者ID: {}, 扣除金额: {}", 
                rewardRequest.getReaderId(), rewardRequest.getAmount());
            
            // 4. 创建Reward记录（关联Transaction、小说ID）
            Reward reward = new Reward();
            reward.setTransactionId(transaction.getTransactionId());
            reward.setNovelId(rewardRequest.getNovelId());
            reward.setTransaction(transaction);
            // 使用 EntityManager 直接持久化，避免 merge 时的主键断言问题
            entityManager.persist(reward);
            logger.info("创建打赏记录成功，交易ID: {}, 小说ID: {}", 
                transaction.getTransactionId(), rewardRequest.getNovelId());
            
            // 5. 获取小说信息
            Long authorId = getNovelAuthorId(rewardRequest.getNovelId());
            if (authorId == null) {
                logger.error("获取小说信息失败，小说ID: {}", rewardRequest.getNovelId());
                throw new RuntimeException("小说不存在");
            }
            
            // 6. 创建"打赏"类型的AuthorIncome记录
            AuthorIncome authorIncome = new AuthorIncome();
            authorIncome.setAuthorId(authorId);
            authorIncome.setNovelId(rewardRequest.getNovelId());
            authorIncome.setType("打赏");
            authorIncome.setAmount(rewardRequest.getAmount());
            authorIncome.setCreateTime(LocalDateTime.now());
            authorIncomeRepository.save(authorIncome);
            logger.info("创建作者收入记录成功，作者ID: {}, 小说ID: {}, 金额: {}", 
                authorId, rewardRequest.getNovelId(), rewardRequest.getAmount());
            
            // 7. 增加作者收益和累计收入
            boolean updateSuccess = updateAuthorEarning(authorId, rewardRequest.getAmount());
            if (!updateSuccess) {
                logger.error("更新作者收入失败，作者ID: {}, 增加金额: {}", 
                    authorId, rewardRequest.getAmount());
                throw new RuntimeException("更新作者收入失败");
            }
            logger.info("更新作者收入成功，作者ID: {}, 增加金额: {}", 
                authorId, rewardRequest.getAmount());
            
            return true;
        } catch (Exception e) {
            logger.error("打赏处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("打赏处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取读者余额
     * @param readerId 读者ID
     * @return 读者余额
     */
    private BigDecimal getReaderBalance(Long readerId) {
        try {
            @SuppressWarnings("unchecked")
            Mono<Map> balanceMono = userServiceWebClient.get()
                    .uri("/readers/{readerId}/balance", readerId)
                    .retrieve()
                    .bodyToMono(Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) balanceMono.block();
            if (result != null) {
                String status = (String) result.get("status");
                if ("error".equals(status)) {
                    String errorMsg = (String) result.get("message");
                    logger.error("获取读者余额失败（业务错误），读者ID: {}, 错误消息: {}", readerId, errorMsg);
                    return null;
                }
                
                Object dataObj = result.get("data");
                if (dataObj != null) {
                    try {
                        BigDecimal balance = new BigDecimal(dataObj.toString());
                        logger.info("获取读者余额成功，读者ID: {}, 余额: {}", readerId, balance);
                        return balance;
                    } catch (NumberFormatException e) {
                        logger.error("读者余额格式转换失败，读者ID: {}, 原始值: {}", readerId, dataObj);
                        return null;
                    }
                } else {
                    logger.warn("获取读者余额响应中 data 为空，读者ID: {}, 响应: {}", readerId, result);
                }
            } else {
                logger.warn("获取读者余额响应为空，读者ID: {}", readerId);
            }
            return null;
        } catch (Exception e) {
            logger.error("获取读者余额失败，读者ID: {}, 错误: {}", readerId, e.getMessage());
            return null;
        }
    }
    
    /**
     * 扣除读者余额
     * @param readerId 读者ID
     * @param amount 扣除金额
     * @return 是否成功
     */
    private boolean deductReaderBalance(Long readerId, BigDecimal amount) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount);
            
            @SuppressWarnings("unchecked")
            Mono<Map> resultMono = userServiceWebClient.post()
                    .uri("/readers/{readerId}/deduct-balance", readerId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) resultMono.block();
            if (result != null) {
                String status = (String) result.get("status");
                if ("error".equals(status)) {
                    String errorMsg = (String) result.get("message");
                    logger.error("扣除读者余额失败（业务错误），读者ID: {}, 扣除金额: {}, 错误消息: {}", 
                        readerId, amount, errorMsg);
                    return false;
                }
                
                Object dataObj = result.get("data");
                if (dataObj instanceof Boolean) {
                    boolean success = (Boolean) dataObj;
                    if (!success) {
                        logger.error("扣除读者余额失败（余额不足或业务返回失败），读者ID: {}, 扣除金额: {}", 
                            readerId, amount);
                    } else {
                        logger.info("扣除读者余额成功，读者ID: {}, 扣除金额: {}", readerId, amount);
                    }
                    return success;
                } else if (dataObj != null) {
                    boolean success = Boolean.parseBoolean(dataObj.toString());
                    if (!success) {
                        logger.error("扣除读者余额失败（解析后为 false），读者ID: {}, 扣除金额: {}, 原始 data: {}", 
                            readerId, amount, dataObj);
                    } else {
                        logger.info("扣除读者余额成功（解析自字符串），读者ID: {}, 扣除金额: {}", readerId, amount);
                    }
                    return success;
                } else {
                    logger.error("扣除读者余额响应中 data 为空，读者ID: {}, 扣除金额: {}, 响应: {}", 
                        readerId, amount, result);
                }
            } else {
                logger.error("扣除读者余额响应为空，读者ID: {}, 扣除金额: {}", readerId, amount);
            }
            return false;
        } catch (Exception e) {
            logger.error("扣除读者余额失败，读者ID: {}, 扣除金额: {}, 错误: {}", 
                readerId, amount, e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取小说的作者ID
     * @param novelId 小说ID
     * @return 作者ID
     */
    private Long getNovelAuthorId(Long novelId) {
        try {
            @SuppressWarnings("unchecked")
            Mono<Map> novelMono = contentServiceWebClient.get()
                    .uri("/novels/{novelId}/author-id", novelId)
                    .retrieve()
                    .bodyToMono(Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) novelMono.block();
            if (result != null) {
                // ApiResponse 格式：{ service, status, data, message }
                Object dataObj = result.get("data");
                if (dataObj instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    Object authorIdObj = data.get("authorId");
                    if (authorIdObj != null) {
                        Long authorId = ((Number) authorIdObj).longValue();
                        logger.info("获取小说作者ID成功，小说ID: {}, 作者ID: {}", novelId, authorId);
                        return authorId;
                    }
                } else if (result.containsKey("authorId")) {
                    // 兼容直接返回 authorId 的情况
                    Long authorId = ((Number) result.get("authorId")).longValue();
                    logger.info("获取小说作者ID成功（兼容格式），小说ID: {}, 作者ID: {}", novelId, authorId);
                    return authorId;
                }
            }
            logger.warn("获取小说作者ID失败，小说ID: {}, 响应: {}", novelId, result);
            return null;
        } catch (Exception e) {
            logger.error("获取小说作者ID失败，小说ID: {}, 错误: {}", novelId, e.getMessage());
            return null;
        }
    }
    
    /**
     * 更新作者收入
     * @param authorId 作者ID
     * @param amount 增加金额
     * @return 是否成功
     */
    private boolean updateAuthorEarning(Long authorId, BigDecimal amount) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount);
            
            @SuppressWarnings("unchecked")
            Mono<Map> resultMono = userServiceWebClient.post()
                    .uri("/authors/{authorId}/add-earning", authorId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) resultMono.block();
            if (result != null) {
                String status = (String) result.get("status");
                if ("error".equals(status)) {
                    String errorMsg = (String) result.get("message");
                    logger.error("更新作者收入失败（业务错误），作者ID: {}, 增加金额: {}, 错误消息: {}", 
                        authorId, amount, errorMsg);
                    return false;
                }
                
                Object dataObj = result.get("data");
                if (dataObj instanceof Boolean) {
                    boolean success = (Boolean) dataObj;
                    if (!success) {
                        logger.error("更新作者收入失败（服务返回 false），作者ID: {}, 增加金额: {}", 
                            authorId, amount);
                    } else {
                        logger.info("更新作者收入成功，作者ID: {}, 增加金额: {}", authorId, amount);
                    }
                    return success;
                } else if (dataObj != null) {
                    boolean success = Boolean.parseBoolean(dataObj.toString());
                    if (!success) {
                        logger.error("更新作者收入失败（解析后为 false），作者ID: {}, 增加金额: {}, 原始 data: {}", 
                            authorId, amount, dataObj);
                    } else {
                        logger.info("更新作者收入成功（解析自字符串），作者ID: {}, 增加金额: {}", authorId, amount);
                    }
                    return success;
                } else {
                    logger.error("更新作者收入响应中 data 为空，作者ID: {}, 增加金额: {}, 响应: {}", 
                        authorId, amount, result);
                }
            } else {
                logger.error("更新作者收入响应为空，作者ID: {}, 增加金额: {}", authorId, amount);
            }
            return false;
        } catch (Exception e) {
            logger.error("更新作者收入失败，作者ID: {}, 增加金额: {}, 错误: {}", 
                authorId, amount, e.getMessage());
            return false;
        }
    }
}