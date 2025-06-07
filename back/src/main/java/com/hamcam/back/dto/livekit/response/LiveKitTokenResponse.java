package com.hamcam.back.dto.livekit.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveKitTokenResponse {
    private String token;
    private String wsUrl;
    private boolean presenter;   // ✅ 필드명 수정 (is 제거)
    private long expiresAt;
}
