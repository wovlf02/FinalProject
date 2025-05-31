package com.hamcam.back.dto.study.team.rest.response;

import com.hamcam.back.entity.study.team.RoomType;
import com.hamcam.back.entity.study.team.StudyRoom;
import lombok.Builder;
import lombok.Getter;

/**
 * ✅ 팀방 목록 조회 응답 DTO (간단 정보)
 */
@Getter
@Builder
public class TeamRoomSimpleInfo {
    private Long roomId;
    private String title;
    private RoomType roomType;
    private boolean isActive;
    private String inviteCode;
    private String password;          // 🔹 추가
    private int maxParticipants;      // 🔹 추가

    public static TeamRoomSimpleInfo from(StudyRoom room) {
        return TeamRoomSimpleInfo.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .isActive(room.isActive())
                .inviteCode(room.getInviteCode())
                .password(room.getPassword())
                .maxParticipants(room.getParticipants() != null
                        ? room.getParticipants().size()
                        : 0)
                .build();
    }
}
