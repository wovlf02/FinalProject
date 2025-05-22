package com.hamcam.back.dto.video.response;

import com.hamcam.back.entity.video.VideoRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * [VideoRoomResponse]
 *
 * 화상 채팅방 응답 DTO
 * - 팀 학습 방에 연동된 화상 채팅방의 상세 정보를 제공합니다.
 */
@Getter
@Builder
public class VideoRoomResponse {

    /**
     * 화상 채팅방 ID
     */
    private Long id;

    /**
     * 연동된 팀 방 ID
     */
    private Long teamId;

    /**
     * 채팅방 제목
     */
    private String title;

    /**
     * 활성 상태 여부
     */
    private Boolean isActive;

    /**
     * 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * VideoRoom 엔티티를 DTO로 변환하는 팩토리 메서드
     *
     * @param room VideoRoom 엔티티
     * @return VideoRoomResponse DTO
     */
    public static VideoRoomResponse fromEntity(VideoRoom room) {
        return VideoRoomResponse.builder()
                .id(room.getId())
                .teamId(room.getTeamId())
                .title(room.getTitle())
                .isActive(room.getIsActive())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
