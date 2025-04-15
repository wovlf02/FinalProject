package com.hamcam.back.dto.chat.request;

import lombok.Data;

/**
 * 채팅 메시지 전송 요청 DTO
 * <p>
 * WebSocket 또는 REST API 방식으로 텍스트/파일 메시지를 전송할 때 사용.
 * 메시지 타입은 TEXT, IMAGE, FILE 등으로 분기 가능.
 * </p>
 */
@Data
public class ChatMessageRequest {

    /**
     * 메시지를 전송한 사용자 ID
     */
    private Long senderId;

    /**
     * 메시지 본문 내용 (텍스트 or 파일명)
     */
    private String content;

    /**
     * 메시지 유형 (TEXT, IMAGE, FILE 등)
     */
    private String messageType;
}
