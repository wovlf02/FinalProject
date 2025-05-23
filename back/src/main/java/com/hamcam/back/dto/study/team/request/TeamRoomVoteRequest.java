package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomVoteRequest {
    private Long userId;         // 투표한 사용자 ID
    private Long roomId;         // 팀 학습방 ID
    private Long targetUserId;   // 발표자 ID (투표 대상자)
    private Integer score;       // 점수 (1~5)
}
