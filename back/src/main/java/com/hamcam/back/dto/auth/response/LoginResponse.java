package com.hamcam.back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 성공 시 반환되는 응답 DTO입니다.
 * 클라이언트는 발급받은 access & refresh 토큰을 저장하고 활용합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * Access Token (1시간 유효)
     */
    private String accessToken;

    /**
     * Refresh Token (14일 유효)
     */
    private String refreshToken;
}
