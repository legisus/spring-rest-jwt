package com.codesoft.edu.dto;

import com.codesoft.edu.model.ToDo;

import java.time.LocalDateTime;

public class ToDoTransformer {
    public static ToDoDto convertToDto(ToDo todo) {
        return new ToDoDto(
                todo.getId(),
                todo.getTitle(),
                todo.getCreatedAt(),
                todo.getOwner().getId()
        );
    }

    public static ToDo convertToEntity(ToDoDto todoDto) {
        ToDo todo = new ToDo();
        todo.setTitle(todoDto.getTitle());
        todo.setCreatedAt(LocalDateTime.now());
        todo.setCollaborators(todoDto.getCollaborators());
        return todo;
    }
}

