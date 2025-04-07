package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * [로그인 응답 DTO]
 *
 * 로그인 성공 시 클라이언트에 반환되는 응답 객체
 * Access Token과 Refresh Token을 포함하며, 사용자 정보도 함께 전달
 *
 * [사용 API]
 * POST /api/auth/login
 *
 * [응답 예시]
 * {
 *     "accessToken": "example",
 *     "refreshToken": "example",
 *     "username": "example111",
 *     "email": "user@example.com",
 *     "nickname": "nickname_example"
 * }
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    /**
     * Access Token (JWT)
     * 인증 후 보호된 리소스 요청 시 사용
     * 발급 후 1시간 유효
     */
    private String accessToken;

    /**
     * Refresh Token (JWT)
     * Access Token 만료 시 재발급 요청에 사용
     * DB에 BCrypt 해싱된 상태로 저장되며 30일 유효
     */
    private String refreshToken;

    /**
     * 사용자 ID (로그인한 사용자 아이디)
     */
    private String username;

    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 사용자 닉네임
     */
    private String nickname;
}
