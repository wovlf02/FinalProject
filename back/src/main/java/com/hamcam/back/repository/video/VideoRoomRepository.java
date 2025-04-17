package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {
}
