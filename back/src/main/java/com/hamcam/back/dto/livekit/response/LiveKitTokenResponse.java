package com.hamcam.back.dto.livekit.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveKitTokenResponse {
    private String token;
    private String wsUrl;
}
