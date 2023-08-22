package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail() {
        userRepository.save(new User(1L, "user", "mail@test.ru"));
        boolean actualUser = userRepository.existsByEmail("mail@test.ru");

        assertTrue(actualUser);
    }
}