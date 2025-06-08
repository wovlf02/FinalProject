package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatRoomDetailRequest]
 * 채팅방 상세 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDetailRequest {

    @NotNull(message = "roomId는 필수입니다.")
    private Long roomId;
}
