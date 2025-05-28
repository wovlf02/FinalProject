package com.hamcam.back.repository.study;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.StudyRoom;
import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRoomParticipantRepository extends JpaRepository<StudyRoomParticipant, Long> {

    /** ✅ 특정 방의 참가자 전체 조회 */
    List<StudyRoomParticipant> findByRoom(StudyRoom room);

    /** ✅ 참가 여부 체크 (중복 입장 방지) */
    boolean existsByRoomAndUser(StudyRoom room, User user);

    /** ✅ 참가자 정보 단건 조회 */
    Optional<StudyRoomParticipant> findByRoomAndUser(StudyRoom room, User user);

    /** ✅ 방 참가자 수 */
    int countByRoom(StudyRoom room);

    /** ✅ 방장 정보 조회 */
    Optional<StudyRoomParticipant> findByRoomAndIsHostTrue(StudyRoom room);

    /** ✅ 특정 유저가 방장인지 확인 */
    boolean existsByRoomAndUserAndIsHostTrue(StudyRoom room, User user);
}
