package com.hamcam.back.dto.study.team.rest.response;

import com.hamcam.back.dto.study.team.rest.response.inner.ParticipantInfo;
import com.hamcam.back.entity.study.team.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamRoomDetailResponse {
    private Long roomId;
    private String title;
    private RoomType roomType;
    private boolean isActive;
    private String inviteCode;

    private List<ParticipantInfo> participants;
}
