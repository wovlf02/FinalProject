package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 인증 응답 DTO
 */
public class AuthResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class JwtResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class EmailVerificationResponse {
        private boolean verified;
        private String message;
    }
}
