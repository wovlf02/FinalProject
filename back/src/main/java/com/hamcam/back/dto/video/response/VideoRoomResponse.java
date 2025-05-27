// src/main/java/com/hamcam/back/dto/video/response/VideoRoomResponse.java
package com.hamcam.back.dto.video.response;

import com.hamcam.back.entity.video.VideoRoom;
import com.hamcam.back.entity.video.RoomStatus;
import com.hamcam.back.entity.video.RoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VideoRoomResponse {
    private Integer id;
    private Long hostId;
    private Long teamId;
    private String title;
    private RoomType type;
    private Integer maxParticipants;
    private boolean locked;
    private Integer targetTime;
    private RoomStatus status;
    private int currentParticipants;

    /** 엔티티 → DTO 변환 */
    public static VideoRoomResponse fromEntity(VideoRoom room) {
        VideoRoomResponse dto = new VideoRoomResponse();
        dto.setId(room.getId());
        dto.setHostId(room.getHostId());
        dto.setTeamId(room.getTeamId());
        dto.setTitle(room.getTitle());
        dto.setType(room.getType());
        dto.setMaxParticipants(room.getMaxParticipants());
        dto.setLocked(room.getPassword() != null && !room.getPassword().isEmpty());
        dto.setTargetTime(room.getTargetTime());
        dto.setStatus(room.getStatus());
        dto.setCurrentParticipants(room.getParticipants().size());
        return dto;
    }
}
