package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    /** ✅ 공통: 초대 코드로 방 조회 (타입 구분 없이) */
    Optional<StudyRoom> findByInviteCode(String inviteCode);

    /** ✅ 공통: 활성 상태 확인 */
    boolean existsByIdAndIsActiveTrue(Long roomId);
}
