package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusReadyRequest {
    private Long roomId;
    private Long userId; // 세션으로 받을 수도 있지만 socket 식별용으로 포함 가능
}
