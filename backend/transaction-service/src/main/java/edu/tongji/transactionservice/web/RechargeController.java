package edu.tongji.transactionservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.transactionservice.dto.RechargeRequestDto;
import edu.tongji.transactionservice.service.AlipayService;
import edu.tongji.transactionservice.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 充值API控制器
 */
@RestController
@RequestMapping("/api/Recharge")
@Tag(name = "充值API", description = "提供用户充值功能")
public class RechargeController {
    
    private static final Logger logger = LoggerFactory.getLogger(RechargeController.class);
    
    @Autowired
    private AlipayService alipayService;
    
    @Autowired
    private RechargeService rechargeService;
    
    /**
     * 发起充值并返回支付URL
     * @param rechargeRequest 充值请求
     * @return 支付URL
     */
    @PostMapping("/start")
    @Operation(summary = "发起充值", description = "用户发起充值请求，返回支付宝支付URL")
    public ResponseEntity<ApiResponse<Map<String, String>>> startRecharge(
            @Parameter(description = "充值请求", required = true) @RequestBody RechargeRequestDto rechargeRequest) {
        try {
            // 参数校验
            if (rechargeRequest.getAmount() == null || rechargeRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("transaction-service", "充值金额必须大于0"));
            }
            
            // 生成唯一订单号 (格式: recharge_[用户ID]_[时间戳])
            String outTradeNo = "recharge_" + rechargeRequest.getReaderId() + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            logger.info("发起充值请求，订单号: {}, 读者ID: {}, 充值金额: {}", 
                outTradeNo, rechargeRequest.getReaderId(), rechargeRequest.getAmount());
            
            // 调用支付宝创建支付订单
            String paymentUrl = alipayService.createPagePay(
                outTradeNo,
                rechargeRequest.getAmount(),
                "用户" + rechargeRequest.getReaderId() + "余额充值"
            );
            
            Map<String, String> result = new HashMap<>();
            result.put("PaymentUrl", paymentUrl);
            result.put("OutTradeNo", outTradeNo);
            
            return ResponseEntity.ok(ApiResponse.ok("transaction-service", "充值请求创建成功", result));
        } catch (Exception e) {
            logger.error("发起充值失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("transaction-service", "发起充值失败：" + e.getMessage()));
        }
    }
    
    /**
     * 支付宝异步通知（后台调用）
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/notify")
    @Operation(summary = "支付宝异步通知", description = "处理支付宝支付结果异步通知")
    public ResponseEntity<String> handleAlipayNotify(HttpServletRequest request) {
        try {
            logger.info("收到支付宝异步通知");
            
            // 1. 获取支付宝异步通知参数
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values != null && values.length > 0) {
                    params.put(key, values[0]);
                }
            });
            
            // 2. 验证支付宝签名
            if (!alipayService.verifyNotify(params)) {
                logger.error("支付宝签名验证失败");
                return ResponseEntity.badRequest().body("签名验证失败");
            }
            
            // 3. 解析订单信息
            String outTradeNo = params.get("out_trade_no");
            String totalAmount = params.get("total_amount");
            String tradeStatus = params.get("trade_status");
            
            if (outTradeNo == null || totalAmount == null || tradeStatus == null) {
                logger.error("支付宝异步通知参数不完整");
                return ResponseEntity.badRequest().body("参数不完整");
            }
            
            // 只处理支付成功的通知
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                logger.info("支付状态不是成功状态，跳过处理，状态: {}", tradeStatus);
                return ResponseEntity.ok("success");
            }
            
            // 从订单号提取用户ID (格式: recharge_[用户ID]_[时间戳])
            String[] parts = outTradeNo.split("_");
            if (parts.length < 3) {
                logger.error("订单号格式不正确: {}", outTradeNo);
                return ResponseEntity.badRequest().body("订单号格式不正确");
            }
            
            Long readerId;
            try {
                readerId = Long.parseLong(parts[1]);
            } catch (NumberFormatException e) {
                logger.error("从订单号解析读者ID失败: {}", outTradeNo);
                return ResponseEntity.badRequest().body("订单号格式不正确");
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(totalAmount);
            } catch (NumberFormatException e) {
                logger.error("解析充值金额失败: {}", totalAmount);
                return ResponseEntity.badRequest().body("金额格式不正确");
            }
            
            logger.info("处理支付宝异步通知，订单号: {}, 读者ID: {}, 金额: {}, 支付状态: {}", 
                outTradeNo, readerId, amount, tradeStatus);
            
            // 4. 调用充值服务
            RechargeRequestDto rechargeRequest = new RechargeRequestDto();
            rechargeRequest.setReaderId(readerId);
            rechargeRequest.setAmount(amount);
            
            try {
                boolean success = rechargeService.processRecharge(rechargeRequest);
                
                if (success) {
                    logger.info("充值处理成功，订单号: {}, 读者ID: {}, 金额: {}", outTradeNo, readerId, amount);
                    // 确保返回 success，支付宝需要这个响应
                    return ResponseEntity.ok("success");
                } else {
                    logger.error("充值处理失败，订单号: {}, 读者ID: {}, 金额: {}", outTradeNo, readerId, amount);
                    // 即使失败也返回 success，避免支付宝重复通知
                    return ResponseEntity.ok("success");
                }
            } catch (RuntimeException e) {
                // 如果业务逻辑执行了但返回时出错，仍然返回 success
                // 因为数据库可能已经更新了
                logger.error("充值处理过程中出现异常，但可能已部分成功，订单号: {}, 读者ID: {}, 金额: {}, 异常: {}", 
                    outTradeNo, readerId, amount, e.getMessage(), e);
                // 返回 success 避免支付宝重复通知
                return ResponseEntity.ok("success");
            }
        } catch (Exception e) {
            logger.error("处理支付宝异步通知异常: {}", e.getMessage(), e);
            // 即使出现异常也返回 success，避免支付宝重复通知
            // 实际业务中应该记录日志并人工处理
            return ResponseEntity.ok("success");
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