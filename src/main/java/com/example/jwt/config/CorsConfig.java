package com.example.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // 서버가 응답 시 JSON 을 JS 에서의 처리 여부 설정
        config.addAllowedOrigin("*");  // 모든 IP 요청에 응답 허용
        config.addAllowedHeader("*");  // 모든 Header 에 응답 허용
        config.addAllowedMethod("*");  // 모든 HTTP Method(GET, POST, PUT, DELETE, PATCH 등) 에 응답 허용
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }

}
