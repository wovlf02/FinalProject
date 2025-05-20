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

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final StringRedisTemplate redisTemplate;

    private Key key;

    public static final String ACCESS_COOKIE = "accessToken";
    public static final String REFRESH_COOKIE = "refreshToken";

    private static final long ACCESS_EXP = 1000L * 60 * 60;         // 1시간
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 14; // 14일

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(User user) {
        String token = generateToken(user.getId(), ACCESS_EXP);
        redisTemplate.opsForValue().set("AT:" + user.getId(), token, Duration.ofMillis(ACCESS_EXP));
        return token;
    }

    public String generateRefreshToken(User user) {
        String token = generateToken(user.getId(), REFRESH_EXP);
        redisTemplate.opsForValue().set("RT:" + user.getId(), token, Duration.ofMillis(REFRESH_EXP));
        return token;
    }

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

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public boolean validateAccessTokenWithRedis(String token, boolean fromHeader) {
        try {
            Claims claims = parseClaims(token);
            String userId = claims.getSubject();

            if (fromHeader) {
                // 헤더에서 온 토큰은 Redis 검사 없이 JWT 자체 유효성만 검사
                return true;
            }

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

    public boolean validateTokenWithoutRedis(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException("토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("유효하지 않은 토큰입니다.");
        }
    }

    public long getExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - new Date().getTime();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
