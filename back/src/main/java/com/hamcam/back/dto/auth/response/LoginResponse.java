package com.hamcam.back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 성공 시 클라이언트로 반환되는 DTO
 * 토큰 + 사용자 정보 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;        // Access Token (1시간 유효)
    private String refreshToken;       // Refresh Token (14일 유효)
    private String username;
    private String name;
    private String email;
    private String nickname;
    private String profileImageUrl;
}
