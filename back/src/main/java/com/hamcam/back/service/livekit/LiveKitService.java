package com.hamcam.back.service.livekit;

import io.livekit.server.sdk.AccessToken;
import io.livekit.server.sdk.VideoGrant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LiveKitService {

    @Value("${livekit.api-key}")
    private String apiKey;

    @Value("${livekit.api-secret}")
    private String apiSecret;

    @Value("${livekit.ttl-minutes:60}")
    private long ttlMinutes;

    public String createAccessToken(String identity, String roomName) {
        VideoGrant grant = new VideoGrant()
                .setRoom(roomName)
                .setRoomJoin(true)
                .setCanPublish(true)
                .setCanSubscribe(true);

        AccessToken token = new AccessToken(apiKey, apiSecret)
                .setIdentity(identity) // 유저 고유 식별자
                .addGrant(grant)
                .setTtl(Duration.ofMinutes(ttlMinutes));

        return token.toJwt(); // JWT 반환
    }
}

