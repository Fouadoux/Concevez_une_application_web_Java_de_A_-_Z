package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
    @Test
    public void testFindById() {
        User user = new User();
        user.setUserName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("password");

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setUserName("Mark Smith");
        user.setEmail("mark.smith@example.com");
        user.setPassword("password");

        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");

        User savedUser = userRepository.save(user);

        savedUser.setUserName("UpdatedUser");
        savedUser.setEmail("updateduser@example.com");

        User updatedUser = userRepository.save(savedUser);

        User foundUser = userRepository.findById(updatedUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("UpdatedUser");
        assertThat(foundUser.getEmail()).isEqualTo("updateduser@example.com");
    }
}
