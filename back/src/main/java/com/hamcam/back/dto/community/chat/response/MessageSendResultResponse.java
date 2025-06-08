package com.hamcam.back.dto.community.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [MessageSendResultResponse]
 *
 * 채팅 메시지 전송 결과에 대한 응답 DTO입니다.
 * WebSocket 또는 REST 방식으로 메시지를 전송한 후 클라이언트에게 상태를 전달합니다.
 *
 * 예시:
 * {
 *     "success": true,
 *     "message": "메시지가 성공적으로 전송되었습니다."
 * }
 */
@Getter
@Builder
@NoArgsConstructor
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
