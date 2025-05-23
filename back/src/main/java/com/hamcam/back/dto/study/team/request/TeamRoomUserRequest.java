package com.hamcam.back.dto.study.team.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomUserRequest {
    private Long userId;
    private Long roomId;
}
