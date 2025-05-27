package com.hamcam.back.dto.video.response;

import com.hamcam.back.dto.video.response.inner.ParticipantInfo;
import com.hamcam.back.entity.video.RoomType;
import com.hamcam.back.entity.video.VideoRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ 학습방 상세 정보 응답 DTO
 */
@Getter
@Builder
public class VideoRoomInfoResponse {

    private Long roomId;
    private String title;
    private String inviteCode;
    private String password;
    private int maxParticipants;
    private RoomType roomType;
    private boolean isActive;
    private LocalDateTime createdAt;

    private List<ParticipantInfo> participants;

    public static VideoRoomInfoResponse from(VideoRoom room) {
        return VideoRoomInfoResponse.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .inviteCode(room.getInviteCode())
                .password(room.getPassword()) // 보안 제외 조건이므로 포함 가능
                .maxParticipants(room.getMaxParticipants())
                .roomType(room.getRoomType())
                .isActive(room.isActive())
                .createdAt(room.getCreatedAt())
                .participants(
                        room.getParticipants().stream()
                                .map(ParticipantInfo::from)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
