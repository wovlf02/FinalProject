package com.studymate.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 파일
 * Redis를 Spring boot 애플리케이션과 연동
 * Access Token을 Redis에 저장 및 관리
 */
@Configuration
public class RedisConfig {

    /**
     * Redis 연결 팩토리 설정 -> Lettuce 사용
     * @return RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379); // 기본 Redis 설정 -> 로컬
    }

    /**
     * RedisTemplate 설정
     * Key, Value를 문자열로 저장
     * Access Token 저장 시 사용
     * @param redisConnectionFactory Redis 연결 설정 데이터
     * @return RedisTemplate<String, String>
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Redis에 문자열 형태로 저장하기 위한 Serializer 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
