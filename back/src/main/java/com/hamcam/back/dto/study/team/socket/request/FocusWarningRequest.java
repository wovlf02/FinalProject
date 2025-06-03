package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusWarningRequest {

    /** 방 ID */
    private Long roomId;

    /** 경고 사유 (예: "away", "sleep") */
    private String reason;
}
