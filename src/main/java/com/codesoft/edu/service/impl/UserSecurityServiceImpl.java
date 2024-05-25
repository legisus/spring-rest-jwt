package com.codesoft.edu.service.impl;

import com.codesoft.edu.model.User;
import com.codesoft.edu.repository.UserRepository;
import com.codesoft.edu.security.UserDetailsImpl;
import com.codesoft.edu.service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {
    private final UserRepository userRepository;

    @Autowired
    public UserSecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isOwner(long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail);
            return user != null && user.getId() == userId;
        }
        return false;
    }

    public boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @Override
    public long getAuthenticatedUserId() {
        Object authorized = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (authorized instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authorized;
            return userDetails.getId();
        } else if (authorized instanceof String) {
            String email = (String) authorized;
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return user.getId();
            }
        }
        return -1;
    }
}
