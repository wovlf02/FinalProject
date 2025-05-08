package com.hamcam.back.dto.study;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRoomCreateRequest {
    private String title;               // 방 제목
    private String roomType;            // QUIZ / FOCUS
    private Integer maxParticipants;    // 최대 인원
    private String password;            // 비밀번호
}
