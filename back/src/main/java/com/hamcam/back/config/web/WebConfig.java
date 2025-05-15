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
                registry.addMapping("/api/**") // /api/** 경로에 대해 CORS 허용
                        .allowedOrigins("http://localhost:3000", // React 앱의 주소
                                        "http://10.20.72.146:3000",
                                        "http://172.17.5.61:3000",
                                        "https://cd6c-123-215-41-208.ngrok-free.app")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true); // 인증 정보 허용
            }
        };
    }
}
