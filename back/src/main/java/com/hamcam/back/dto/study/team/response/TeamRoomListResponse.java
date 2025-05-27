package com.hamcam.back.dto.study.team.response;

import com.hamcam.back.dto.study.team.response.inner.TeamRoomSimpleInfo;
import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamRoomListResponse {
    private List<TeamRoomSimpleInfo> rooms;

    public static TeamRoomListResponse of(List<StudyRoomParticipant> participation) {
        List<TeamRoomSimpleInfo> rooms = participation.stream()
                .map(p -> TeamRoomSimpleInfo.from(p.getRoom()))
                .toList();
        return TeamRoomListResponse.builder().rooms(rooms).build();
    }
}
