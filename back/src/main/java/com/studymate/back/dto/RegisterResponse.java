package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 성공 시 반환되는 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterResponse {

    /**
     * 회원가입 결과 메시지
     */
    private String message;
}
