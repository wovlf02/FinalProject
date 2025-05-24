package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomUnsolvedPostRequest {
    private Long roomId;
    private String autoFilledTitle;
    private String questionContent;
}
