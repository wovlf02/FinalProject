package com.hamcam.back.service.livekit;

import com.hamcam.back.dto.livekit.response.LiveKitTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveKitService {

    @Value("${livekit.api-key}")
    private String apiKey;

    @Value("${livekit.api-secret}")
    private String apiSecret;

    @Getter
    @Value("${livekit.ws-url}")
    private String wsUrl;

    @Value("${livekit.ttl-minutes:60}")
    private long ttlMinutes;

    /**
     * ✅ LiveKit 토큰 + WebSocket 주소 발급
     */
    public LiveKitTokenResponse issueTokenResponse(String identity, String roomName, boolean isPresenter) {
        String token = createAccessToken(identity, roomName, isPresenter);
        long expiresAt = System.currentTimeMillis() + ttlMinutes * 60 * 1000;

        log.info("🎫 LiveKit 토큰 발급 완료: identity={}, roomName={}, isPresenter={}, expiresAt={}",
                identity, roomName, isPresenter, expiresAt);

        return new LiveKitTokenResponse(token, wsUrl, isPresenter, expiresAt);
    }

    /**
     * ✅ JWT 생성 (jjwt 사용)
     */
    public String createAccessToken(String identity, String roomName, boolean isPresenter) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(ttlMinutes * 60);

        Map<String, Object> videoGrant = new HashMap<>();
        videoGrant.put("roomJoin", true);
        videoGrant.put("roomCreate", true);
        videoGrant.put("canPublish", isPresenter);
        videoGrant.put("canSubscribe", true);
        videoGrant.put("room", roomName);

        Map<String, Object> grants = new HashMap<>();
        grants.put("video", videoGrant);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer(apiKey)
                .setSubject(identity)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .addClaims(grants)
                .signWith(Keys.hmacShaKeyFor(apiSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ 발표자 아닌 기본 권한 토큰 발급
     */
    public String createAccessToken(String identity, String roomName) {
        return createAccessToken(identity, roomName, false);
    }
}
