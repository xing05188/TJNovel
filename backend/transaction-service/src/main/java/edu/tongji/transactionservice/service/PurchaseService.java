package edu.tongji.transactionservice.service;

import edu.tongji.transactionservice.dto.ChapterPurchaseDto;
import edu.tongji.transactionservice.entity.AuthorIncome;
import edu.tongji.transactionservice.entity.Purchase;
import edu.tongji.transactionservice.entity.Transaction;
import edu.tongji.transactionservice.repository.AuthorIncomeRepository;
import edu.tongji.transactionservice.repository.PurchaseRepository;
import edu.tongji.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 章节购买服务类，实现章节购买业务逻辑
 */
@Service
public class PurchaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);
    
    /**
     * 章节价格信息内部类
     */
    private static class ChapterPriceInfo {
        BigDecimal price;
        boolean isCharged;
        
        ChapterPriceInfo(BigDecimal price, boolean isCharged) {
            this.price = price;
            this.isCharged = isCharged;
        }
    }
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private AuthorIncomeRepository authorIncomeRepository;
    
    @Autowired
    private WebClient userServiceWebClient;
    
    @Autowired
    private WebClient contentServiceWebClient;

    @Autowired
    private OrderEventProducer orderEventProducer;

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * 执行章节购买操作
     * 流程：
     * 1. 检查读者是否已购买该章节
     * 2. 获取章节价格
     * 3. 检查读者余额是否足够
     * 4. 创建"解锁章节"类型的Transaction记录
     * 5. 扣除读者余额
     * 6. 创建Purchase记录（关联Transaction、小说ID、章节ID）
     * 7. 获取小说信息
     * 8. 创建"章节购买"类型的AuthorIncome记录
     * 9. 增加作者收益和累计收入
     * 
     * @param chapterPurchaseRequest 章节购买请求
     * @return 是否成功
     */
    @Transactional
    public boolean processChapterPurchase(ChapterPurchaseDto chapterPurchaseRequest) {
        try {
            // 1. 检查读者是否已购买该章节
            boolean alreadyPurchased = checkIfChapterPurchased(
                chapterPurchaseRequest.getReaderId(),
                chapterPurchaseRequest.getNovelId(),
                chapterPurchaseRequest.getChapterId()
            );
            
            if (alreadyPurchased) {
                logger.error("读者已购买该章节，读者ID: {}, 小说ID: {}, 章节ID: {}", 
                    chapterPurchaseRequest.getReaderId(), 
                    chapterPurchaseRequest.getNovelId(), 
                    chapterPurchaseRequest.getChapterId());
                return false;
            }
            
            // 2. 获取章节价格和收费状态
            ChapterPriceInfo priceInfo = getChapterPriceInfo(
                chapterPurchaseRequest.getNovelId(),
                chapterPurchaseRequest.getChapterId()
            );
            
            if (priceInfo == null) {
                logger.error("获取章节信息失败，读者ID: {}, 小说ID: {}, 章节ID: {}", 
                    chapterPurchaseRequest.getReaderId(), 
                    chapterPurchaseRequest.getNovelId(), 
                    chapterPurchaseRequest.getChapterId());
                throw new IllegalArgumentException("章节不存在或获取章节信息失败");
            }
            
            BigDecimal chapterPrice = priceInfo.price;
            
            // === 免费章节：不创建交易/购买记录，直接视为已解锁 ===
            if (!priceInfo.isCharged) {
                logger.info("章节为免费，不创建交易和购买记录，直接返回成功，读者ID: {}, 小说ID: {}, 章节ID: {}", 
                    chapterPurchaseRequest.getReaderId(), 
                    chapterPurchaseRequest.getNovelId(), 
                    chapterPurchaseRequest.getChapterId());
                return true;
            }
            
            // === 付费章节：统一按付费流程处理（价格由内容服务返回，可为 0，也可大于 0） ===
            if (chapterPrice == null) {
                logger.error("付费章节价格为空，小说ID: {}, 章节ID: {}", 
                    chapterPurchaseRequest.getNovelId(), 
                    chapterPurchaseRequest.getChapterId());
                throw new IllegalArgumentException("付费章节价格不能为空");
            }
            // 3. 检查读者余额是否足够
            BigDecimal readerBalance = getReaderBalance(chapterPurchaseRequest.getReaderId());
            if (readerBalance == null) {
                logger.error("获取读者余额失败，读者ID: {}", chapterPurchaseRequest.getReaderId());
                throw new RuntimeException("获取读者余额失败，请稍后重试");
            }
            if (readerBalance.compareTo(chapterPrice) < 0) {
                logger.error("读者余额不足，读者ID: {}, 当前余额: {}, 章节价格: {}", 
                    chapterPurchaseRequest.getReaderId(), readerBalance, chapterPrice);
                throw new RuntimeException("余额不足，当前余额: " + readerBalance + "，章节价格: " + chapterPrice);
            }
            
            // 4. 创建"解锁章节"类型的Transaction记录
            Transaction transaction = new Transaction(
                chapterPurchaseRequest.getReaderId(), 
                "解锁章节", 
                chapterPrice
            );
            transaction = transactionRepository.save(transaction);
            logger.info("创建交易记录成功，交易ID: {}", transaction.getTransactionId());
            
            // 5. 扣除读者余额
            boolean deductSuccess = deductReaderBalance(
                chapterPurchaseRequest.getReaderId(), 
                chapterPrice
            );
            
            if (!deductSuccess) {
                logger.error("扣除读者余额失败，读者ID: {}, 扣除金额: {}", 
                    chapterPurchaseRequest.getReaderId(), chapterPrice);
                throw new RuntimeException("扣除读者余额失败");
            }
            logger.info("扣除读者余额成功，读者ID: {}, 扣除金额: {}", 
                chapterPurchaseRequest.getReaderId(), chapterPrice);
            
            // 6. 创建Purchase记录（关联Transaction、小说ID、章节ID）
            Purchase purchase = new Purchase();
            purchase.setTransactionId(transaction.getTransactionId());
            purchase.setNovelId(chapterPurchaseRequest.getNovelId());
            purchase.setChapterId(chapterPurchaseRequest.getChapterId());
            purchase.setTransaction(transaction);
            // 直接使用 EntityManager 持久化，避免 merge 过程中因标识处理导致的断言错误
            entityManager.persist(purchase);
            logger.info("创建购买记录成功，交易ID: {}, 小说ID: {}, 章节ID: {}", 
                transaction.getTransactionId(), 
                chapterPurchaseRequest.getNovelId(), 
                chapterPurchaseRequest.getChapterId());
            
            // 7. 获取小说信息
            Long authorId = getNovelAuthorId(chapterPurchaseRequest.getNovelId());
            if (authorId == null) {
                logger.error("获取小说信息失败，小说ID: {}", chapterPurchaseRequest.getNovelId());
                throw new RuntimeException("小说不存在");
            }
            
            // 8. 创建"章节购买"类型的AuthorIncome记录
            AuthorIncome authorIncome = new AuthorIncome();
            authorIncome.setAuthorId(authorId);
            authorIncome.setNovelId(chapterPurchaseRequest.getNovelId());
            authorIncome.setType("章节购买");
            authorIncome.setAmount(chapterPrice);
            authorIncome.setCreateTime(LocalDateTime.now());
            authorIncomeRepository.save(authorIncome);
            logger.info("创建作者收入记录成功，作者ID: {}, 小说ID: {}, 金额: {}", 
                authorId, chapterPurchaseRequest.getNovelId(), chapterPrice);
            
            // 9. 增加作者收益和累计收入
            boolean updateSuccess = updateAuthorEarning(authorId, chapterPrice);
            if (!updateSuccess) {
                logger.error("更新作者收入失败，作者ID: {}, 增加金额: {}", 
                    authorId, chapterPrice);
                throw new RuntimeException("更新作者收入失败");
            }
            logger.info("更新作者收入成功，作者ID: {}, 增加金额: {}", 
                authorId, chapterPrice);
            
            // 异步发布订单事件（订单回调）：通知读者本次购买结果
            try {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("novelId", chapterPurchaseRequest.getNovelId());
                orderData.put("chapterId", chapterPurchaseRequest.getChapterId());
                orderData.put("amount", chapterPrice);
                orderData.put("transactionId", transaction.getTransactionId());
                orderEventProducer.publishOrderEvent(
                        chapterPurchaseRequest.getReaderId(),
                        "章节购买成功",
                        "您已成功解锁章节，消费 " + chapterPrice + " 书币",
                        orderData);
            } catch (Exception e) {
                logger.error("发布订单事件失败: {}", e.getMessage(), e);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("章节购买处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("章节购买处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查读者是否已购买/解锁指定章节
     * 免费章节视为已解锁，付费章节查询 Purchase 表
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 是否已购买/解锁
     */
    public boolean checkIfChapterPurchased(Long readerId, Long novelId, Long chapterId) {
        // 先检查章节是否免费
        try {
            ChapterPriceInfo priceInfo = getChapterPriceInfo(novelId, chapterId);
            if (priceInfo != null && !priceInfo.isCharged) {
                // 免费章节，视为已解锁
                logger.info("章节为免费，视为已解锁，读者ID: {}, 小说ID: {}, 章节ID: {}", readerId, novelId, chapterId);
                return true;
            }
        } catch (Exception e) {
            // 获取章节信息失败，继续检查购买记录
            logger.warn("获取章节信息失败，将检查购买记录，小说ID: {}, 章节ID: {}, 错误: {}", novelId, chapterId, e.getMessage());
        }
        
        // 付费章节，查询购买记录
        return purchaseRepository.existsByReaderIdAndNovelIdAndChapterId(readerId, novelId, chapterId);
    }
    
    /**
     * 获取章节价格信息
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 章节价格信息，如果章节不存在则返回null
     */
    private ChapterPriceInfo getChapterPriceInfo(Long novelId, Long chapterId) {
        try {
            logger.info("开始获取章节价格信息，小说ID: {}, 章节ID: {}", novelId, chapterId);
            
            @SuppressWarnings("unchecked")
            Mono<Map> chapterMono = contentServiceWebClient.get()
                    .uri("/chapters/novels/{novelId}/chapters/{chapterId}/price", novelId, chapterId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> {
                                logger.error("获取章节价格失败，HTTP状态: {}, 小说ID: {}, 章节ID: {}", 
                                    response.statusCode(), novelId, chapterId);
                                if (response.statusCode().value() == 404) {
                                    return Mono.error(new RuntimeException("章节不存在，小说ID: " + novelId + ", 章节ID: " + chapterId));
                                }
                                return Mono.error(new RuntimeException("获取章节价格失败，HTTP状态: " + response.statusCode()));
                            })
                    .bodyToMono(Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) chapterMono.block();
            logger.debug("获取章节价格响应，小说ID: {}, 章节ID: {}, 响应: {}", novelId, chapterId, result);
            
            if (result != null) {
                // 检查响应格式 - ApiResponse格式：{service, status, data, message}
                String status = (String) result.get("status");
                if ("error".equals(status)) {
                    String errorMsg = (String) result.get("message");
                    logger.error("获取章节价格失败，错误消息: {}, 小说ID: {}, 章节ID: {}", errorMsg, novelId, chapterId);
                    throw new RuntimeException("获取章节价格失败: " + errorMsg);
                }
                
                Object dataObj = result.get("data");
                if (dataObj instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    // 检查章节是否收费
                    String isChargedStr = (String) data.get("isCharged");
                    boolean isCharged = isChargedStr != null && ("是".equals(isChargedStr) || "yes".equalsIgnoreCase(isChargedStr) || "true".equalsIgnoreCase(isChargedStr));
                    
                    // 获取计算价格
                    Object priceObj = data.get("calculatedPrice");
                    BigDecimal price = null;
                    if (priceObj != null) {
                        try {
                            price = new BigDecimal(priceObj.toString());
                        } catch (NumberFormatException e) {
                            logger.error("价格格式转换失败，价格值: {}, 小说ID: {}, 章节ID: {}", priceObj, novelId, chapterId);
                            price = BigDecimal.ZERO;
                        }
                    }
                    
                    logger.info("获取章节价格信息成功，小说ID: {}, 章节ID: {}, 是否收费: {}, 价格: {}", 
                        novelId, chapterId, isCharged, price);
                    return new ChapterPriceInfo(price, isCharged);
                } else if (result.containsKey("calculatedPrice")) {
                    // 兼容直接返回价格信息的情况
                    Object priceObj = result.get("calculatedPrice");
                    BigDecimal price = null;
                    if (priceObj != null) {
                        try {
                            price = new BigDecimal(priceObj.toString());
                        } catch (NumberFormatException e) {
                            logger.error("价格格式转换失败，价格值: {}, 小说ID: {}, 章节ID: {}", priceObj, novelId, chapterId);
                            price = BigDecimal.ZERO;
                        }
                    }
                    boolean isCharged = price != null && price.compareTo(BigDecimal.ZERO) > 0;
                    logger.info("获取章节价格信息成功（兼容格式），小说ID: {}, 章节ID: {}, 价格: {}", 
                        novelId, chapterId, price);
                    return new ChapterPriceInfo(price, isCharged);
                } else {
                    logger.warn("章节价格信息格式不正确，缺少data字段，小说ID: {}, 章节ID: {}, 响应: {}", novelId, chapterId, result);
                }
            } else {
                logger.warn("章节价格信息响应为空，小说ID: {}, 章节ID: {}", novelId, chapterId);
            }
            return null;
        } catch (RuntimeException e) {
            // 重新抛出RuntimeException，让上层处理
            throw e;
        } catch (Exception e) {
            logger.error("获取章节价格失败，小说ID: {}, 章节ID: {}, 错误: {}", novelId, chapterId, e.getMessage(), e);
            throw new RuntimeException("获取章节价格失败: " + e.getMessage(), e);
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
                // user-service 使用统一的 ApiResponse 包装：{ service, status, data, message }
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
                    // 兼容非布尔类型的情况
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
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> {
                                logger.error("获取小说作者ID失败，HTTP状态: {}, 小说ID: {}", 
                                    response.statusCode(), novelId);
                                if (response.statusCode().value() == 404) {
                                    return Mono.error(new RuntimeException("小说不存在，ID: " + novelId));
                                }
                                return Mono.error(new RuntimeException("获取小说作者ID失败，HTTP状态: " + response.statusCode()));
                            })
                    .bodyToMono(Map.class);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) novelMono.block();
            if (result != null) {
                // 检查响应格式 - ApiResponse格式：{service, status, data, message}
                String status = (String) result.get("status");
                if ("error".equals(status)) {
                    String errorMsg = (String) result.get("message");
                    logger.error("获取小说作者ID失败，错误消息: {}, 小说ID: {}", errorMsg, novelId);
                    throw new RuntimeException("获取小说作者ID失败: " + errorMsg);
                }
                
                Object dataObj = result.get("data");
                if (dataObj instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    Object authorIdObj = data.get("authorId");
                    if (authorIdObj != null) {
                        return ((Number) authorIdObj).longValue();
                    }
                } else if (result.containsKey("authorId")) {
                    // 兼容直接返回作者ID的情况
                    return ((Number) result.get("authorId")).longValue();
                }
            }
            logger.warn("小说作者ID信息格式不正确，小说ID: {}, 响应: {}", novelId, result);
            return null;
        } catch (RuntimeException e) {
            // 重新抛出RuntimeException，让上层处理
            throw e;
        } catch (Exception e) {
            logger.error("获取小说作者ID失败，小说ID: {}, 错误: {}", novelId, e.getMessage(), e);
            throw new RuntimeException("获取小说作者ID失败: " + e.getMessage(), e);
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