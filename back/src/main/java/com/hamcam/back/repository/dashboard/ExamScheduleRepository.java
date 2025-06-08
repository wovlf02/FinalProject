package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.dashboard.ExamSchedule;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {

    /**
     * 특정 사용자에 대한 모든 시험 일정 조회 (시험일 기준 오름차순 정렬)
     */
    List<ExamSchedule> findAllByUserOrderByExamDateAsc(User user);

    /**
     * 현재 날짜 기준으로 가장 가까운 시험 일정 조회
     */
    @Query("SELECT e FROM ExamSchedule e " +
            "WHERE e.user = :user AND e.examDate >= :today " +
            "ORDER BY e.examDate ASC")
    Optional<ExamSchedule> findNearestExamSchedule(
            @Param("user") User user,
            @Param("today") LocalDate today
    );
}
