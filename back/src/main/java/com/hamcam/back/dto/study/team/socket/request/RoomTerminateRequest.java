package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 방 종료 요청
 */
@Getter
@Setter
public class RoomTerminateRequest {
    private Long roomId;
}
