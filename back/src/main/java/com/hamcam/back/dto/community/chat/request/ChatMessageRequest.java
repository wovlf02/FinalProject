package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hamcam.back.entity.chat.ChatMessageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatMessageRequest]
 *
 * WebSocket 또는 REST 방식으로 채팅 메시지를 전송할 때 사용하는 요청 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageRequest {

    /**
     * 보낸 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 채팅방 ID
     */
    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;

    /**
     * 메시지 본문 (텍스트, 파일명 등)
     */
    private String content;

    /**
     * 메시지 타입 (TEXT, IMAGE, FILE 등)
     */
    @NotNull(message = "메시지 타입은 필수입니다.")
    private ChatMessageType type;

    /**
     * 저장된 파일명 (파일 메시지 전용)
     */
    private String storedFileName;
}
