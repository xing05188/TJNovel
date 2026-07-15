package edu.tongji.notificationservice.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBusConfig {

    @Bean
    @ConditionalOnProperty(prefix = "azure.servicebus", name = "connection-string")
    public ServiceBusSenderClient serviceBusSenderClient(AzureServiceBusProperties properties) {
        return new ServiceBusClientBuilder()
                .connectionString(properties.connectionString())
                .sender()
                .queueName(properties.queueName())
                .buildClient();
    }
}

