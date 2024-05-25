package com.codesoft.edu.service;

import com.codesoft.edu.model.Task;

import java.util.List;

public interface TaskService {
    Task create(Task task);
    Task readById(long id);
    Task update(Task task);
    void delete(long id);
    List<Task> getAll();

    List<Task> getByTodoId(long todoId);
}
