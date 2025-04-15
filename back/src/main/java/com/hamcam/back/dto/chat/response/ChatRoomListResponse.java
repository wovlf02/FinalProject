package com.hamcam.back.dto.chat.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 채팅방 목록용 응답 DTO (요약)
 */
@Data
public class ChatRoomListResponse {

    /**
     * 채팅방 ID
     */
    private Long roomId;

    /**
     * 채팅방 이름
     */
    private String name;

    /**
     * 마지막 메시지 내용 (요약)
     */
    private String lastMessage;

    /**
     * 마지막 메시지 시간
     */
    private LocalDateTime lastMessageAt;
}
