package edu.tongji.common.mq;

/**
 * RabbitMQ 交换机 / 队列 / 路由键常量（所有微服务共享，保证生产者与消费者命名一致）
 */
public final class MqConstants {

    private MqConstants() {
    }

    // ==================== 统一交换机（所有通知类型共用一个 TopicExchange） ====================
    public static final String EXCHANGE = "tjnovel.exchange";

    // ==================== 通知（审核结果推送等） ====================
    public static final String NOTIFICATION_QUEUE = "tjnovel.notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "tjnovel.notification";

    // ==================== 小说更新通知 ====================
    public static final String NOVEL_UPDATE_QUEUE = "tjnovel.novel.update.queue";
    public static final String NOVEL_UPDATE_ROUTING_KEY = "tjnovel.novel.update";

    // ==================== 订单事件（订单回调等） ====================
    public static final String ORDER_EVENT_QUEUE = "tjnovel.order.event.queue";
    public static final String ORDER_EVENT_ROUTING_KEY = "tjnovel.order.event";

    // ==================== 消息类型 ====================
    public static final String TYPE_NOVEL_UPDATE = "NOVEL_UPDATE";
    public static final String TYPE_ORDER_EVENT = "ORDER_EVENT";
    public static final String TYPE_AUDIT_RESULT = "AUDIT_RESULT";
    public static final String TYPE_SYSTEM = "SYSTEM";
}
