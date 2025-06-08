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

    private Long roomId;
}
