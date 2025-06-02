package com.hamcam.back.dto.community.chat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatReadRequest]
 * WebSocket을 통해 메시지를 읽었음을 서버에 알릴 때 사용하는 요청 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatReadRequest {

    @NotNull(message = "roomId는 필수입니다.")
    @JsonProperty("room_id")
    private Long roomId;

    @NotNull(message = "messageId는 필수입니다.")
    @JsonProperty("message_id")
    private Long messageId;
}
