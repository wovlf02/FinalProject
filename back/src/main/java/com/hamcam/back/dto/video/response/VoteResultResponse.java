package com.hamcam.back.dto.video.response;

import com.hamcam.back.dto.video.response.inner.VoteSummary;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 투표 결과 응답 DTO
 */
@Getter
@Builder
public class VoteResultResponse {

    private Long roomId;
    private VoteSummary result;
}
