package edu.tongji.transactionservice.config;

import edu.tongji.common.mq.MqConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * transaction-service 的 RabbitMQ 生产者配置。
 * 声明统一的 TopicExchange（队列与绑定由 notification-service 声明）。
 */
@Configuration
public class RabbitMQProducerConfig {

    @Bean
    public TopicExchange tjnovelExchange() {
        return new TopicExchange(MqConstants.EXCHANGE);
    }

    @Bean
    public MessageConverter orderEventMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate orderEventRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(orderEventMessageConverter());
        return rabbitTemplate;
    }
}
