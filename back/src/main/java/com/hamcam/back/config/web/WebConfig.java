package com.hamcam.back.config.web;

import com.hamcam.back.config.auth.JwtProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.nio.file.Paths;

/**
 * Web 관련 설정을 담당하는 Config 클래스
 * - 정적 리소스 매핑 (/uploads/**)
 * - CORS 정책 설정 (/api/**)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 📁 업로드 파일이 저장되는 로컬 디렉터리
    private static final String LOCAL_UPLOAD_DIR = "C:/FinalProject/uploads/";


    /**
     * ✅ 정적 자원 핸들러 설정
     * - 브라우저에서 "/uploads/**" 경로로 접근하면 로컬 디렉터리에서 파일 제공
     * - 예: /uploads/chatroom/uuid.png → C:/FinalProject/uploads/chatroom/uuid.png
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(LOCAL_UPLOAD_DIR).toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600) // 캐시 설정 (초)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * ✅ CORS 정책 설정
     * - 모든 HTTP 메서드 허용
     * - 특정 Origin(도메인)에서의 요청 허용
     * - 자격 증명(Cookie, Authorization header) 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://10.20.33.65:3000",
                        "http://192.168.35.205:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // ✅ HttpOnly Cookie 인증을 위해 필수
    }
}
