package edu.tongji.transactionservice.service;

import edu.tongji.common.mq.MqConstants;
import edu.tongji.common.mq.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * transaction-service 订单事件生产者：通过 RabbitMQ 发布"订单回调"通知（章节购买 / 充值成功）。
 * 发送失败仅记录日志，不影响主交易流程（异步解耦）。
 */
@Service
public class OrderEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发布订单事件（定向推送给下单用户）。
     *
     * @param userId  目标用户 ID（读者）
     * @param title   标题
     * @param content 正文
     * @param data    业务数据（订单号、金额、小说/章节 ID 等）
     */
    public void publishOrderEvent(Long userId, String title, String content, Map<String, Object> data) {
        try {
            NotificationMessage message = new NotificationMessage(
                    MqConstants.TYPE_ORDER_EVENT,
                    userId,
                    title,
                    content,
                    data,
                    System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    MqConstants.EXCHANGE,
                    MqConstants.ORDER_EVENT_ROUTING_KEY,
                    message);
            logger.info("订单事件已发布: userId={}, title={}", userId, title);
        } catch (Exception e) {
            logger.error("发布订单事件失败: {}", e.getMessage(), e);
        }
    }
}
