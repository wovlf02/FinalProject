package com.hamcam.back.dto.study.team.response;

import com.hamcam.back.entity.study.TeamRoom;
import com.hamcam.back.entity.study.TeamRoomMode;
import com.hamcam.back.entity.study.RoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamRoomResponse {

    private Long roomId;
    private String roomName;
    private TeamRoomMode mode;
    private RoomStatus status;
    private Integer targetTime;
    private Integer currentQuestionIndex;
    private Long currentPresenterId;
    private Long creatorId;
    private String creatorNickname;
    private LocalDateTime createdAt;

    /**
     * TeamRoom 엔티티를 응답 DTO로 변환
     */
    public static TeamRoomResponse from(TeamRoom room) {
        return TeamRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .mode(room.getMode())
                .status(room.getStatus())
                .targetTime(room.getTargetTime())
                .currentQuestionIndex(room.getCurrentQuestionIndex())
                .currentPresenterId(room.getCurrentPresenterId())
                .creatorId(room.getCreator().getId())
                .creatorNickname(room.getCreator().getNickname()) // 닉네임 필드 존재 가정
                .createdAt(room.getCreatedAt())
                .build();
    }
}
