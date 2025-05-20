package com.hamcam.back.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.util.List;

/**
 * Web 관련 설정을 담당하는 Config 클래스
 * - 정적 리소스 매핑 (/uploads/**)
 * - CORS 정책 설정 (/api/**, /ws/**)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 📁 업로드 파일 디렉토리 (직접 지정)
     */
    private static final String FILE_UPLOAD_DIR = "file:///C:/FinalProject/uploads/"; // 반드시 file:/// 로 시작해야 함

    /**
     * 🌐 허용할 CORS origin 목록 (직접 지정)
     */
    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
    );

    /**
     * ✅ 정적 자원 핸들러 설정
     * - /uploads/** 경로 요청 시 로컬 C:/FinalProject/uploads/ 폴더에서 파일을 제공
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(FILE_UPLOAD_DIR)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * ✅ CORS 정책 설정
     * - API 및 WebSocket 핸드셰이크 경로에 대해 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = ALLOWED_ORIGINS.toArray(new String[0]);

        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/ws/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
