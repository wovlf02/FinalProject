package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatRoomDeleteRequest]
 * 채팅방 삭제 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDeleteRequest {

    /**
     * 사용자 ID (삭제 요청자)
     */
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    /**
     * 삭제할 채팅방 ID
     */
    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;
}
