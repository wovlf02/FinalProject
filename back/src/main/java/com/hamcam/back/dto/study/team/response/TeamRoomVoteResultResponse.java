package com.hamcam.back.dto.study.team.response;

import lombok.*;

/**
 * QUIZ 풀이 투표 결과 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomVoteResultResponse {

    /**
     * 발표자 ID
     */
    private Long presenterId;

    /**
     * 성공 여부 (과반수 이상 성공이면 true)
     */
    private boolean success;

    /**
     * 성공 시 지급된 포인트
     */
    private int rewardPoints;
}
