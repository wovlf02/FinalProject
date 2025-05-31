package com.hamcam.back.dto.livekit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveKitTokenRequest {
    private String roomName;
    private String userId;  // ✅ 추가
}
