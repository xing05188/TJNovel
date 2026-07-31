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

    /**
     * 商户订单号 / 第三方支付流水号（支付宝回调会回传）。
     * 用于幂等去重：相同 outTradeNo 只会处理一次。直充场景可为空，由服务端按"用户+金额+时间窗"兜底防重。
     */
    private String outTradeNo;
}