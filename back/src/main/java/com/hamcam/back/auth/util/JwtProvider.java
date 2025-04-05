package com.hamcam.back.auth.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티 클래스
 */
@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    /**
     * application.yml에서 secret key 및 토큰 유효 시간 설정을 불러옴
     * @param secret
     * @param accessTokenValidity
     * @param refreshTokenValidity
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenValidity,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidity
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /**
     * Access Token 생성 (1시간 유효)
     * @param username 사용자 아이디
     * @return accessToken 문자열
     */
    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenValidity, "access");
    }

    /**
     * Refresh Token 생성 (1시간 유효)
     * @param username 사용자 아이디
     * @return accessToken 문자열
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenValidity, "refresh");
    }

    /**
     * 실제 JWT 생성 로직
     * @param username 사용자 아이디
     * @param validityMillis 만료 시간
     * @param tokenType Access인지 Refresh인지
     * @return JWT 토큰
     */
    private String generateToken(String username, long validityMillis, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("type", tokenType)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     * @param token 토큰 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰에서 username 추출
     * @param token 토큰 문자열
     * @return username (subject)
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
