package com.hamcam.back.dto.community.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * [ChatRoomListRequest]
 * 로그인한 사용자의 채팅방 목록을 조회할 때 사용되는 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
