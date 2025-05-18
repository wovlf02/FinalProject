package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * 회원가입 최종 요청 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotNull(message = "학년은 필수 입력 값입니다.")
    private Integer grade;

    @NotNull(message = "과목은 최소 1개 이상 선택해야 합니다.")
    private List<String> subjects;

    @NotBlank(message = "공부 습관은 필수 입력 값입니다.")
    private String studyHabit;

    @Pattern(regexp = "^\\d{10,15}$", message = "전화번호는 숫자만 포함되어야 하며 10자리 이상 15자리 이하여야 합니다.")
    private String phone; // ✅ 필드 유지

    private String profileImageUrl;
}
