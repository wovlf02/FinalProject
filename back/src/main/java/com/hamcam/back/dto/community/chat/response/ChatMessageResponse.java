package com.hamcam.back.dto.community.chat.response;

import com.hamcam.back.entity.chat.ChatMessageType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * [ChatMessageResponse]
 *
 * 채팅 메시지 응답 DTO입니다.
 * 메시지 유형에 따라 TEXT/IMAGE/FILE/READ_ACK 등으로 렌더링됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatMessageResponse {

    private Long messageId;           // 메시지 고유 ID
    private Long roomId;              // 채팅방 ID
    private Long senderId;            // 보낸 사용자 ID
    private String nickname;          // 사용자 닉네임
    private String profileUrl;        // 프로필 이미지 URL
    private String content;           // 메시지 본문 or 파일명
    private ChatMessageType type;     // 메시지 타입 (TEXT, IMAGE, FILE, READ_ACK 등)
    private String storedFileName;    // 저장된 파일명
    private LocalDateTime sentAt;     // 전송 시각
    private int unreadCount;          // 읽지 않은 인원 수
}
