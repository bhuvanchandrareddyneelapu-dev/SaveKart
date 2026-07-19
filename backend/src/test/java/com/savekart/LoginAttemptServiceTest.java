package com.savekart;

import com.savekart.service.LoginAttemptService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private final LoginAttemptService loginAttemptService = new LoginAttemptService();

    @Test
    void testBruteForceLockout() {
        String testEmail = "lockout_user@savekart.com";

        assertFalse(loginAttemptService.isBlocked(testEmail));

        for (int i = 0; i < 5; i++) {
            loginAttemptService.loginFailed(testEmail);
        }

        assertTrue(loginAttemptService.isBlocked(testEmail));

        loginAttemptService.loginSucceeded(testEmail);
        assertFalse(loginAttemptService.isBlocked(testEmail));
    }
}
