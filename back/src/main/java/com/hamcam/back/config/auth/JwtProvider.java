package com.hamcam.back.config.auth;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final StringRedisTemplate redisTemplate;

    private Key key;

    public static final String ACCESS_COOKIE = "accessToken";
    public static final String REFRESH_COOKIE = "refreshToken";

    private static final long ACCESS_EXP = 1000L * 60 * 60;           // 1시간
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 14; // 14일

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 📌 토큰 생성
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

    private String generateToken(Long userId, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 📌 쿠키 생성
    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(ACCESS_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(ACCESS_EXP))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(REFRESH_EXP))
                .build();
    }

    // 📌 쿠키 삭제 (로그아웃 시)
    public ResponseCookie deleteCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }

    // 📌 토큰 파싱 및 유효성 검사
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateAccessTokenWithRedis(String token, boolean fromHeader) {
        try {
            Claims claims = parseClaims(token);
            String userId = claims.getSubject();

            if (fromHeader) {
                return true; // 헤더에서 온 경우: JWT 유효성만 확인
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
}
