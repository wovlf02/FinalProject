package com.hamcam.back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 재발급 및 로그인 시 응답 객체입니다.
 * 클라이언트는 accessToken을 헤더에 담아 요청하고,
 * refreshToken은 저장소에 안전하게 저장 후 토큰 갱신 시 사용합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    /**
     * 인가(Access) 토큰 - 짧은 만료 시간, 모든 요청 시 헤더에 포함
     */
    private String accessToken;

    /**
     * 재발급(Refresh) 토큰 - 긴 만료 시간, 토큰 갱신 요청 시 사용
     */
    private String refreshToken;
}
