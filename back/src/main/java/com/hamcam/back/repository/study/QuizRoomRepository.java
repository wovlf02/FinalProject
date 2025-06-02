package com.hamcam.back.repository.study;

import com.hamcam.back.entity.study.team.QuizRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRoomRepository extends JpaRepository<QuizRoom, Long> {

    /**
     * ✅ 문제 ID로 방 조회 (예: 중복 문제 방 방지, 분석용 등)
     */
    List<QuizRoom> findByProblemId(Long problemId);

    /**
     * ✅ 문제 필터링 조건 기반 조회 (선택적으로 사용 가능)
     */
    List<QuizRoom> findBySubjectAndGradeAndMonthAndDifficulty(
            String subject,
            int grade,
            int month,
            String difficulty
    );

    List<QuizRoom> findBySubjectAndGradeAndMonthAndDifficulty(String subject, Integer grade, Integer month, String difficulty);

}
