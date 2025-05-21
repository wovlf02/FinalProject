package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatReadRequest]
 *
 * WebSocket을 통해 전달되는 채팅 메시지 읽음 처리 요청 DTO입니다.
 * 사용자가 특정 채팅방에서 특정 메시지를 읽었을 때 서버로 전송됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatReadRequest {

    /**
     * 읽음 처리를 수행할 채팅방 ID
     */
    @JsonProperty("roomId")
    @NotNull(message = "roomId는 필수 입력 값입니다.")
    private Long roomId;

    /**
     * 읽음 처리 대상이 되는 메시지 ID
     */
    @JsonProperty("messageId")
    @NotNull(message = "messageId는 필수 입력 값입니다.")
    private Long messageId;
}
