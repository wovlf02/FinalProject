package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
     * 가장 집중률이 높았던 하루 (지난 30일 기준)
     */
    @Query("SELECT s FROM StudySession s " +
            "WHERE s.user = :user AND s.studyDate >= :startDate " +
            "ORDER BY s.focusRate DESC, s.studyDate ASC")
    List<StudySession> findTopFocusDay(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate
    );
}
