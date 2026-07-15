package edu.tongji.contentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient配置类
 */
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    /**
     * 创建WebClient Bean用于调用 admin-service
     * 注意：在 Docker 环境下，通过服务名访问 API Gateway，而不是 localhost。
     * api-gateway 服务在 docker-compose 中的名称为 "api-gateway"，映射端口为 7080。
     * 因此这里使用 http://api-gateway:7080/api 作为网关入口。
     *
     * 如果在本地直接跑 Spring Boot（不通过 Docker），请确保 api-gateway 监听在本机 7080 端口，
     * 并且本机访问地址同样是 http://localhost:7080/api。
     *
     * 如需在本地与 Docker 之间切换，可以考虑改成读取环境变量或配置文件：
     * services.gateway-base-url: http://api-gateway:7080/api 或 http://localhost:7080/api
     *
     * @return WebClient实例
     */
    @Bean
    public WebClient adminServiceWebClient() {
        return WebClient.builder()
                // 通过 API Gateway 调用 admin-service，在 docker-compose 网络中使用服务名访问
                .baseUrl("http://api-gateway:7080/api")
                .build();
    }
}