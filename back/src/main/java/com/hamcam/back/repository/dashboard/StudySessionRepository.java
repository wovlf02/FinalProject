package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    /**
     * 특정 사용자의 특정 날짜 공부 세션 조회
     */
    List<StudySession> findByUserAndStudyDate(User user, LocalDate studyDate);

    /**
     * 특정 사용자, 날짜 범위 내 공부 세션 조회 (주간, 월간 분석용)
     */
    List<StudySession> findByUserAndStudyDateBetween(User user, LocalDate startDate, LocalDate endDate);

    /**
     * 특정 사용자의 과목별 집중률 평균, 정확도 평균 등을 집계
     * → 과목 연관 필드가 추가되었을 경우 별도 쿼리 필요
     */

    /**
     * 가장 집중률이 높았던 하루 (지난 30일 기준)
     */
    @Query("SELECT s FROM StudySession s " +
            "WHERE s.user = :user AND s.studyDate >= :startDate " +
            "ORDER BY s.focusRate DESC, s.studyDate ASC")
    List<StudySession> findTopFocusDay(User user, LocalDate startDate);
}
