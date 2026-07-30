package edu.tongji.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类（替代 Azure Service Bus）。
 * 所有通知类型共用一个 TopicExchange，通过不同 routingKey 路由到三个独立队列。
 */
@Configuration
public class RabbitMQConfig {

    /** 统一交换机（所有服务共享，来自 common 的 MqConstants） */
    public static final String EXCHANGE = MqConstants.EXCHANGE;

    // ==================== 统一交换机 ====================

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // ==================== 三个队列（按 routingKey 隔离） ====================

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(MqConstants.NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue novelUpdateQueue() {
        return QueueBuilder.durable(MqConstants.NOVEL_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue orderEventQueue() {
        return QueueBuilder.durable(MqConstants.ORDER_EVENT_QUEUE).build();
    }

    // ==================== 绑定：routingKey -> 队列 ====================

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(exchange())
                .with(MqConstants.NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding novelUpdateBinding() {
        return BindingBuilder.bind(novelUpdateQueue())
                .to(exchange())
                .with(MqConstants.NOVEL_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding orderEventBinding() {
        return BindingBuilder.bind(orderEventQueue())
                .to(exchange())
                .with(MqConstants.ORDER_EVENT_ROUTING_KEY);
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