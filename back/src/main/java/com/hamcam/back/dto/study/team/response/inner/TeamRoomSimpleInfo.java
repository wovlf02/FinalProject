package com.hamcam.back.dto.study.team.response.inner;

import com.hamcam.back.entity.study.team.FocusRoom;
import com.hamcam.back.entity.study.team.QuizRoom;
import com.hamcam.back.entity.study.team.StudyRoom;
import com.hamcam.back.entity.study.team.RoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamRoomSimpleInfo {

    private Long roomId;
    private String title;
    private RoomType roomType;
    private boolean isActive;
    private String inviteCode;

    public static TeamRoomSimpleInfo from(StudyRoom room) {
        RoomType roomType = null;
        if (room instanceof QuizRoom) roomType = RoomType.QUIZ;
        else if (room instanceof FocusRoom) roomType = RoomType.FOCUS;

        return TeamRoomSimpleInfo.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .isActive(room.isActive())
                .inviteCode(room.getInviteCode())
                .build();
    }
}
