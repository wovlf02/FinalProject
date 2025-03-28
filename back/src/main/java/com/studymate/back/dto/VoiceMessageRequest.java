package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 음성 메시지 요청 DTO
 * 사용자가 음성 메시지를 녹음하여 전송할 때 사용됨
 */
@Getter
@Setter
@NoArgsConstructor
public class VoiceMessageRequest {

    /**
     * 음성 파일 URL
     * 사용자가 업로드한 음성 메시지의 URL
     */
    @NotBlank(message = "음성 메시지 파일 URL이 필요합니다.")
    private String audioUrl;
}
