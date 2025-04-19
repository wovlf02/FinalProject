package com.hamcam.back.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 메시지 전송 결과 응답 DTO
 * WebSocket 또는 REST 전송 결과에 대한 상태 보고용
 */
@Data
@AllArgsConstructor
public class MessageSendResultResponse {

    /**
     * 메시지 전송 성공 여부
     */
    private boolean success;

    /**
     * 메시지 또는 상태 설명
     */
    private String message;
}
