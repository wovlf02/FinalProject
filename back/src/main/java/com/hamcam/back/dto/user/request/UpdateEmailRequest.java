package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 변경 요청 DTO
 * <p>
 * 사용자가 마이페이지 등에서 이메일을 수정할 때 사용하는 요청 형식입니다.
 * 이메일 형식 유효성과 비어 있지 않음을 검증합니다.
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {

    /**
     * 새로 설정할 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}
