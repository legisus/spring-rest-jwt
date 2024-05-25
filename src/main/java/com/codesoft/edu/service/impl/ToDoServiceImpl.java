package com.codesoft.edu.service.impl;

import com.codesoft.edu.exception.NullEntityReferenceException;
import com.codesoft.edu.model.ToDo;
import com.codesoft.edu.repository.ToDoRepository;
import com.codesoft.edu.service.ToDoService;
import com.codesoft.edu.service.UserSecurityService;
import com.codesoft.edu.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@Service
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository todoRepository;
    private final UserService userService;
    private final UserSecurityService userSecurityService;

    public ToDoServiceImpl(ToDoRepository todoRepository, UserService userService,
                           UserSecurityService userSecurityService) {
        this.todoRepository = todoRepository;
        this.userService = userService;
        this.userSecurityService = userSecurityService;
    }

    @Override
    public ToDo create(ToDo todo) {
        if (todo != null) {
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Override
    public ToDo readById(long id) {
        return todoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("ToDo with id " + id + " not found"));
    }

    @Override
    public ToDo update(ToDo todo) {
        if (todo != null) {
            readById(todo.getId());
            return todoRepository.save(todo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        ToDo todo = readById(id);
        todoRepository.delete(todo);
    }

    @Override
    public List<ToDo> getAll() {
        return todoRepository.findAll();
    }

    @Override
    public List<ToDo> getByUserId(long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> list = (List<GrantedAuthority>) authentication.getAuthorities();
        List<String> authorities = list.stream().map(GrantedAuthority::getAuthority).toList();

        if (authorities.contains("ROLE_ADMIN") || authorities.contains("ADMIN")) {
            return todoRepository.findAll();
        } else {
            return todoRepository.getByUserId(userId);
        }

    }
}
