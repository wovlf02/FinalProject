package com.hamcam.back.dto.study.team.socket.request;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 목표 시간 도달 시 서버에 전송
 */
@Getter
@Setter
public class FocusGoalAchievedRequest {
    private Long roomId;   // 현재 참여 중인 방 ID
}
