package com.codesoft.edu.controller;

import com.codesoft.edu.service.UserSecurityService;
import com.codesoft.edu.dto.ToDoDto;
import com.codesoft.edu.dto.ToDoResponse;
import com.codesoft.edu.dto.ToDoTransformer;
import com.codesoft.edu.model.ToDo;
import com.codesoft.edu.model.User;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todos/{user_id}")
@Slf4j
public class ToDoController {
    private final UserService userService;
    private final ToDoService toDoService;
    private final UserSecurityService userSecurityService;

    @Autowired
    public ToDoController(UserService userService, ToDoService toDoService, UserSecurityService userSecurityService) {
        this.userService = userService;
        this.toDoService = toDoService;
        this.userSecurityService = userSecurityService;
    }

    @GetMapping
    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && (hasAuthority('ADMIN') || @userSecurityService.isOwner(#userId))")
    public ResponseEntity<List<ToDoResponse>> getAll(@PathVariable("user_id") long userId, @RequestHeader String authorization) {
        log.info("Get ToDos by user_id = {}", userService.readById(userId));

           List<ToDo> toDos = toDoService.getByUserId(userId);

              List<ToDoResponse> toDoResponses = toDos.stream()
                     .map(ToDoResponse::new)
                     .toList();

              return ResponseEntity.ok(toDoResponses);
    }


    @PostMapping
    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && (hasAuthority('ADMIN') or @userSecurityService.isOwner(#userId))")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createToDo(@PathVariable("user_id") long userId, @Validated @RequestBody ToDoDto toDoDto,
                                        BindingResult result, @RequestHeader String authorization) {
        if (result.hasErrors()) {
            log.error("ToDo creation failed: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        if (!userSecurityService.isAdmin() && userSecurityService.getAuthenticatedUserId() != userId) {
            log.error("ToDo creation failed. Please enter authorized userId");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ToDo toDo = ToDoTransformer.convertToEntity(toDoDto);
        toDo.setOwner(userService.readById(userId));

        try {
            toDoService.create(toDo);
            ToDoResponse toDoResponse = new ToDoResponse(toDo);
            log.info("ToDo created: {}", toDoResponse);
            return ResponseEntity.ok(toDoResponse);
        } catch (Exception e) {
            log.error("Failed to create ToDo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{todo_id}")
    public ResponseEntity<?> deleteToDo(@PathVariable("user_id") long userId,
                                        @PathVariable("todo_id") long todoId,
                                        @RequestHeader String authorization) {
        toDoService.delete(todoId);
        log.info("ToDo deleted: id={}", todoId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{todo_id}")
    public ResponseEntity<?> updateToDo(@PathVariable("user_id") long userId,
                                        @PathVariable("todo_id") long todoId,
                                        @Valid @RequestBody ToDoDto toDoDto,
                                        BindingResult result,
                                        @RequestHeader String authorization) {
        if (result.hasErrors()) {
            log.error("User update failed: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        ToDo existingToDo = toDoService.readById(todoId);
        existingToDo.setTitle(toDoDto.getTitle());
        toDoService.update(existingToDo);
        ToDoResponse toDoResponse = new ToDoResponse(existingToDo);
        log.info("ToDo updated: {}", toDoResponse);
        return ResponseEntity.ok(toDoResponse);
    }

    @PutMapping("/{id}/collaborator/")
    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && (hasAuthority('ADMIN') || @authorizationComponent.mayAddDeleteCollaborators(#id))")
    public ResponseEntity<?> addCollaborator(@PathVariable long id, @PathVariable("user_id") long userId,
                                             @RequestHeader String authorization) {
        ToDo todo = toDoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.add(userService.readById(userId));
        todo.setCollaborators(collaborators);
        toDoService.update(todo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/collaborator")
    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && (hasAuthority('ADMIN') || @authorizationComponent.mayAddDeleteCollaborators(#id))")
    public ResponseEntity<?> removeCollaborator(@PathVariable long id, @PathVariable("user_id") long userId, @RequestHeader String authorization) {
        ToDo todo = toDoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        toDoService.update(todo);
        return ResponseEntity.ok().build();
    }
}
