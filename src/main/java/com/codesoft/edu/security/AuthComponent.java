package com.codesoft.edu.security;

import com.codesoft.edu.config.TokenStorage;
import com.codesoft.edu.model.Task;
import com.codesoft.edu.model.ToDo;
import com.codesoft.edu.model.User;
import com.codesoft.edu.service.TaskService;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("authComponent")
public class AuthComponent {

    private final UserService userService;
    private final ToDoService toDoService;
    private final TaskService taskService;

    @Autowired
    public AuthComponent(UserService userService, ToDoService toDoService, TaskService taskService) {
        this.userService = userService;
        this.toDoService = toDoService;
        this.taskService = taskService;
    }

    public boolean mayDeleteUpdateUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getName().equals(getUserById(userId))) {
                return true;
            } else {
                log.error("User does not have the necessary permissions.");
                return false;
            }
        }
        log.error("User not found in getCurrentUserName() method, SecurityContextHolder");
        return false;
    }

    public boolean mayViewEditDeleteTodo(Long todoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
            log.error("User does not have the necessary permissions.");
            return false;
        }

        return true;
    }

    public boolean mayManageTask(Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        String currentUserName = authentication.getName();
        Task task = taskService.readById(taskId);

        boolean isOwner = task.getTodo().getOwner().getUsername().equals(currentUserName);

        if(!isOwner) {
            log.error("User does not have the necessary permissions.");
            return false;
        }
        return true;
    }

    public boolean mayAddDeleteCollaborators(Long todoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String currentUserName = authentication.getName();
        ToDo todo = toDoService.readById(todoId);

        boolean isOwner = todo.getOwner().getUsername().equals(currentUserName);

        if (!isOwner) {
            log.error("User does not have the necessary permissions.");
            return false;

        }
        return true;
    }

    private String getUserById(Long id) {
        return userService.readById(id).getEmail();
    }

    public boolean isTokenNotBlacklisted(String authorization) {
        TokenStorage tokenStorage = TokenStorage.getInstance();
        if (authorization != null) {
            String token = authorization.split(" ")[1];
            return !tokenStorage.isTokenBlacklisted(token);
        }
        return false;
    }


}
