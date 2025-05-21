package com.hamcam.back.dto.community.chat.request;

import lombok.*;

/**
 * [ChatEnterRequest]
 *
 * 사용자가 채팅방에 입장할 때 전송하는 WebSocket 메시지 요청 DTO입니다.
 * WebSocket의 /pub/chat/enter 로 전송되어, 서버는 마지막 읽은 메시지를 갱신합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEnterRequest {

    /**
     * 입장할 채팅방의 ID
     */
    private Long roomId;
}
