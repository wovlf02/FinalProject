package com.hamcam.back.dto.study.team.request;

import com.hamcam.back.entity.study.TeamRoomMode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusRoomCreateRequest {
    private String roomName;
    private String password;
    private Integer targetTime;

    public TeamRoomMode getMode() {
        return TeamRoomMode.FOCUS;
    }
}
