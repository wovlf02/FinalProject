package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 회원가입 최종 요청 DTO입니다.
 * 기본 정보 + 학습 정보 + 프로필 정보를 포함합니다.
 */
@Getter
@NoArgsConstructor
public class RegisterRequest {

    /**
     * 사용자 아이디
     */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    /**
     * 사용자 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    /**
     * 사용자 이메일
     */
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    /**
     * 사용자 닉네임
     */
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    /**
     * 학년 (1, 2, 3 중 선택)
     */
    @NotNull(message = "학년은 필수 입력 값입니다.")
    private Integer grade;

    /**
     * 관심 과목 리스트 (예: 수학, 영어 등)
     */
    @NotNull(message = "과목은 최소 1개 이상 선택해야 합니다.")
    private List<String> subjects;

    /**
     * 공부 습관 (예: 새벽형, 야행성, 주말 집중형 등)
     */
    @NotBlank(message = "공부 습관은 필수 입력 값입니다.")
    private String studyHabit;

    /**
     * 프로필 이미지 URL (선택)
     */
    private String profileImageUrl;
}