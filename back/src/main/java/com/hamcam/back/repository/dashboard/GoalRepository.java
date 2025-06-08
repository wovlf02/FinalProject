package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.dashboard.Goal;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * 사용자의 모든 목표 이력을 조회 (설정일 내림차순)
     */
    List<Goal> findAllByUserOrderBySetAtDesc(User user);

    /**
     * 사용자의 가장 최근 목표 1개 조회
     */
    @Query("SELECT g FROM Goal g WHERE g.user = :user ORDER BY g.setAt DESC")
    Optional<Goal> findLatestGoalByUser(@Param("user") User user);
}
