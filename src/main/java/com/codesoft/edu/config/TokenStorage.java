package com.codesoft.edu.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStorage {
    private static final TokenStorage INSTANCE = new TokenStorage();
    private TokenStorage() {
    }

    public static TokenStorage getInstance() {
        return INSTANCE;
    }

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final Set<String> activeTokens = ConcurrentHashMap.newKeySet();

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public boolean isActiveToken(String token) {
        return activeTokens.contains(token);
    }

    public boolean addActiveToken(String token) {
        return activeTokens.add(token);
    }

    public void transferTokenToBlacklist(String token) {
        activeTokens.remove(token);
        blacklistedTokens.add(token);
    }
}
