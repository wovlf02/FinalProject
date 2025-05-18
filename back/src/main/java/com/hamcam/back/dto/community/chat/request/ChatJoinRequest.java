package com.hamcam.back.dto.community.chat.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [ChatJoinRequest]
 *
 * 채팅방 입장 또는 퇴장 시 사용하는 요청 DTO입니다.
 * WebSocket 연결 또는 REST API 요청을 통해 roomId와 userId를 서버에 전달합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatJoinRequest {

    /**
     * 입장 또는 퇴장하는 사용자 ID
     */
    private Long userId;

    /**
     * 입장 또는 퇴장 대상 채팅방 ID
     */
    private Long roomId;
}
