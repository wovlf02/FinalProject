package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 아이디 중복 확인 요청 DTO
 */
@Getter
@Setter
public class UsernameCheckRequest {

    /**
     * 아이디
     * 3~50자
     * 필수 입력
     */
    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 3, max = 50, message = "아이디는 3자 이상 50자 이하로 입력해주세요.")
    private String username;
}
