package com.hamcam.back.dto.study.team.request;

import com.hamcam.back.entity.study.TeamRoomMode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRoomCreateRequest {
    private Long userId;
    private TeamRoomMode mode;         // "QUIZ" 또는 "FOCUS"
    private String roomName;           // 방 이름 → 기존 subject 를 이름으로 사용한다고 가정
    private String grade;
    private String month;
    private String difficulty;
    private Long selectedProblemId;
    private Integer targetTime;        // 기존 goalMinutes → targetTime 으로 수정
    private String password;
}
