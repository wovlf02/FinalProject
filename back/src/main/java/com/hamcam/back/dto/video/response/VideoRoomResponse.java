package com.hamcam.back.dto.video.response;

import com.hamcam.back.entity.video.VideoRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

/**
 * [VideoRoomResponse]
 * 화상 채팅방 정보 응답 DTO
 */
@Getter
@Builder
public class VideoRoomResponse {

    private Long roomId;
    private Long teamId;
    private Long hostId;
    private String title;
    private int currentUserCount;
    private String createdAt;

    /**
     * VideoRoom 엔티티를 DTO로 변환하는 정적 메서드
     */
    public static VideoRoomResponse fromEntity(VideoRoom room) {
        return VideoRoomResponse.builder()
                .roomId(room.getId())
                .teamId(room.getTeamId())
                .hostId(room.getHostId())
                .title(room.getTitle())
                .createdAt(room.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .currentUserCount(0) // Redis 값은 별도로 조회
                .build();
    }
}
