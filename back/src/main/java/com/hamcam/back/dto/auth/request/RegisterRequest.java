package com.hamcam.back.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * [RegisterRequest]
 * 회원가입 요청 DTO (snake_case 필드명 사용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @JsonProperty("nickname")
    private String nickname;

    @NotNull(message = "학년은 필수 입력 값입니다.")
    @JsonProperty("grade")
    private Integer grade;

    @NotNull(message = "과목은 최소 1개 이상 선택해야 합니다.")
    @JsonProperty("subjects")
    private List<String> subjects;

    @NotBlank(message = "공부 습관은 필수 입력 값입니다.")
    @JsonProperty("study_habit")
    private String studyHabit;

    @Pattern(regexp = "^\\d{10,15}$", message = "전화번호는 숫자만 포함되어야 하며 10자리 이상 15자리 이하여야 합니다.")
    @JsonProperty("phone")
    private String phone;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;
}
