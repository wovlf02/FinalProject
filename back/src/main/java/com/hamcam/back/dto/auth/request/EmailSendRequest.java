package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 인증 코드 발송 요청 DTO입니다.
 * 인증 목적에 따라 회원가입, 아이디 찾기, 비밀번호 재설정 등의 상황에서 사용됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendRequest {

    /**
     * 인증 코드를 받을 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    /**
     * 인증 목적 구분값: register | find-id | reset-pw 등
     */
    @NotBlank(message = "요청 타입은 필수 입력 값입니다.")
    private String type;
}
