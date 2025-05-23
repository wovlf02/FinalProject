package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomStudyTimeRequest {
    private Long userId;
    private Long roomId;

    /** 측정된 학습 시간 (단위: 분) */
    private Integer studyMinutes;
}
