package com.hamcam.back.config.web;

import com.hamcam.back.global.resolver.CurrentUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.nio.file.Paths;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    private static final String LOCAL_UPLOAD_DIR = "C:/FinalProject/uploads"; // 로컬 파일 업로드 경로

    /**
     * 정적 리소스 핸들링: /uploads/** → 실제 로컬 경로
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(LOCAL_UPLOAD_DIR).toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath) // → file:/C:/FinalProject/uploads/
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * 전역 CORS 설정
     * - REST API, WebSocket, 파일 업로드 등 모든 경로 적용
     * - allowCredentials(true): 쿠키 인증 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000", "http://192.168.*:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // ✅ HttpOnly 쿠키 전달 허용
    }

    /**
     * 커스텀 파라미터 리졸버 등록 (@CurrentUser 지원)
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
