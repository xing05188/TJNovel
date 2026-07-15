package edu.tongji.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类（替代 Azure Service Bus）
 */
@Configuration
public class RabbitMQConfig {

    /** 通知交换机名称 */
    public static final String NOTIFICATION_EXCHANGE = "tjnovel.notification.exchange";

    /** 通知队列名称 */
    public static final String NOTIFICATION_QUEUE = "tjnovel.notification.queue";

    /** 通知路由键 */
    public static final String NOTIFICATION_ROUTING_KEY = "tjnovel.notification";

    /** 小说更新交换机 */
    public static final String NOVEL_UPDATE_EXCHANGE = "tjnovel.novel.update.exchange";

    /** 小说更新队列 */
    public static final String NOVEL_UPDATE_QUEUE = "tjnovel.novel.update.queue";

    /** 小说更新路由键 */
    public static final String NOVEL_UPDATE_ROUTING_KEY = "tjnovel.novel.update";

    /** 订单事件交换机 */
    public static final String ORDER_EVENT_EXCHANGE = "tjnovel.order.event.exchange";

    /** 订单事件队列 */
    public static final String ORDER_EVENT_QUEUE = "tjnovel.order.event.queue";

    /** 订单事件路由键 */
    public static final String ORDER_EVENT_ROUTING_KEY = "tjnovel.order.event";

    // ==================== 通知交换机与队列 ====================

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(NOTIFICATION_ROUTING_KEY);
    }

    // ==================== 小说更新交换机与队列 ====================

    @Bean
    public TopicExchange novelUpdateExchange() {
        return new TopicExchange(NOVEL_UPDATE_EXCHANGE);
    }

    @Bean
    public Queue novelUpdateQueue() {
        return QueueBuilder.durable(NOVEL_UPDATE_QUEUE).build();
    }

    @Bean
    public Binding novelUpdateBinding(Queue novelUpdateQueue, TopicExchange novelUpdateExchange) {
        return BindingBuilder.bind(novelUpdateQueue)
                .to(novelUpdateExchange)
                .with(NOVEL_UPDATE_ROUTING_KEY);
    }

    // ==================== 订单事件交换机与队列 ====================

    @Bean
    public TopicExchange orderEventExchange() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE);
    }

    @Bean
    public Queue orderEventQueue() {
        return QueueBuilder.durable(ORDER_EVENT_QUEUE).build();
    }

    @Bean
    public Binding orderEventBinding(Queue orderEventQueue, TopicExchange orderEventExchange) {
        return BindingBuilder.bind(orderEventQueue)
                .to(orderEventExchange)
                .with(ORDER_EVENT_ROUTING_KEY);
    }

    // ==================== 通用配置 ====================

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}