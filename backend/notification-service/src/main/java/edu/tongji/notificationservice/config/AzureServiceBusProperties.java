package edu.tongji.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "azure.servicebus")
public record AzureServiceBusProperties(
        String connectionString,
        String queueName) {
}

