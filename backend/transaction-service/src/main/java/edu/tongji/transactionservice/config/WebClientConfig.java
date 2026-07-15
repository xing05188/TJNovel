package edu.tongji.transactionservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient配置类，用于服务间调用
 */
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient userServiceWebClient(@Value("${services.user:http://localhost:7081}") String userServiceUrl) {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
    
    @Bean
    public WebClient contentServiceWebClient(@Value("${services.content:http://localhost:7082}") String contentServiceUrl) {
        return WebClient.builder()
                .baseUrl(contentServiceUrl)
                .build();
    }
}