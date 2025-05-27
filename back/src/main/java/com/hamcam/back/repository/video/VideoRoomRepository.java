// ✅ VideoRoomRepository.java
package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Integer> {
    List<VideoRoom> findByTeamId(Long teamId);
}
