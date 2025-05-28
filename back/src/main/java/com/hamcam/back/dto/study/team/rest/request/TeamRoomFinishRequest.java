package com.hamcam.back.dto.study.team.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRoomFinishRequest {
    private Long roomId;  // 종료할 방 ID
}
