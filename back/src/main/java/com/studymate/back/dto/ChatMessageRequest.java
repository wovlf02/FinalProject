package com.studymate.back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 메시지 요청 DTO
 * 사용자가 메시지를 전송할 때 사용
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequest {

    /**
     * 보낸 사용자 ID
     * 메시지를 보낸 사용자 ID
     */
    @NotNull(message = "보낸 사용자의 ID가 필요합니다.")
    private Long senderId;

    /**
     * 메시지 내용
     * 텍스트 기반 메시지
     */
    @NotBlank(message = "메시지 내용을 입력해주세요.")
    private String message;

    /**
     * 첨부파일 URL (선택 사항)
     * 이미지, 문서, 동영상 등의 파일이 첨부될 수 있음
     */
    private String attachmentUrl;
}
