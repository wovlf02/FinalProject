package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomParticipantRepository extends JpaRepository<StudyRoomParticipant, Long> {

    /**
     * ✅ 특정 유저가 참여 중인 모든 팀방 조회
     */
    List<StudyRoomParticipant> findByUserId(Long userId);

    /**
     * ✅ 특정 방의 모든 참가자 조회
     */
    List<StudyRoomParticipant> findByStudyRoomId(Long roomId);

    /**
     * ✅ 특정 유저가 해당 방에 참여 중인지 여부 확인
     */
    boolean existsByStudyRoomIdAndUserId(Long roomId, Long userId);

    /**
     * ✅ 특정 방의 전체 인원 수
     */
    int countByStudyRoomId(Long roomId);

    void deleteAllByStudyRoomId(Long roomId);

}
