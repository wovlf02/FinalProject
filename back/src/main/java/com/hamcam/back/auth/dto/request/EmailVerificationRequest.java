package com.hamcam.back.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이메일 인증 요청 및 인증번호 검증에 공통으로 사용되는 DTO
 * 
 * [사용 목적]
 * 1. 이메일 인증번호 전송 요청 (/send-email-code)
 * 2. 이메일 인증번호 검증 요청 (/verify-email-code)
 * 
 * [요청 예시]
 * {
 *     "email": "user@example.com"
 *     "code": "593872"
 * }
 * 
 * [주의]
 * 인증번호 전송 시에는 email만 필요
 * 인증번호 검증 시에는 email + code 둘 다 필요
 */
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationRequest {

    /**
     * 사용자 이메일 주소
     * 인증 대상 이메일
     */
    private String email;

    /**
     * 사용자 입력 인증번호
     * 인증번호 검증 시 사용됨
     * 이메일 전송 시에는 null이어도 무방
     */
    private String code;
}
