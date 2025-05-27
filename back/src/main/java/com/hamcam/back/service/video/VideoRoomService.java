package com.hamcam.back.service.video;

import com.hamcam.back.entity.video.RoomType;
import com.hamcam.back.entity.video.VideoRoom;
import java.util.List;

public interface VideoRoomService {
    VideoRoom createRoom(Long hostId,
                         Long teamId,
                         String title,
                         RoomType type,
                         Integer maxParticipants,
                         String password,
                         Integer targetTime);

    List<VideoRoom> getRoomsByTeam(Long teamId);

    void joinRoom(Integer roomId, Long userId);

    void leaveRoom(Integer roomId, Long userId);

    Long getParticipantCount(Integer roomId);

    // ✅ 추가: 참여자 ID 목록 조회 메서드
    List<Long> getParticipants(Integer roomId);
}
