package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomStudyTimeRequest {
    private Long roomId;
    private Integer studyMinutes;
}
