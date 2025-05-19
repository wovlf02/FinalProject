package com.hamcam.back.dto.user.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 전화번호 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
public class UpdatePhoneRequest {

    @Pattern(regexp = "^\\d{10,15}$", message = "전화번호는 숫자만 포함되어야 하며 10자리 이상 15자리 이하여야 합니다.")
    private String phone;
}
