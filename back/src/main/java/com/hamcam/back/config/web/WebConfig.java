package com.hamcam.back.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String LOCAL_UPLOAD_DIR = "C:/upload"; // 파일 업로드 경로

    /**
     * 정적 자원 핸들러 설정
     * 예: http://localhost:8080/static/파일명 으로 접근
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(LOCAL_UPLOAD_DIR).toUri().toString();
        registry.addResourceHandler("/static/**")
                .addResourceLocations(uploadPath);
    }

    /**
     * CORS 설정 (React Native, Web 요청 허용)
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000", "http://192.168.35.52:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
