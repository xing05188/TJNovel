package edu.tongji.transactionservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 打赏请求DTO
 */
public class RewardRequestDto {
    
    @NotNull(message = "读者ID不能为空")
    private Long readerId;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    @NotNull(message = "打赏金额不能为空")
    @Positive(message = "打赏金额必须大于0")
    private BigDecimal amount;
    
    // 默认构造函数
    public RewardRequestDto() {
    }
    
    // 带参数的构造函数
    public RewardRequestDto(Long readerId, Long novelId, BigDecimal amount) {
        this.readerId = readerId;
        this.novelId = novelId;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}