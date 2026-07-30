package edu.tongji.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tongji.common.mq.MqConstants;
import edu.tongji.common.mq.NotificationMessage;
import edu.tongji.notificationservice.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 消费者：监听三个队列，将消息持久化（内存）并通过 WebSocket 实时推送给前端。
 *
 * <ul>
 *   <li>小说更新通知（广播）</li>
 *   <li>订单事件（订单回调，定向推送）</li>
 *   <li>通知（审核结果推送，定向推送）</li>
 * </ul>
 */
@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationStore store;
    private final NotificationWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationConsumer(NotificationStore store,
                                NotificationWebSocketHandler webSocketHandler,
                                ObjectMapper objectMapper) {
        this.store = store;
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MqConstants.NOVEL_UPDATE_QUEUE)
    public void listenNovelUpdate(NotificationMessage message) {
        logger.info("收到小说更新通知: {}", message.getTitle());
        Notification n = store.save(message.getTargetUserId(), message.getType(),
                message.getTitle(), message.getContent(), message.getData(), message.getTimestamp());
        push(n);
    }

    @RabbitListener(queues = MqConstants.ORDER_EVENT_QUEUE)
    public void listenOrderEvent(NotificationMessage message) {
        logger.info("收到订单事件: {}", message.getTitle());
        Notification n = store.save(message.getTargetUserId(), message.getType(),
                message.getTitle(), message.getContent(), message.getData(), message.getTimestamp());
        push(n);
    }

    @RabbitListener(queues = MqConstants.NOTIFICATION_QUEUE)
    public void listenNotification(NotificationMessage message) {
        logger.info("收到通知(审核结果等): {}", message.getTitle());
        Notification n = store.save(message.getTargetUserId(), message.getType(),
                message.getTitle(), message.getContent(), message.getData(), message.getTimestamp());
        push(n);
    }

    /**
     * 推送：有目标用户则定向推送；否则广播（如小说更新）。
     */
    private void push(Notification n) {
        try {
            String json = objectMapper.writeValueAsString(n);
            if (n.getTargetUserId() != null) {
                webSocketHandler.sendToUser(n.getTargetUserId(), json);
            } else {
                webSocketHandler.broadcast(json);
            }
        } catch (Exception e) {
            logger.error("推送通知到WebSocket失败: {}", e.getMessage(), e);
        }
    }
}
