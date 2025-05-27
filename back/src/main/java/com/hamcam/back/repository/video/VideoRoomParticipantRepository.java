// src/main/java/com/hamcam/back/repository/video/VideoRoomParticipantRepository.java
package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRoomParticipantRepository extends JpaRepository<VideoRoomParticipant, Long> {
    boolean existsByRoom_IdAndUserId(Integer roomId, Long userId);
    void deleteByRoom_IdAndUserId(Integer roomId, Long userId);
    long countByRoom_Id(Integer roomId);
}
