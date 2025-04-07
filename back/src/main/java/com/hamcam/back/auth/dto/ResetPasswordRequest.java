package com.hamcam.back.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [비밀번호 재설정 요청 DTO]
 *
 * 사용자가 아이디 입력 및 이메일 인증 후, 새 비밀번호를 설정하는 요청에 사용
 * 기존 사용자 여부 확인 후 서버에서 새 비밀번호로 업데이트
 *
 * [사용 API]
 * POST /api/auth/reset-password
 *
 * [요청 예시]
 * {
 *     "username": "example12",
 *     "newPassword": "newPassword123!!"
 * }
 *
 * [Flow]
 * 1. 사용자가 아이디를 입력하고 본인 인증(이메일)을 완료
 * 2. 새 비밀번호를 입력하여 요청
 * 3. 서버는 해당 사용자 확인 후 비밀번호를 암호화하여 저장
 */
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordRequest {

    /**
     * 사용자 아이디
     * 비밀번호를 변경할 대상 사용자 식별용
     */
    private String username;

    /**
     * 새 비밀번호
     * 프론트에서는 마스킹 처리 필요
     * 서버에서 BCrypt로 암호화 후 저장
     */
    private String newPassword;
}
