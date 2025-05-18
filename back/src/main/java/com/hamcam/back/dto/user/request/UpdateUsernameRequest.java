package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 아이디(username) 변경 요청 DTO
 * <p>
 * 사용자가 로그인 상태에서 기존 아이디를 새로운 아이디로 변경할 때 사용됩니다.
 * </p>
 */
@Getter
@NoArgsConstructor
public class UpdateUsernameRequest {

    /**
     * 변경할 사용자 아이디
     */
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String username;
}
