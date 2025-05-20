package com.hamcam.back.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.nio.file.Paths;
import java.util.List;

/**
 * Web 관련 설정을 담당하는 Config 클래스
 * - 정적 리소스 매핑 (/uploads/**)
 * - CORS 정책 설정 (/api/**, /ws/**)
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /**
     * 📁 업로드 파일 디렉토리 (application.yml에서 주입)
     * 예: upload.dir=C:/FinalProject/uploads/
     */
    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * 🌐 허용할 CORS origin 목록 (application.yml에서 주입)
     */
    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    /**
     * ✅ 정적 자원 핸들러 설정
     * - 브라우저에서 "/uploads/**" 경로로 접근하면 로컬 디렉터리에서 파일 제공
     * - 예: /uploads/chatroom/uuid.png → {uploadDir}/chatroom/uuid.png
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(uploadDir).toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600) // 1시간 캐시
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * ✅ CORS 정책 설정
     * - API + WebSocket 핸드셰이크 경로에 대해 설정
     * - 자격 증명 포함, 모든 메서드 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/ws/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
