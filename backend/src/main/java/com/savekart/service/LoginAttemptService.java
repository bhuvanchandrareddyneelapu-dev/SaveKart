package com.savekart.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION_MS = 15 * 60 * 1000; // 15 Minutes

    private final Map<String, Integer> attemptsMap = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTimeMap = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsMap.remove(key);
        lockTimeMap.remove(key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsMap.getOrDefault(key, 0) + 1;
        attemptsMap.put(key, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            lockTimeMap.put(key, System.currentTimeMillis() + LOCK_TIME_DURATION_MS);
        }
    }

    public boolean isBlocked(String key) {
        Long lockTime = lockTimeMap.get(key);
        if (lockTime == null) {
            return false;
        }
        if (System.currentTimeMillis() > lockTime) {
            lockTimeMap.remove(key);
            attemptsMap.remove(key);
            return false;
        }
        return true;
    }
}
