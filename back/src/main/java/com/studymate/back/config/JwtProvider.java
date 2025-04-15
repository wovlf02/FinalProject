package com.studymate.back.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 */
@Component
public class JwtProvider {

    private final Key secretKey;

    /**
     * Access Token 만료 시간 -> 1시간
     */
    private final long accessTokenExpiration;

    /**
     * Refresh Token 만료 시간 -> 30일
     */
    private final long refreshTokenExpiration;

    /**
     * JWT Secret Key 설정
     * application.yml에서 환경 변수로 관리
     * @param secret 64비트 JWT Secret Key
     * @param accessTokenExpiration Access Token 만료 기간 -> 1시간
     * @param refreshTokenExpiration Refresh Token 만료 기간 -> 30일
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Access Token 생성 메서드
     * @param username 아이디
     * @return 1시간 동안 유효한 Access Token
     */
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Refresh Token 생성 메서드
     * @param username 아이디
     * @return 30일 동안 유효한 Refresh Token
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰 검증 메서드
     * @param token 토큰
     * @return 토큰 검증 결과 (서명이 유효한 경우: true, 만료되었거나 변조된 경우: false)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT 토큰이 만료되었습니다.");
        } catch (JwtException e) {
            System.out.println("JWT 토큰이 유효하지 않습니다.");
        }
        return false;
    }

    /**
     * 토큰에서 사용자 아이디 추출 메서드
     * @param token 토큰
     * @return 아이디
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
