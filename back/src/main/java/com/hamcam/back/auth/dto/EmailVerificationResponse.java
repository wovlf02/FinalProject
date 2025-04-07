package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이메일 인증번호 전송 후 클라이언트에 응답하는 DTO
 *
 * [예시 응답]
 * {
 *     "message": "인증번호가 이메일로 전송되었습니다."
 * }
 */
@Getter
@AllArgsConstructor
public class EmailVerificationResponse {

    /**
     * 전송 결과 메시지
     * 이메일 발송 성공 시 사용자에게 보여줄 안내 문구
     */
    private String message;
}
