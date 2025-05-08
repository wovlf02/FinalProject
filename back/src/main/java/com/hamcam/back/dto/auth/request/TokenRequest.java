package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 액세스 및 리프레시 토큰을 함께 담는 요청 DTO입니다.
 * 로그아웃, 토큰 재발급 시 클라이언트가 보유한 JWT를 전달합니다.
 */
@Getter
@NoArgsConstructor
public class TokenRequest {

    /**
     * 현재 사용 중인 access 토큰
     */
    @NotBlank(message = "accessToken은 필수입니다.")
    private String accessToken;

    /**
     * refresh 토큰
     */
    @NotBlank(message = "refreshToken은 필수입니다.")
    private String refreshToken;
}
