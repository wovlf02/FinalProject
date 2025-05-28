package com.hamcam.back.dto.study.team.socket.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomClosedResponse {
    private Long roomId;
    private String reason; // 예: "정상 종료", "인원 부족", "문제풀이 실패"
}
