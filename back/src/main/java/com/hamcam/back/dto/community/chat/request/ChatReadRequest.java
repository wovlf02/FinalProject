package com.hamcam.back.dto.community.chat.request;

import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket으로 수신되는 읽음 처리 요청
 */
@Getter
@Setter
public class ChatReadRequest {
    private String type; // "READ"
    private Long roomId;
    private Long messageId;
}
