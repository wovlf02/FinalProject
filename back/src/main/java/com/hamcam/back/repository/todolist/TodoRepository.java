// src/main/java/com/hamcam/back/repository/todolist/TodoRepository.java
package com.hamcam.back.repository.todolist;

import com.hamcam.back.entity.todolist.Todo;
import com.hamcam.back.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser(User user);
}