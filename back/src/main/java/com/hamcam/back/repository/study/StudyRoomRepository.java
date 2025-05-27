package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    /** ✅ 초대 코드로 조회 */
    Optional<StudyRoom> findByInviteCode(String inviteCode);

    /** ✅ 중복 방지 */
    boolean existsByInviteCode(String inviteCode);

    List<StudyRoom> findByIsActiveTrue();

}
