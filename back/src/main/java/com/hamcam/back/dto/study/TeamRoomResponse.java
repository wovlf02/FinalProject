package com.hamcam.back.dto.study;

import com.hamcam.back.entity.study.TeamRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamRoomResponse {
    private Long id;
    private String title;
    private String roomType;
    private int maxParticipants;
    private String password;

    public static TeamRoomResponse from(TeamRoom room) {
        return TeamRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .maxParticipants(room.getMaxParticipants())
                .password(room.getPassword())
                .build();
    }
}
