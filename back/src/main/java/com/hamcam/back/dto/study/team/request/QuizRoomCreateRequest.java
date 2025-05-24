package com.hamcam.back.dto.study.team.request;

import com.hamcam.back.entity.study.TeamRoomMode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRoomCreateRequest {
    private String roomName;
    private String password;

    public TeamRoomMode getMode() {
        return TeamRoomMode.QUIZ;
    }
}
