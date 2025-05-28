package com.hamcam.back.dto.study.team.rest.response.inner;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingInfo {
    private Long userId;
    private String nickname;
    private Integer focusTime;  // 단위: 초 또는 분

    public static RankingInfo of(Long userId, String nickname, Integer focusTime) {
        return RankingInfo.builder()
                .userId(userId)
                .nickname(nickname)
                .focusTime(focusTime)
                .build();
    }
}
