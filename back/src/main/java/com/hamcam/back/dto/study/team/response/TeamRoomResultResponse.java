package com.hamcam.back.dto.study.team.response;

import lombok.*;

import java.util.List;

/**
 * Focus 모드 결과 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomResultResponse {

    private Long winnerId;              // 승리자 ID
    private String winnerNickname;      // 승리자 닉네임
    private List<RankEntry> rankings;   // 전체 랭킹

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RankEntry {
        private Long userId;
        private String nickname;
        private int studyMinutes;
    }
}
