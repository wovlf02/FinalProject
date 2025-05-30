package com.hamcam.back.dto.study.team.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ✅ LiveKit 접속용 토큰 응답
 */
@Getter
@AllArgsConstructor
public class LiveKitTokenResponse {
    private String token;
}
