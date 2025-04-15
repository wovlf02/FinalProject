package com.studymate.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 인증 성공 시 반환되는 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class VerifyEmailResponse {

    /**
     * 이메일 인증 결과 메시지
     */
    private String message;
}
