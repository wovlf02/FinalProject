package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 요청 DTO입니다.
 * 사용자가 로그인 상태에서 기존 비밀번호를 확인한 후
 * 새 비밀번호로 변경할 때 사용됩니다.
 */
@Getter
@NoArgsConstructor
public class UpdatePasswordRequest {

    /**
     * 현재 비밀번호
     */
    @NotBlank(message = "현재 비밀번호는 필수 입력 값입니다.")
    private String currentPassword;

    /**
     * 새 비밀번호
     */
    @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
}
