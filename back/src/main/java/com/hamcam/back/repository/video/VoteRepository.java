package com.hamcam.back.repository.video;

import com.hamcam.back.entity.video.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    /** 특정 방 + 발표자 기준 전체 투표 조회 */
    List<Vote> findAllByRoomIdAndTargetUserId(Long roomId, Long targetUserId);

    /** 중복 투표 방지 (방 + 발표자 + 투표자) */
    boolean existsByRoomIdAndTargetUserIdAndVoterId(Long roomId, Long targetUserId, Long voterId);
}
