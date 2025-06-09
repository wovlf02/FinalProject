package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomProblemChangeRequest {
    private Long roomId;
    private Long problemId;
}
