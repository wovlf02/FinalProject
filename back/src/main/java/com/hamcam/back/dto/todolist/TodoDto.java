// src/main/java/com/hamcam/back/dto/todolist/TodoDto.java
package com.hamcam.back.dto.todolist;

import lombok.Data;

@Data
public class TodoDto {
    private Long id;
    private String text;
    private boolean done;
}