package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 재설정 요청 DTO입니다.
 * 이메일 인증이 완료된 사용자에 대해 새 비밀번호를 저장하기 위해 사용됩니다.
 */
@Getter
@NoArgsConstructor
public class PasswordChangeRequest {

    /**
     * 새 비밀번호
     */
    @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
}
