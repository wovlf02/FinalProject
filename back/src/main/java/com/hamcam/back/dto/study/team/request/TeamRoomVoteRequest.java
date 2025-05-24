package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomVoteRequest {
    private Long roomId;
    private Long targetUserId;
    private Integer score;
}
