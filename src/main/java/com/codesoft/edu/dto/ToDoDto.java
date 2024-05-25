package com.codesoft.edu.dto;

import com.codesoft.edu.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToDoDto {
    private Long id;

    @NotBlank(message = "The 'title' cannot be empty")
    private String title;

    private LocalDateTime createdAt;

    private Long ownerId;

    private List<User> collaborators;

    public ToDoDto() {
    }

    public ToDoDto(long id, String title, LocalDateTime createdAt, Long ownerId) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.collaborators = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<User> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<User> collaborators) {
        this.collaborators = collaborators;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDoDto toDoDto = (ToDoDto) o;
        return Objects.equals(id, toDoDto.id) &&
               Objects.equals(title, toDoDto.title) &&
               Objects.equals(createdAt, toDoDto.createdAt) &&
               Objects.equals(ownerId, toDoDto.ownerId) &&
               Objects.equals(collaborators, toDoDto.collaborators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, createdAt, ownerId, collaborators);
    }

    @Override
    public String toString() {
        return "ToDoDto{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", createdAt=" + createdAt +
               ", ownerId=" + ownerId +
               ", collaborators=" + collaborators +
               '}';
    }
}

