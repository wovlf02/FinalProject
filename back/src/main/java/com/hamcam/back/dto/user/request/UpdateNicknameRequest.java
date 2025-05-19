package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 닉네임 변경 요청 DTO
 * <p>
 * 사용자가 마이페이지 또는 설정 화면에서 닉네임을 수정할 때 사용하는 요청 형식입니다.
 * 닉네임은 공백이 아닌 값이어야 하며, 유효성 검사를 통해 필수 입력을 강제합니다.
 * </p>
 */
@Getter
@NoArgsConstructor
public class UpdateNicknameRequest {

    /**
     * 변경할 닉네임
     */
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;
}
