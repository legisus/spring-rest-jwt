package com.codesoft.edu.security;

import com.codesoft.edu.model.Task;
import com.codesoft.edu.model.ToDo;
import com.codesoft.edu.model.User;
import com.codesoft.edu.service.TaskService;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component("authorizationComponent")
public class AuthorizationComponent {

    private final UserService userService;
    private final ToDoService toDoService;
    private final TaskService taskService;

    private Authentication authentication;

    @Autowired
    public AuthorizationComponent(UserService userService, ToDoService toDoService, TaskService taskService) {
        this.userService = userService;
        this.toDoService = toDoService;
        this.taskService = taskService;
    }

    public boolean mayDeleteUpdateUser(Long userId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getName().equals(getUserById(userId))) {
                return true;
            } else {
                throw new AccessDeniedException("User does not have the necessary permissions.");
            }
        }
        throw new UsernameNotFoundException("User not found in getCurrentUserName() method, SecurityContextHolder");
    }

    public boolean mayViewEditDeleteTodo(Long todoId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {

            return false;
        }
        String currentUserName = authentication.getName();
        ToDo todo = toDoService.readById(todoId);

        boolean merge;
        boolean isOwner = todo.getOwner().getUsername().equals(currentUserName);
        boolean isCollaborator = todo.getCollaborators().stream()
                .map(User::getUsername)
                .anyMatch(username -> username.equals(currentUserName));

        merge = isOwner || isCollaborator;

        if (!merge) {
            throw new AccessDeniedException("User does not have the necessary permissions.");
        }

        return true;
    }

    public boolean mayDeleteTask(Long taskId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        String currentUserName = authentication.getName();
        Task task = taskService.readById(taskId);

        boolean isOwner = task.getTodo().getOwner().getUsername().equals(currentUserName);

        if(!isOwner) throw new AccessDeniedException("User does not have the necessary permissions.");
        return true;
    }

    public boolean mayAddDeleteCollaborators(Long todoId) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String currentUserName = authentication.getName();
        ToDo todo = toDoService.readById(todoId);

        boolean isOwner = todo.getOwner().getUsername().equals(currentUserName);

        if (!isOwner) throw new AccessDeniedException("User does not have the necessary permissions.");

        return true;
    }

    private String getUserById(Long id) {
        return userService.readById(id).getEmail();
    }
}
