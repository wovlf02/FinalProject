package com.hamcam.back.dto.video.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * ✅ 집중 경쟁방 실시간 랭킹 응답 DTO
 */
@Getter
@Builder
public class RankingResponse {

    private Long roomId;

    private List<RankingEntry> ranking;

    @Getter
    @Builder
    public static class RankingEntry {
        private Long userId;
        private String nickname;
        private int focusTime; // 초 단위 누적 집중 시간
        private boolean finished; // 목표 도달 여부
    }
}
