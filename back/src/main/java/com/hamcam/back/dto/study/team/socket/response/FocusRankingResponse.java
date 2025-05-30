package com.hamcam.back.dto.study.team.socket.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * ✅ 1분마다 서버가 클라이언트에게 보내는 실시간 랭킹 응답
 */
@Getter
@Builder
public class FocusRankingResponse {
    private Long roomId;                               // 방 ID
    private List<FocusRankingEntry> rankings;          // 유저별 순위 정보

    @Getter
    @Builder
    public static class FocusRankingEntry {
        private String nickname;       // 유저 닉네임
        private int totalSeconds;      // 누적 집중 시간
        private boolean goalAchieved;  // 목표 시간 달성 여부
    }
}
