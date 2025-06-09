package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 준비 상태 요청
 */
@Getter
@Setter
public class RoomReadyRequest {
    private Long roomId;
}
