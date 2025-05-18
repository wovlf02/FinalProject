package com.hamcam.back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [TokenResponse]
 *
 * AccessToken과 RefreshToken을 포함한 토큰 재발급 응답 DTO입니다.
 * 주로 재로그인 또는 토큰 갱신 시 클라이언트에 전달됩니다.
 * - AccessToken은 HttpOnly 쿠키로 따로 전달할 수도 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {

    /**
     * Access Token (JWT)
     */
    private String accessToken;

    /**
     * Refresh Token (JWT)
     */
    private String refreshToken;
}
