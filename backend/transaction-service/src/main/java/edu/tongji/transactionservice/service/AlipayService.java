package edu.tongji.transactionservice.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付服务
 */
@Service
public class AlipayService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlipayService.class);
    
    @Value("${alipay.server-url}")
    private String serverUrl;
    
    @Value("${alipay.app-id}")
    private String appId;
    
    @Value("${alipay.private-key}")
    private String privateKey;
    
    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;
    
    @Value("${alipay.charset:UTF-8}")
    private String charset;
    
    @Value("${alipay.sign-type:RSA2}")
    private String signType;
    
    @Value("${alipay.format:json}")
    private String format;
    
    @Value("${alipay.notify-url}")
    private String notifyUrl;
    
    @Value("${alipay.return-url}")
    private String returnUrl;
    
    private AlipayClient alipayClient;
    
    /**
     * 获取AlipayClient实例（懒加载）
     */
    private AlipayClient getAlipayClient() {
        if (alipayClient == null) {
            alipayClient = new DefaultAlipayClient(
                serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        }
        return alipayClient;
    }
    
    /**
     * 获取支付链接（直接返回可访问的URL）
     * 
     * @param outTradeNo 商户订单号
     * @param totalAmount 订单金额
     * @param subject 订单标题
     * @return 支付链接URL
     */
    public String createPagePay(String outTradeNo, BigDecimal totalAmount, String subject) {
        try {
            // 将BigDecimal转换为double
            double amount = totalAmount.doubleValue();
            logger.info("获取支付宝支付链接, outTradeNo: {}, totalAmount: {}, subject: {}", 
                outTradeNo, amount, subject);
            
            // 创建请求对象
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            
            // 设置业务参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(String.format("%.2f", amount));
            model.setSubject(subject);
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            
            request.setBizModel(model);
            
            // 设置异步通知地址
            if (notifyUrl != null && !notifyUrl.isEmpty()) {
                request.setNotifyUrl(notifyUrl);
                logger.info("设置notify_url: {}", notifyUrl);
            }
            
            // 设置同步跳转地址
            if (returnUrl != null && !returnUrl.isEmpty()) {
                request.setReturnUrl(returnUrl);
                logger.info("设置return_url: {}", returnUrl);
            }
            
            // 执行请求，使用GET方式获取支付URL
            AlipayTradePagePayResponse response = getAlipayClient().pageExecute(request, "GET");
            
            if (response.isSuccess()) {
                // 返回支付链接
                logger.info("支付宝支付链接创建成功, outTradeNo: {}", outTradeNo);
                return response.getBody();
            } else {
                logger.error("支付宝支付链接创建失败, outTradeNo: {}", outTradeNo);
                logger.error("错误代码: {}", response.getCode());
                logger.error("错误信息: {}", response.getMsg());
                throw new RuntimeException("支付宝接口调用失败: " + response.getMsg());
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝API调用异常: {}", e.getMessage(), e);
            throw new RuntimeException("支付宝API调用异常: " + e.getMessage(), e);
        }
    }
    
    /**
     * 格式化公钥（去除PEM头尾标记和换行符）
     */
    private String formatPublicKey(String publicKey) {
        if (publicKey == null) {
            return null;
        }
        return publicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "") // 去除所有空白字符
                .trim();
    }
    
    /**
     * 验证支付宝异步通知签名
     * @param params 支付宝回调参数
     * @return 验证是否通过
     */
    public boolean verifyNotify(Map<String, String> params) {
        try {
            String formattedPublicKey = formatPublicKey(alipayPublicKey);
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                formattedPublicKey,
                charset,
                signType
            );
            
            if (signVerified) {
                logger.info("支付宝异步通知签名验证成功");
                return true;
            } else {
                logger.warn("支付宝异步通知签名验证失败");
                return false;
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝异步通知签名验证异常", e);
            return false;
        }
    }
}
