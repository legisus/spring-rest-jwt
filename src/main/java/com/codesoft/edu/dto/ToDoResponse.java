package com.codesoft.edu.dto;

import com.codesoft.edu.model.ToDo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ToDoResponse {
    Long id;
    String title;
    LocalDateTime created_at;
    Long owner_id;

    public ToDoResponse(ToDo toDo){
        id = toDo.getId();
        title = toDo.getTitle();
        created_at = toDo.getCreatedAt();
        owner_id = toDo.getOwner().getId();
    }
}
