package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hamcam.back.entity.chat.ChatMessageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatMessageRequest]
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

    /** 채팅방 ID */
    @NotNull(message = "roomId는 필수입니다.")
    @JsonProperty("room_id")
    private Long roomId;

    /** 발신자 ID */
    @NotNull(message = "senderId는 필수입니다.")
    @JsonProperty("sender_id")
    private Long senderId;

    /** 메시지 본문 (TEXT, ENTER 타입일 경우 사용) */
    private String content;

    /** 메시지 타입 (TEXT, FILE, IMAGE, ENTER 등) */
    @NotNull(message = "type은 필수입니다.")
    private ChatMessageType type;

    /** 저장된 파일 이름 (FILE, IMAGE 전용) */
    @JsonProperty("stored_file_name")
    private String storedFileName;
}
