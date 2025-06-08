package com.hamcam.back.dto.study.team.socket.response;

import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 투표 결과 응답
 */
@Getter
@Builder
public class VoteResultResponse {
    private Long roomId;
    private boolean success;          // 과반 이상 성공 여부
    private int yesVotes;             // "SUCCESS" 투표 수
    private int noVotes;              // "FAIL" 투표 수
    private int totalParticipants;   // 전체 인원 수
}
