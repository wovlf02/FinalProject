// src/main/java/com/hamcam/back/service/todolist/impl/TodoServiceImpl.java
package com.hamcam.back.service.todolist.impl;

import com.hamcam.back.dto.todolist.TodoDto;
import com.hamcam.back.entity.todolist.Todo;
import com.hamcam.back.entity.auth.User;
import com.hamcam.back.repository.todolist.TodoRepository;
import com.hamcam.back.repository.auth.UserRepository;
import com.hamcam.back.service.todolist.TodoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoServiceImpl(TodoRepository todoRepository,
                           UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public List<TodoDto> getTodosByUser(String username) {
        User user = getCurrentUser(username);
        return todoRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TodoDto createTodo(String username, TodoDto dto) {
        User user = getCurrentUser(username);
        Todo todo = new Todo();
        todo.setText(dto.getText());
        todo.setDone(false);
        todo.setUser(user);
        Todo saved = todoRepository.save(todo);
        return toDto(saved);
    }

    @Override
    public TodoDto toggleTodo(String username, Long todoId) {
        User user = getCurrentUser(username);
        Todo todo = todoRepository.findById(todoId)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Todo not found or unauthorized"));
        todo.setDone(!todo.isDone());
        return toDto(todo);
    }

    @Override
    public void deleteTodo(String username, Long todoId) {
        User user = getCurrentUser(username);
        Todo todo = todoRepository.findById(todoId)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Todo not found or unauthorized"));
        todoRepository.delete(todo);
    }

    private TodoDto toDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setText(todo.getText());
        dto.setDone(todo.isDone());
        return dto;
    }
}