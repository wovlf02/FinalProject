package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [PasswordConfirmRequest]
 *
 * 사용자 비밀번호 확인 요청 DTO입니다.
 * 회원탈퇴, 민감 정보 변경 등 보안이 필요한 작업 전에 사용됩니다.
 *
 * 사용 예:
 * - POST /api/users/withdraw
 * - POST /api/auth/check-password
 */
@Getter
@NoArgsConstructor
public class PasswordConfirmRequest {

    /**
     * 현재 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
