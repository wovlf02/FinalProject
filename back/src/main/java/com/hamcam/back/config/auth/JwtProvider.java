package com.hamcam.back.config.auth;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.time.Duration;

/**
 * JWT 토큰을 생성하고 파싱 및 검증하는 유틸 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final StringRedisTemplate redisTemplate;

    private Key key;

    private static final long ACCESS_EXP = 1000L * 60 * 60;         // 1시간
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 14; // 14일

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Access Token 생성 및 Redis 저장
     */
    public String generateAccessToken(User user) {
        String token = generateToken(user.getId(), ACCESS_EXP);
        redisTemplate.opsForValue().set("AT:" + user.getId(), token, Duration.ofMillis(ACCESS_EXP));
        return token;
    }

    /**
     * Refresh Token 생성 및 Redis 저장
     */
    public String generateRefreshToken(User user) {
        String token = generateToken(user.getId(), REFRESH_EXP);
        redisTemplate.opsForValue().set("RT:" + user.getId(), token, Duration.ofMillis(REFRESH_EXP));
        return token;
    }

    /**
     * 사용자 ID 기반 JWT 생성
     */
    private String generateToken(Long userId, long exp) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + exp);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * Redis에 저장된 AccessToken과 비교하며 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String userId = claims.getSubject();
            String redisToken = redisTemplate.opsForValue().get("AT:" + userId);

            if (redisToken == null || !redisToken.equals(token)) {
                throw new CustomException("Redis에 등록되지 않았거나 만료된 토큰입니다.");
            }

            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException("토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("유효하지 않은 토큰입니다.");
        }
    }

    /**
     * 토큰에서 만료 시간 추출
     */
    public long getExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - new Date().getTime();
    }

    /**
     * Claims 파싱
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
