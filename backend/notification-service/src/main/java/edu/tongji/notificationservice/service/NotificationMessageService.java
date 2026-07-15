package edu.tongji.notificationservice.service;

import edu.tongji.notificationservice.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 通知消息发送服务（基于 RabbitMQ 替代 Azure Service Bus）
 */
@Service
public class NotificationMessageService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMessageService.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送通知消息
     */
    public void sendNotification(Map<String, Object> message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    message);
            logger.info("通知消息已发送: {}", message);
        } catch (Exception e) {
            logger.error("发送通知消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送小说更新通知
     */
    public void sendNovelUpdate(Map<String, Object> message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOVEL_UPDATE_EXCHANGE,
                    RabbitMQConfig.NOVEL_UPDATE_ROUTING_KEY,
                    message);
            logger.info("小说更新通知已发送: {}", message);
        } catch (Exception e) {
            logger.error("发送小说更新通知失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送订单事件通知
     */
    public void sendOrderEvent(Map<String, Object> message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EVENT_EXCHANGE,
                    RabbitMQConfig.ORDER_EVENT_ROUTING_KEY,
                    message);
            logger.info("订单事件通知已发送: {}", message);
        } catch (Exception e) {
            logger.error("发送订单事件通知失败: {}", e.getMessage(), e);
        }
    }
}