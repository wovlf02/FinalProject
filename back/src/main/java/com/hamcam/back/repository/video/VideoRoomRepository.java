// src/main/java/com/hamcam/back/repository/video/VideoRoomRepository.java
package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Integer> {
    // 팀 ID로 방 목록 조회
    List<VideoRoom> findByTeamId(Long teamId);
}
