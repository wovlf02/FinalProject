package com.hamcam.back.service.livekit;

import com.hamcam.back.dto.livekit.response.LiveKitTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LiveKitService {

    @Value("${livekit.api-key}")
    private String apiKey;

    @Value("${livekit.api-secret}")
    private String apiSecret;

    @Value("${livekit.ws-url}")
    private String wsUrl;

    @Value("${livekit.ttl-minutes:60}")
    private long ttlMinutes;

    private Key secretKey;

    @PostConstruct
    public void init() {
        if (apiSecret.length() < 32) {
            throw new IllegalArgumentException("LiveKit API Secret은 최소 256비트(32자 이상)여야 합니다.");
        }
        this.secretKey = Keys.hmacShaKeyFor(apiSecret.getBytes());
    }

    /**
     * 토큰 + WebSocket URL 포함 응답 생성
     */
    public LiveKitTokenResponse issueTokenResponse(String identity, String roomName) {
        String token = createAccessToken(identity, roomName);
        return new LiveKitTokenResponse(token, wsUrl);
    }

    /**
     * LiveKit 접속용 JWT 생성
     */
    public String createAccessToken(String identity, String roomName) {
        if (identity == null || identity.isBlank() || roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("identity와 roomName은 필수입니다.");
        }

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + ttlMinutes * 60 * 1000);

        // ✅ video grant 구성
        Map<String, Object> videoGrant = new HashMap<>();
        videoGrant.put("room", roomName);
        videoGrant.put("room_join", true);
        videoGrant.put("can_publish", true);
        videoGrant.put("can_subscribe", true);

        // ✅ 전체 grants wrapping
        Map<String, Object> grants = new HashMap<>();
        grants.put("video", videoGrant);
        grants.put("identity", identity);

        return Jwts.builder()
                .setIssuer(apiKey)
                .setSubject(identity)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim("grants", grants)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
