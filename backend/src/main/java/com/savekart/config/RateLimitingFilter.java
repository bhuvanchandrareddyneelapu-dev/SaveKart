package com.savekart.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private final Map<String, UserRequestCount> requestCounts = new ConcurrentHashMap<>();

    private static class UserRequestCount {
        final AtomicInteger count = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = getClientIp(request);
        long currentTime = System.currentTimeMillis();

        UserRequestCount requestCount = requestCounts.computeIfAbsent(clientIp, k -> new UserRequestCount());

        synchronized (requestCount) {
            if (currentTime - requestCount.startTime > 60000) {
                requestCount.startTime = currentTime;
                requestCount.count.set(1);
            } else {
                if (requestCount.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Too many requests. Rate limit exceeded (Max 120 req/min).\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
