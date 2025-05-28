package com.hamcam.back.dto.study.team.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRoomVoteRequest {
    private Long roomId;
    private Long targetUserId;  // 발표자 ID
    private boolean success;    // true: 성공, false: 실패
}
