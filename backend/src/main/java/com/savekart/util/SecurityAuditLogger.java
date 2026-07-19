package com.savekart.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SecurityAuditLogger {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditLogger.class);

    public void logEvent(String eventType, String userEmail, String clientIp, String details) {
        logger.info("[AUDIT-LOG] [{}] timestamp={} user={} ip={} details={}",
                eventType, LocalDateTime.now(), userEmail, clientIp, details);
    }
}
