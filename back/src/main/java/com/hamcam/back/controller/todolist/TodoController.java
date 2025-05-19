// src/main/java/com/hamcam/back/controller/todolist/TodoController.java
package com.hamcam.back.controller.todolist;

import com.hamcam.back.dto.todolist.TodoDto;
import com.hamcam.back.service.todolist.TodoService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping
    public List<TodoDto> getTodos() {
        return todoService.getTodosByUser(getCurrentUsername());
    }

    @PostMapping
    public TodoDto addTodo(@RequestBody TodoDto dto) {
        return todoService.createTodo(getCurrentUsername(), dto);
    }

    @PutMapping("/{id}/toggle")
    public TodoDto toggleTodo(@PathVariable Long id) {
        return todoService.toggleTodo(getCurrentUsername(), id);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(getCurrentUsername(), id);
    }
}
