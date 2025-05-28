package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.FocusRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FocusRoomRepository extends JpaRepository<FocusRoom, Long> {

    /** ✅ 초대 코드로 FocusRoom 조회 */
    Optional<FocusRoom> findByInviteCode(String inviteCode);

    /** ✅ 방이 아직 활성 상태인지 체크 (입장 시 필수) */
    boolean existsByIdAndIsActiveTrue(Long roomId);
}
