package com.hamcam.back.dto.community.chat.request;

import lombok.*;

/**
 * [ChatEnterRequest]
 * 사용자가 채팅방에 입장하거나 나갈 때 사용하는 요청 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEnterRequest {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 입장할 채팅방의 ID
     */
    private Long roomId;
}
