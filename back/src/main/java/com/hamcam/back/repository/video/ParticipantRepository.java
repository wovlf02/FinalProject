package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    /** 유저가 이미 해당 방에 참가했는지 여부 */
    boolean existsByUserIdAndRoomId(Long userId, Long roomId);

    /** 유저가 참여 중인 모든 방의 참가자 정보 */
    List<Participant> findAllByUserId(Long userId);

    /** 특정 방에 속한 모든 참가자 조회 */
    List<Participant> findAllByRoomId(Long roomId);
}
