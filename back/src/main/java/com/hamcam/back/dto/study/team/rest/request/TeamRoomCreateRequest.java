package com.hamcam.back.dto.study.team.rest.request;

import com.hamcam.back.entity.study.team.RoomType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRoomCreateRequest {
    private String title;
    private RoomType roomType;  // QUIZ or FOCUS

    // QUIZ 전용
    private String subject;
    private String grade;
    private String month;
    private String difficulty;
    private Long problemId;

    // FOCUS 전용
    private Integer targetTime;

    private Integer maxParticipants;
    private String password;
}
