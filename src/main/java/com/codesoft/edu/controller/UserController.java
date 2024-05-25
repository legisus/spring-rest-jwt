package com.codesoft.edu.controller;

import com.codesoft.edu.dto.UserDto;
import com.codesoft.edu.dto.UserResponse;
import com.codesoft.edu.model.User;
import com.codesoft.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && hasAuthority('ADMIN') || hasAuthority('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long id, @RequestHeader String authorization) {
        User user = userService.readById(id);
        UserResponse userResponse = new UserResponse(user);
        log.info("User found: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && (hasAuthority('ADMIN') || @authComponent.mayDeleteUpdateUser(#id))")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UserDto userDto,
                                        BindingResult result, @RequestHeader String authorization) {
        if (result.hasErrors()) {
            log.error("User update failed: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        User existingUser = userService.readById(id);
        if (userDto.getFirstName() != null) existingUser.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) existingUser.setLastName(userDto.getLastName());
        if (userDto.getEmail() != null) existingUser.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(existingUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        userService.update(existingUser);
        UserResponse userResponse = new UserResponse(existingUser);
        log.info("User updated: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && (hasAuthority('ADMIN') || @authComponent.mayDeleteUpdateUser(#id))")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id, @RequestHeader String authorization) {
        userService.delete(id);
        log.info("User deleted: id={}", id);
    }

    @PreAuthorize("@authComponent.isTokenNotBlacklisted(#authorization) && hasAuthority('ADMIN') || hasAuthority('USER')")
    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getAll(@RequestHeader String authorization) {

        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .toList();
        userResponses.forEach(userResponse -> log.info("User found: {}", userResponse));
        return ResponseEntity.ok(userResponses);
    }
}
