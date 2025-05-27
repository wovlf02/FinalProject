package com.hamcam.back.repository.study;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.study.team.StudyRoom;
import com.hamcam.back.entity.study.team.StudyRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRoomParticipantRepository extends JpaRepository<StudyRoomParticipant, Long> {

    /** ✅ 특정 방에 참여한 유저인지 조회 */
    Optional<StudyRoomParticipant> findByRoomAndUser(StudyRoom room, User user);

    /** ✅ 해당 방의 모든 참가자 조회 */
    List<StudyRoomParticipant> findAllByRoom(StudyRoom room);

    /** ✅ 특정 유저가 참여 중인 모든 방 */
    List<StudyRoomParticipant> findAllByUser(User user);

    /** ✅ 방에서 퇴장한 모든 유저 삭제 */
    void deleteAllByRoom(StudyRoom room);

    /** ✅ 중복 입장 방지용 체크 */
    boolean existsByRoomAndUser(StudyRoom room, User user);
}
