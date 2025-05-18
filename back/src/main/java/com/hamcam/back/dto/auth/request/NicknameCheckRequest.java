package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [NicknameCheckRequest]
 *
 * 닉네임 중복 확인 요청 DTO입니다.
 * 회원가입 또는 프로필 수정 시 입력된 닉네임의 중복 여부를 확인합니다.
 *
 * 사용 예:
 * - POST /api/auth/check-nickname
 */
@Getter
@NoArgsConstructor
public class NicknameCheckRequest {

    /**
     * 중복 확인할 닉네임
     */
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;
}
