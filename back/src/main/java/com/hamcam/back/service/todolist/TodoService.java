// src/main/java/com/hamcam/back/service/todolist/TodoService.java
package com.hamcam.back.service.todolist;

import com.hamcam.back.dto.todolist.TodoDto;
import java.util.List;

public interface TodoService {
    List<TodoDto> getTodosByUser(String username);
    TodoDto createTodo(String username, TodoDto dto);
    TodoDto toggleTodo(String username, Long todoId);
    void deleteTodo(String username, Long todoId);
}