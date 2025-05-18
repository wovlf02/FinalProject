package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [TokenRequest]
 *
 * 클라이언트가 보유 중인 Access Token과 Refresh Token을 서버로 전달하는 요청 DTO입니다.
 * 로그아웃 또는 토큰 재발급 시 사용됩니다.
 *
 * 예시 요청:
 * {
 *   "accessToken": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "refreshToken": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 * }
 */
@Getter
@NoArgsConstructor
public class TokenRequest {

    /**
     * 클라이언트가 보유한 Access Token (로그아웃 시 파기 대상)
     */
    @NotBlank(message = "accessToken은 필수 입력 값입니다.")
    private String accessToken;

    /**
     * Refresh Token (재발급 검증 및 로그아웃 시 삭제 대상)
     */
    @NotBlank(message = "refreshToken은 필수 입력 값입니다.")
    private String refreshToken;
}
