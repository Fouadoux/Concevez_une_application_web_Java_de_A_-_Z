package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Role;
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

    @Autowired
    private RoleRepository roleRepository;


    @Test
    public void testSaveUser() {
        Role role=new Role();
        role.setRoleName("user");
        roleRepository.save(role);

        User user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(role);

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
    @Test
    public void testFindById() {
        Role role=new Role();
        role.setRoleName("user");
        roleRepository.save(role);

        User user1 = new User();
        user1.setUserName("Jane Doe");
        user1.setEmail("jane.doe@example.com");
        user1.setPassword("password");
        user1.setRole(role);

        User savedUser = userRepository.save(user1);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    public void testDeleteUser() {
        Role role=new Role();
        role.setRoleName("user");
        roleRepository.save(role);

        User user = new User();
        user.setUserName("Mark Smith");
        user.setEmail("mark.smith@example.com");
        user.setPassword("password");
        user.setRole(role);

        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testUpdateUser() {
        Role role=new Role();
        role.setRoleName("user");
        roleRepository.save(role);

        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("testuser@example.com");
        user.setRole(role);

        user.setPassword("password123");User savedUser = userRepository.save(user);

        savedUser.setUserName("UpdatedUser");
        savedUser.setEmail("updateduser@example.com");

        User updatedUser = userRepository.save(savedUser);

        User foundUser = userRepository.findById(updatedUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("UpdatedUser");
        assertThat(foundUser.getEmail()).isEqualTo("updateduser@example.com");
    }
}
