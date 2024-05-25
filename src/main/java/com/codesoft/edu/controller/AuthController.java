package com.codesoft.edu.controller;

import com.codesoft.edu.config.TokenStorage;
import com.codesoft.edu.dto.*;
import com.codesoft.edu.model.Role;
import com.codesoft.edu.model.User;
import com.codesoft.edu.security.JwtUtils;
import com.codesoft.edu.security.UserDetailsImpl;
import com.codesoft.edu.service.impl.RoleServiceImpl;
import com.codesoft.edu.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private UserServiceImpl userService;
    private RoleServiceImpl roleService;
    private PasswordEncoder passwordEncoder;
    private TokenStorage tokenStorage;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils, UserServiceImpl userService,
                          RoleServiceImpl roleService,
                          PasswordEncoder passwordEncoder, TokenStorage tokenStorage) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.tokenStorage = tokenStorage;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            log.info("Login request: {}", loginRequest);
            log.info("Email: {}", loginRequest.getEmail());
            log.info("Password: {}", loginRequest.getPassword());

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            log.info("UsernamePasswordAuthenticationToken:" + usernamePasswordAuthenticationToken);

            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            log.info("Authentication: {}", authentication);

            if (authentication.isAuthenticated()) {
                log.info("Authentication successful: {}", authentication);
            } else {
                log.info("Authentication failed");
            }

            log.info(authentication.getPrincipal().toString());
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            String jwtToken = jwtUtils.generateTokenFromUsername(user.getUsername());
//            TokenStorage tokenStorage = TokenStorage.getInstance();
            log.info("Token saved: " + tokenStorage.addActiveToken(jwtToken));

            AuthResponse authResponse = new AuthResponse(user.getUsername(), jwtToken);
            return ResponseEntity.ok().body(authResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getCause() + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        Role role = roleService.findByName(userDto.getRole());
        if (role == null) {
            role = new Role();
            if (userDto.getRole() == null) {
                if (roleService.findByName("USER") == null) {
                    role.setName("USER");
                } else {
                    role = roleService.findByName("USER");
                }
            } else {
                role.setName(userDto.getRole().toUpperCase());
            }
            roleService.create(role);
        }

        User newUser = UserTransformer.convertToEntity(userDto);
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);
        newUser.setRole(role);

        userService.create(newUser);
        UserResponse userResponse = new UserResponse(newUser);
        log.info("User created: {}", userResponse);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
//            TokenStorage tokenStorage = TokenStorage.getInstance();
//            log.info("IsActiveToken: " + tokenStorage.isActiveToken(token));
//            log.info("Token removed: " + tokenStorage.transferTokenToBlacklist(token));
            tokenStorage.transferTokenToBlacklist(token);

            return ResponseEntity.ok("Logged out successfully ");
        } else {
            return ResponseEntity.badRequest().body("Invalid token format.");
        }
    }
}
