package com.paymybuddy.app.integration;

import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EmailAlreadyExistsException;
import com.paymybuddy.app.exception.EntityAlreadyExistsException;
import com.paymybuddy.app.exception.InvalidEmailException;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.RegistrationService;
import com.paymybuddy.app.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegisterUserIT {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppAccountRepository appAccountRepository;

    @Autowired
    private UserService userService;

    private Role defaultRole;

    @BeforeEach
    void setUp() {
        defaultRole = new Role();
        defaultRole.setRoleName("USER");
        roleRepository.save(defaultRole);
    }

    @Test
    void testRegisterUser_success() {
        // Arrange
        User newUser = new User();
        newUser.setUserName("Bob");
        newUser.setEmail("Bob@example.fr");
        newUser.setPassword("securepassword");
        newUser.setCreatedAt(LocalDateTime.now());

        // Act
        userService.registerAndCreateAccount(newUser);

        // Assert
        Optional<User> savedUser = userRepository.findByEmail("Bob@example.fr");
        assertTrue(savedUser.isPresent());
        assertEquals("Bob", savedUser.get().getUserName());

        Optional<AppAccount> savedAccount = appAccountRepository.findByUserId(savedUser.get().getId());
        assertTrue(savedAccount.isPresent());
        assertEquals(0L, savedAccount.get().getBalance());
    }

    @Test
    void testRegisterUser_emailAlreadyExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setUserName("ExistingUser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("password");
        existingUser.setRole(defaultRole);
        userRepository.save(existingUser);

        AppAccount appAccount = new AppAccount();
        appAccount.setUser(existingUser);
        appAccount.setBalance(0L);
        appAccount.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(appAccount);

        RegisterDTO newUser = new RegisterDTO();
        newUser.setUserName("TestUser");
        newUser.setEmail("existing@example.com");
        newUser.setPassword("securepassword");

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () ->
                registrationService.registerUser(newUser));

        assertEquals("Registration failed. Email already in use: "+ newUser.getEmail(), exception.getMessage());

        assertEquals(1, userRepository.findAll().size());
        assertEquals(1, appAccountRepository.findAll().size());
    }

    @Test
    void testRegisterUser_invalidEmail() {
        // Arrange
        RegisterDTO newUser = new RegisterDTO();
        newUser.setUserName("TestUser");
        newUser.setEmail("invalid-email");
        newUser.setPassword("securepassword");

        // Act & Assert
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () ->
                registrationService.registerUser(newUser));

        assertEquals("Invalid email format: " + newUser.getEmail(), exception.getMessage());

        assertTrue(userRepository.findAll().isEmpty());
        assertTrue(appAccountRepository.findAll().isEmpty());
    }

    @Test
    void testRegisterUser_accountCreationFails() {
        // Arrange
        User newUser = new User();
        newUser.setUserName("FailUser");
        newUser.setEmail("failuser@example.com");
        newUser.setPassword("securepassword");

        roleRepository.deleteAll();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.registerAndCreateAccount(newUser));

        assertTrue(exception.getMessage().contains("Default role 'USER' not found"));

        assertTrue(userRepository.findAll().isEmpty());
        assertTrue(appAccountRepository.findAll().isEmpty());
    }
}
