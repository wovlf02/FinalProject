// ✅ 올바른 파일명: VideoRoomParticipantRepository.java
package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VideoRoomParticipantRepository 
    extends JpaRepository<VideoRoomParticipant, Long> {

    boolean existsByRoom_IdAndUserId(Integer roomId, Long userId);
    long countByRoom_Id(Integer roomId);
    void deleteByRoom_IdAndUserId(Integer roomId, Long userId);

    @Query("SELECT v.userId FROM VideoRoomParticipant v WHERE v.room.id = :roomId")
    List<Long> findUserIdsByRoomId(@Param("roomId") Integer roomId);
}
