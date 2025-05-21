package com.hamcam.back.config.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.TimeZone;

/**
 * 전역 JSON 직렬화 설정을 담당하는 Jackson 설정 클래스
 * - LocalDateTime 형식 지정
 * - snake_case 적용
 * - null 값 필드 제외
 * - Long 타입 -> 문자열 변환 (선택)
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .timeZone(TimeZone.getTimeZone("Asia/Seoul")) // ✅ KST 시간대
                .modules(new JavaTimeModule())                // ✅ LocalDateTime 지원
                .simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")   // ✅ ISO 8601 형식
                .serializationInclusion(JsonInclude.Include.NON_NULL) // ✅ null 필드 제외
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ✅ timestamp 금지
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)       // ✅ snake_case 적용
                .build();
    }
}
