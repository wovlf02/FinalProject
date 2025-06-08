package com.hamcam.back.repository;

import com.hamcam.back.entity.auth.User;
import com.hamcam.back.entity.dashboard.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserOrderByTodoDateDesc(User user);
    List<Todo> findAllByUserAndTodoDateBetween(User user, LocalDate start, LocalDate end);
    List<Todo> findAllByUserAndTodoDateOrderByPriorityDesc(User user, LocalDate date);
} 