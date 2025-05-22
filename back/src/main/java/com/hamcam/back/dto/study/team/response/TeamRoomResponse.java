package com.hamcam.back.dto.study.team.response;

import com.hamcam.back.entity.study.TeamRoom;
import lombok.Builder;
import lombok.Getter;

/**
 * 팀 스터디방 응답 DTO
 * <p>
 * 클라이언트에게 팀 방 정보(제목, 유형, 최대 인원 등)를 제공하는 응답 형식입니다.
 * 민감한 정보(예: 비밀번호)는 포함하지 않습니다.
 * </p>
 */
@Getter
@Builder
public class TeamRoomResponse {

    /**
     * 스터디방 고유 ID
     */
    private Long id;

    /**
     * 스터디방 제목
     */
    private String title;

    /**
     * 스터디방 유형 (QUIZ, FOCUS 등)
     */
    private String roomType;

    /**
     * 최대 참여 인원
     */
    private int maxParticipants;

    /**
     * TeamRoom 엔티티를 기반으로 응답 DTO 생성
     *
     * @param room TeamRoom 엔티티
     * @return TeamRoomResponse DTO
     */
    public static TeamRoomResponse from(TeamRoom room) {
        return TeamRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .roomType(room.getRoomType())
                .maxParticipants(room.getMaxParticipants())
                .build(); // 🔒 비밀번호는 응답에서 제외
    }
}
