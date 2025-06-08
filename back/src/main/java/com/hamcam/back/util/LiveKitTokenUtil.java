package com.hamcam.back.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LiveKitTokenUtil {

    public static String createToken(String apiKey, String apiSecret, String identity, String roomName, boolean isPresenter, long ttlMinutes) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(ttlMinutes * 60);

        Map<String, Object> videoGrant = new HashMap<>();
        videoGrant.put("roomJoin", true);
        videoGrant.put("roomCreate", true);
        videoGrant.put("canPublish", isPresenter);
        videoGrant.put("canSubscribe", true);
        videoGrant.put("room", roomName);

        Map<String, Object> grant = new HashMap<>();
        grant.put("video", videoGrant);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer(apiKey)
                .setSubject(identity)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("video", videoGrant) // ✅ video 권한 필수
                .signWith(Keys.hmacShaKeyFor(apiSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
