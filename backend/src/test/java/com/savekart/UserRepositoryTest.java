package com.savekart;

import com.savekart.model.Role;
import com.savekart.model.User;
import com.savekart.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUserByEmail() {
        String email = "repo_test_" + System.currentTimeMillis() + "@savekart.com";
        User user = User.builder()
                .fullName("Repo Test User")
                .email(email)
                .password("hashed_pass")
                .role(Role.ROLE_USER)
                .verified(true)
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(email);
        assertTrue(found.isPresent());
        assertEquals("Repo Test User", found.get().getFullName());
    }
}
