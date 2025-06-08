package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 클라이언트에서 집중 시간 전송 (1분 단위)
 */
@Getter
@Setter
public class FocusTimeUpdateRequest {
    private Long roomId;           // 방 ID
    private int focusedSeconds;   // 새로 측정된 집중 시간 (누적 아님)
}
