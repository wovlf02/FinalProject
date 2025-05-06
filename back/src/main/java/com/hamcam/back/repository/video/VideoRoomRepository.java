package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hamcam.back.entity.study.TeamRoom;


import java.util.List;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {
    List<VideoRoom> findByTeamId(Long teamId);
}
