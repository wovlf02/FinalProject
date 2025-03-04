package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 메시지 번역 요청 DTO
 * 사용자가 채팅 메시지를 번역할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class TranslateRequest {

    /**
     * 원본 메시지
     * 번역할 원본 메시지 내용
     */
    @NotBlank(message = "번역할 메시지를 입력해야 합니다.")
    private String message;

    /**
     * 목표 언어 코드
     * 번역할 언어 (ex: en -> 영어, ko -> 한국어)
     */
    @NotBlank(message = "목표 언어 코드를 입력해야 합니다.")
    private String targetLanguage;
}
