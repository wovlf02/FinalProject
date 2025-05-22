package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatReadRequest]
 *
 * WebSocket을 통해 채팅 메시지를 읽었음을 서버에 알릴 때 사용하는 요청 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatReadRequest {

    /**
     * 채팅방 ID
     */
    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;

    /**
     * 읽은 마지막 메시지 ID
     */
    @NotNull(message = "messageId는 필수입니다.")
    private Long messageId;

    /**
     * 읽은 사용자 ID
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
