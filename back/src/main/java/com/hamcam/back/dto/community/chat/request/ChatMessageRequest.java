package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hamcam.back.entity.chat.ChatMessageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatMessageRequest]
 *
 * WebSocket 또는 REST 방식으로 채팅 메시지를 전송할 때 사용하는 요청 DTO입니다.
 * 텍스트, 이미지, 파일 메시지를 구분하여 처리할 수 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageRequest {

    /**
     * 메시지를 보낸 사용자 ID (WebSocket 인증 토큰 기반이면 생략 가능)
     */
    @JsonProperty("senderId")
    private Long senderId;

    /**
     * 메시지가 전송되는 채팅방 ID
     */
    @JsonProperty("roomId")
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long roomId;

    /**
     * 메시지 본문 (텍스트 or 파일명)
     */
    @JsonProperty("content")
    private String content;

    /**
     * 메시지 타입: TEXT | IMAGE | FILE 등
     */
    @JsonProperty("type")
    @NotNull(message = "메시지 타입은 필수입니다.")
    private ChatMessageType type;

    /**
     * 서버에 저장된 파일명 (파일 메시지일 경우만 사용)
     */
    @JsonProperty("storedFileName")
    private String storedFileName;

    /**
     * ✅ 읽음 처리 시 필요한 메시지 ID
     */
    @JsonProperty("messageId")
    private Long messageId;
}
