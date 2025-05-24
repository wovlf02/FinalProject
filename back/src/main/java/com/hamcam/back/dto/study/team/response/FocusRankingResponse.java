package com.hamcam.back.dto.study.team.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 공부시간 경쟁 종료 후 랭킹 결과 응답 DTO
 */
@Getter
@Builder
public class FocusRankingResponse {

    /** 방 제목 */
    private String roomName;

    /** 공부 시간 기준 정렬된 유저 랭킹 리스트 */
    private List<Rank> ranks;

    /**
     * 개별 랭킹 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class Rank {
        private String nickname;
        private int totalMinutes;
    }
}
