package com.hamcam.back.dto.video.response.inner;

import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 투표 결과 요약 DTO
 */
@Getter
@Builder
public class VoteSummary {

    private Long targetUserId; // 발표자 ID
    private int agreeCount;
    private int disagreeCount;
    private boolean passed; // 과반 이상 찬성 여부
}
