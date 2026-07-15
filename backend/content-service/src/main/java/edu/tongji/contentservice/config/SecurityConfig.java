package edu.tongji.contentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 开发环境：完全禁用所有安全认证
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(registry -> registry
                        .anyRequest().permitAll());
        
        return http.build();
    }
}

