package com.hamcam.back.dto.video;

import com.hamcam.back.entity.video.VideoRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class VideoRoomResponse {
    private Long id;
    private Long teamId;
    private String title;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static VideoRoomResponse from(VideoRoom room) {
        return VideoRoomResponse.builder()
                .id(room.getId())
                .teamId(room.getTeamId())
                .title(room.getTitle())
                .isActive(room.getIsActive())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
