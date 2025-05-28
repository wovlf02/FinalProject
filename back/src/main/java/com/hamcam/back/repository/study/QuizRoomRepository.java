package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.QuizRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRoomRepository extends JpaRepository<QuizRoom, Long> {

    /** ✅ 초대 코드로 방 조회 (입장 시 사용) */
    Optional<QuizRoom> findByInviteCode(String inviteCode);

    /** ✅ 방이 아직 유효한지 확인 (중복 입장 방지 or 종료 방 차단) */
    boolean existsByIdAndIsActiveTrue(Long roomId);

    /** ✅ 문제 필터 조건 (과목, 학년, 월, 난이도) 기반 조회 */
    List<QuizRoom> findBySubjectAndGradeAndMonthAndDifficulty(
            String subject,
            String grade,
            String month,
            String difficulty
    );
}
