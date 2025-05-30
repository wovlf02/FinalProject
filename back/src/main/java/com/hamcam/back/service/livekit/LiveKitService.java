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

    @Value("${livekit.ws-url}")
    private String wsUrl;

    @Value("${livekit.ttl-minutes:60}")
    private long ttlMinutes;

    /**
     * LiveKit 접속용 JWT 생성
     *
     * @param identity 유저 고유 식별자 (ex: userId or nickname)
     * @param roomName 입장할 방 이름
     * @return JWT 토큰 문자열
     */
    public String createAccessToken(String identity, String roomName) {
        if (identity == null || identity.isBlank() || roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("LiveKit 토큰 발급에 필요한 값이 부족합니다.");
        }

        VideoGrant grant = new VideoGrant()
                .setRoom(roomName)
                .setRoomJoin(true)
                .setCanPublish(true)
                .setCanSubscribe(true);

        try {
            AccessToken token = new AccessToken(apiKey, apiSecret)
                    .setIdentity(identity)
                    .addGrant(grant)
                    .setTtl(Duration.ofMinutes(ttlMinutes));

            return token.toJwt();
        } catch (Exception e) {
            throw new RuntimeException("LiveKit AccessToken 생성 실패", e);
        }
    }

    /**
     * WebSocket URL을 외부에서 쓸 수 있도록 getter 제공
     */
    public String getWsUrl() {
        return wsUrl;
    }
}
