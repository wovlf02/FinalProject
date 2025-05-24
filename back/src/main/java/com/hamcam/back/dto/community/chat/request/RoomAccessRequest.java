package com.hamcam.back.dto.community.chat.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [RoomAccessRequest]
 * 채팅방 입장 시 필요한 요청 정보
 */
@Getter
@Setter
@NoArgsConstructor
public class RoomAccessRequest {

    private Long roomId;
}
