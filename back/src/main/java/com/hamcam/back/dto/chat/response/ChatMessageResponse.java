package com.hamcam.back.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 */
@Data
@AllArgsConstructor
public class ChatMessageResponse {

    /**
     * 메시지 고유 ID
     */
    private Long messageId;

    /**
     * 메시지를 보낸 사용자 ID
     */
    private Long senderId;

    /**
     * 보낸 사용자 닉네임
     */
    private String senderNickname;

    /**
     * 메시지 내용
     */
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE 등)
     */
    private String messageType;

    /**
     * 메시지 전송 시각
     */
    private LocalDateTime sentAt;
}
