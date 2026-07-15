package edu.tongji.contentservice.service;

import edu.tongji.contentservice.dto.WholePurchaseDto;
import edu.tongji.contentservice.entity.WholePurchase;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.repository.NovelRepository;
import edu.tongji.contentservice.repository.WholePurchaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WholePurchaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(WholePurchaseService.class);
    
    @Autowired
    private WholePurchaseRepository wholePurchaseRepository;
    
    @Autowired
    private NovelRepository novelRepository;
    
    @Autowired
    private WebClient webClient;
    
    @Value("${services.user:http://localhost:7081}")
    private String userService;
    
    @Value("${services.transaction:http://localhost:7083}")
    private String transactionService;
    
    /**
     * 整本购买小说
     * @param wholePurchaseDto 购买请求DTO
     * @return 购买记录
     */
    @Transactional
    public WholePurchase purchaseNovel(WholePurchaseDto wholePurchaseDto) {
        try {
            // 1. 检查小说是否存在并获取价格
            Optional<Novel> novelOpt = novelRepository.findById(wholePurchaseDto.getNovelId());
            if (novelOpt.isEmpty()) {
                throw new RuntimeException("小说不存在");
            }
            Novel novel = novelOpt.get();
            BigDecimal novelPrice = novel.getTotalPrice();
            
            // 2. 检查是否已经购买
            Optional<WholePurchase> existingPurchase = wholePurchaseRepository.findByReaderIdAndNovelId(
                    wholePurchaseDto.getReaderId(), wholePurchaseDto.getNovelId());
            
            if (existingPurchase.isPresent()) {
                WholePurchase purchase = existingPurchase.get();
                if ("是".equals(purchase.getIsBought())) {
                    throw new RuntimeException("已经购买过该小说");
                }
                // 更新为已购买状态
                purchase.setIsBought("是");
                wholePurchaseRepository.save(purchase);
                
                // 继续执行后续流程（创建AuthorIncome记录和更新作者收益）
                processAuthorIncome(novel.getAuthorId(), wholePurchaseDto.getNovelId(), novelPrice);
                updateAuthorEarning(novel.getAuthorId(), novelPrice);
                
                return purchase;
            } else {
                // 3. 检查读者余额是否足够
                BigDecimal readerBalance = getReaderBalance(wholePurchaseDto.getReaderId());
                if (readerBalance == null || readerBalance.compareTo(novelPrice) < 0) {
                    logger.error("读者余额不足，读者ID: {}, 当前余额: {}, 小说价格: {}", 
                        wholePurchaseDto.getReaderId(), readerBalance, novelPrice);
                    throw new RuntimeException("余额不足，无法购买");
                }
                
                // 4. 扣除读者余额
                boolean deductSuccess = deductReaderBalance(wholePurchaseDto.getReaderId(), novelPrice);
                if (!deductSuccess) {
                    logger.error("扣除读者余额失败，读者ID: {}, 扣除金额: {}", 
                        wholePurchaseDto.getReaderId(), novelPrice);
                    throw new RuntimeException("扣除余额失败");
                }
                
                // 5. 创建新的购买记录
                WholePurchase newPurchase = new WholePurchase(
                        wholePurchaseDto.getReaderId(), 
                        wholePurchaseDto.getNovelId(), 
                        "是");
                newPurchase = wholePurchaseRepository.save(newPurchase);
                
                // 6. 创建"整本买断"类型的AuthorIncome记录
                processAuthorIncome(novel.getAuthorId(), wholePurchaseDto.getNovelId(), novelPrice);
                
                // 7. 增加作者收益和累计收入
                updateAuthorEarning(novel.getAuthorId(), novelPrice);
                
                return newPurchase;
            }
        } catch (Exception e) {
            logger.error("整本购买处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("整本购买处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取读者余额
     * @param readerId 读者ID
     * @return 读者余额
     */
    private BigDecimal getReaderBalance(Long readerId) {
        try {
            return webClient.get()
                    .uri(userService + "/readers/" + readerId + "/balance")
                    .retrieve()
                    .bodyToMono(BigDecimal.class)
                    .block();
        } catch (Exception e) {
            logger.error("获取读者余额失败，读者ID: {}", readerId, e);
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
            
            Boolean result = webClient.post()
                    .uri(userService + "/readers/" + readerId + "/deduct-balance")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("扣除读者余额失败，读者ID: {}, 扣除金额: {}", readerId, amount, e);
            return false;
        }
    }
    
    /**
     * 创建AuthorIncome记录
     * @param authorId 作者ID
     * @param novelId 小说ID
     * @param amount 金额
     */
    private void processAuthorIncome(Long authorId, Long novelId, BigDecimal amount) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("authorId", authorId);
            requestBody.put("type", "整本买断");
            requestBody.put("amount", amount);
            requestBody.put("novelId", novelId);
            requestBody.put("createTime", LocalDateTime.now());
            
            webClient.post()
                    .uri(transactionService + "/author-income")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
                    
            logger.info("创建AuthorIncome记录成功，作者ID: {}, 小说ID: {}, 金额: {}", authorId, novelId, amount);
        } catch (Exception e) {
            logger.error("创建AuthorIncome记录失败，作者ID: {}, 小说ID: {}, 金额: {}", authorId, novelId, amount, e);
            throw new RuntimeException("创建收入记录失败");
        }
    }
    
    /**
     * 更新作者收益
     * @param authorId 作者ID
     * @param amount 增加金额
     */
    private void updateAuthorEarning(Long authorId, BigDecimal amount) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount);
            
            Boolean result = webClient.post()
                    .uri(userService + "/authors/" + authorId + "/add-earning")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            
            if (!Boolean.TRUE.equals(result)) {
                throw new RuntimeException("更新作者收益失败");
            }
            
            logger.info("更新作者收益成功，作者ID: {}, 增加金额: {}", authorId, amount);
        } catch (Exception e) {
            logger.error("更新作者收益失败，作者ID: {}, 增加金额: {}", authorId, amount, e);
            throw new RuntimeException("更新作者收益失败");
        }
    }
    
    /**
     * 查询读者是否已整本买断某小说
     * @param readerId 读者ID
     * @param novelId 小说ID
     * @return 是否已购买
     */
    public boolean checkPurchaseStatus(Long readerId, Long novelId) {
        return wholePurchaseRepository.existsByReaderIdAndNovelIdAndIsBought(readerId, novelId, "是");
    }
}