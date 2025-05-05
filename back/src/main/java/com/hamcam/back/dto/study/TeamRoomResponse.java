package com.hamcam.back.dto.study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamRoomResponse {
    private Long id;
    private String title;
    private String roomType;
    private Integer maxParticipants;
    private String password;
}
