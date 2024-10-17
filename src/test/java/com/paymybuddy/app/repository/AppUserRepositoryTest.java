package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void testSaveUser() {
        AppUser appUser = new AppUser();
        appUser.setUserName("John Doe");
        appUser.setEmail("john.doe@example.com");
        appUser.setPassword("password");

        AppUser savedAppUser = appUserRepository.save(appUser);

        assertThat(savedAppUser).isNotNull();
        assertThat(savedAppUser.getId()).isGreaterThan(0);
    }
    @Test
    public void testFindById() {
        AppUser user = new AppUser();
        user.setUserName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setPassword("password");

        AppUser savedUser = appUserRepository.save(user);
        Optional<AppUser> foundUser = appUserRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    public void testDeleteUser() {
        AppUser user = new AppUser();
        user.setUserName("Mark Smith");
        user.setEmail("mark.smith@example.com");
        user.setPassword("password");

        AppUser savedUser = appUserRepository.save(user);
        appUserRepository.delete(savedUser);

        Optional<AppUser> deletedUser = appUserRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testUpdateUser() {
        AppUser user = new AppUser();
        user.setUserName("TestUser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");

        AppUser savedUser = appUserRepository.save(user);

        savedUser.setUserName("UpdatedUser");
        savedUser.setEmail("updateduser@example.com");

        AppUser updatedUser = appUserRepository.save(savedUser);

        AppUser foundUser = appUserRepository.findById(updatedUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("UpdatedUser");
        assertThat(foundUser.getEmail()).isEqualTo("updateduser@example.com");
    }
}
