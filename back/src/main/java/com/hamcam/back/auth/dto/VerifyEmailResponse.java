package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이메일 인증번호 검증이 성공한 경우 클라이언트에 응답하는 DTO
 *
 * [예시 응답]
 * {
 *     "message": "이메일 인증이 완료되었습니다."
 * }
 *
 * [사용 API]
 * POST /api/auth/verify-email-code
 */
@Getter
@AllArgsConstructor
public class VerifyEmailResponse {

    /**
     * 인증 성공 안내 메시지
     * 인증 실패 시에는 예외 처리로 별도 응답됨
     */
    private String message;
}
