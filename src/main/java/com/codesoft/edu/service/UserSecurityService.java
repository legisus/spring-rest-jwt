package com.codesoft.edu.service;

public interface UserSecurityService {
    boolean isOwner(long userId);
    boolean isAdmin();
    long getAuthenticatedUserId();
}
