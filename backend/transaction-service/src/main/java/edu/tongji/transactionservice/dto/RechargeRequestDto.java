package edu.tongji.transactionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值请求DTO
 */
@Data
public class RechargeRequestDto {
    
    @NotNull(message = "读者ID不能为空")
    private Long readerId;
    
    @NotNull(message = "充值金额不能为空")
    private BigDecimal amount;
}