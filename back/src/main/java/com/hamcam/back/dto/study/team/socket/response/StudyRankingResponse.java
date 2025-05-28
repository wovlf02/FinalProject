package com.hamcam.back.dto.study.team.socket.response;

import com.hamcam.back.dto.study.team.rest.response.inner.RankingInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyRankingResponse {
    private Long roomId;
    private List<RankingInfo> rankings;
}
