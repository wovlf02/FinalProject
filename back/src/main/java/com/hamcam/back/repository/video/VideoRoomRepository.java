package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {

    /** 초대 코드로 방 조회 */
    Optional<VideoRoom> findByInviteCode(String inviteCode);

    /** 활성화된 모든 방 조회 */
    List<VideoRoom> findByIsActiveTrue();
}
