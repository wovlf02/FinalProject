package com.hamcam.back.repository.dashboard;

import com.hamcam.back.entity.dashboard.Todo;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * 특정 사용자에 대한 전체 Todo 목록 조회 (날짜 + 우선순위 기준 정렬)
     */
    List<Todo> findByUser(User user);

    /**
     * 특정 날짜의 Todo 목록 조회 (날짜별 조회용)
     */
    List<Todo> findAllByUserAndTodoDateOrderByPriorityDesc(User user, LocalDate todoDate);

    /**
     * 특정 달의 Todo 목록 조회 (캘린더 통합용)
     */
    List<Todo> findAllByUserAndTodoDateBetween(User user, LocalDate start, LocalDate end);
}
