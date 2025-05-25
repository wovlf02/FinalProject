package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * [ChatStompMessage]
 * 클라이언트 → 서버로 전달되는 WebSocket STOMP 메시지 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatStompMessage {

    /**
     * 채팅방 ID
     */
    @JsonProperty("room_id")
    private Long roomId;

    /**
     * 메시지 타입 (예: TEXT, FILE, ENTER, READ 등)
     */
    private String type;

    /**
     * 텍스트 메시지 내용 또는 파일 이름
     */
    private String content;

    /**
     * 저장된 파일명 (파일 메시지일 경우)
     */
    @JsonProperty("stored_file_name")
    private String storedFileName;

    /**
     * 메시지 ID (읽음 처리용)
     */
    @JsonProperty("message_id")
    private Long messageId;
}
