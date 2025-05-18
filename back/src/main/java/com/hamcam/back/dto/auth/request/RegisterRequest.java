package com.hamcam.back.dto.auth.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * [RegisterRequest]
 *
 * 회원가입 시 클라이언트로부터 전달되는 모든 정보를 담는 요청 DTO입니다.
 * 필수 입력 필드와 선택 입력 필드를 구분하여 서버 유효성 검사를 수행합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /**
     * 사용자 아이디 (중복 불가)
     */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    /**
     * 사용자 비밀번호 (최소 8자 이상)
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
     * 사용자 실명 (선택사항일 경우 @NotBlank 제거 가능)
     */
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    /**
     * 닉네임 (중복 불가)
     */
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    /**
     * 학년 (1, 2, 3 중 하나)
     */
    @NotNull(message = "학년은 필수 입력 값입니다.")
    private Integer grade;

    /**
     * 사용자가 선택한 과목 목록
     */
    @NotNull(message = "과목은 최소 1개 이상 선택해야 합니다.")
    private List<String> subjects;

    /**
     * 공부 습관 (집중형 / 루틴형 등)
     */
    @NotBlank(message = "공부 습관은 필수 입력 값입니다.")
    private String studyHabit;

    /**
     * 전화번호 (숫자만, 하이픈 제외)
     */
    @Pattern(regexp = "^\\d{10,15}$", message = "전화번호는 숫자만 포함되어야 하며 10자리 이상 15자리 이하여야 합니다.")
    private String phone;

    /**
     * 프로필 이미지 URL (선택 사항)
     */
    private String profileImageUrl;
}
