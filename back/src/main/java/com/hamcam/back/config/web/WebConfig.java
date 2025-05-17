package com.hamcam.back.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.nio.file.Paths;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String LOCAL_UPLOAD_DIR = "C:/FinalProject/uploads"; // 파일 업로드 경로

    /**
     * 정적 자원 핸들러 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(LOCAL_UPLOAD_DIR).toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * CORS 설정 (React Native, WebSocket 포함)
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // REST API 엔드포인트 허용
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("http://localhost:3000", "http://192.168.*:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                // WebSocket 핸드셋 경로 허용
                registry.addMapping("/ws/**")
                        .allowedOriginPatterns("http://localhost:3000", "http://192.168.*:3000")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
