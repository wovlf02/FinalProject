package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandRaiseRequest {
    private Long roomId;
    private Long userId; // 손들기 요청자
}
