package com.hamcam.back.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * [회원가입 응답 DTO]
 *
 * 회원가입 성공 시 클라이언트에 반환되는 간단한 메시지 응답 객체
 * 일반적으로 메시지 하나만 포함되며, 실패 시에는 예외 처리로 응답됨
 *
 * [사용 API]
 * POST /api/auth/register
 *
 * [응답 예시]
 * {
 *     "message": "회원가입이 완료되었습니다."
 * }
 */
@Getter
@AllArgsConstructor
public class RegisterResponse {

    /**
     * 회원가입 처리 결과 메시지
     * 회원가입 성공 시 사용자에게 보여줄 메시지
     * 실패 시에는 해당 DTO가 아닌 Exception 처리로 응답됨
     */
    private String message;
}
