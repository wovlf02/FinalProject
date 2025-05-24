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

    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;
}
