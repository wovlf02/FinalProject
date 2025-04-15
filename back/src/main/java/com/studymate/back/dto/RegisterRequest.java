package com.studymate.back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 DTO
 */
@Getter
@Setter
public class RegisterRequest {

    /**
     * 아이디
     * 3~50자
     * 필수 입력
     */
    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 3, max = 50, message = "아이디는 3자 이상 50자 이하로 입력해주세요.")
    private String username;

    /**
     * 비밀번호
     * 8~100자
     * 필수 입력
     */
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해주세요")
    private String password;

    /**
     * 이름
     * 100자 이하
     * 필수 입력
     */
    @NotBlank(message = "이름을 입력해주세요")
    @Size(max = 100, message = "이름은 최대 100자까지 입력 가능합니다.")
    private String name;

    /**
     * 전화번호
     * 15자 이하
     * 필수 입력
     */
    @NotBlank(message = "전화번호를 입력해주세요")
    @Size(max = 15, message = "전화번호는 최대 15자까지 입력 가능합니다.")
    private String phone;

    /**
     * 이메일
     * 이메일 형식 검사
     * 필수 입력
     */
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    private String email;
}
