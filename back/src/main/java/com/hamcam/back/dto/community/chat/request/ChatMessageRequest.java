package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;

    private String content;

    @NotNull(message = "type은 필수입니다.")
    private ChatMessageType type;

    private String storedFileName;
}
