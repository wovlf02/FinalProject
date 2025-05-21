package com.hamcam.back.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web 관련 설정 (정적 자원 + CORS 정책)
 * - 보안 제거 버전 (Credentials X)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String FILE_UPLOAD_DIR = "file:///C:/FinalProject/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(FILE_UPLOAD_DIR)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins("*") // 모든 도메인 허용
                .allowedMethods("*")
                .allowedHeaders("*");
        // allowCredentials 생략
    }
}
