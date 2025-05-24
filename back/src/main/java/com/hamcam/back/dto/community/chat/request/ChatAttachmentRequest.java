package com.hamcam.back.dto.community.chat.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ChatAttachmentRequest]
 * 채팅 메시지 ID를 기반으로 첨부파일 처리할 때 사용하는 단일 DTO
 */
@Getter
@NoArgsConstructor
public class ChatAttachmentRequest {
    private Long messageId;
}
