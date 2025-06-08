package com.hamcam.back.dto.community.chat.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * [ChatBroadcastMessage]
 * STOMP를 통해 채팅방 구독자에게 전송되는 메시지 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBroadcastMessage {

    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String nickname;
    private String profileUrl;
    private String type; // TEXT, FILE 등
    private String content;
    private String storedFileName;
    private LocalDateTime sentAt;
    private int unreadCount;
}
