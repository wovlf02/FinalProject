package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.RoomType;
import com.hamcam.back.entity.study.team.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    /**
     * ✅ 초대 코드로 방 조회 (중복 방지 및 입장 처리 등)
     */
    Optional<StudyRoom> findByInviteCode(String inviteCode);

    /**
     * ✅ 방 활성화 여부 확인
     */
    boolean existsByIdAndIsActiveTrue(Long roomId);
    List<StudyRoom> findAllByIsActiveTrue();
    List<StudyRoom> findByRoomTypeAndIsActiveTrue(RoomType roomType);

}
