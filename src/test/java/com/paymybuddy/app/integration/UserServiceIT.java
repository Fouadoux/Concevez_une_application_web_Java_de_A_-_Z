package com.paymybuddy.app.integration;

import com.paymybuddy.app.dto.UpdateUserRequestDTO;
import com.paymybuddy.app.entity.*;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.*;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppAccountRepository appAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;

    private Role role;

    @BeforeEach
    void setUo() {
        role = new Role();
        role.setRoleName("USER");

        roleRepository.save(role);
    }

    @Test
    void testCreateUser_success() {
        // Arrange
        User user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());


        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser.getId());
        assertEquals("John Doe", createdUser.getUserName());
        assertTrue(userRepository.findByEmail("john.doe@example.com").isPresent());
    }


    @Test
    void testUpdateUser_success() {
        // Arrange
        User user = new User();
        user.setUserName("Old Name");
        user.setEmail("old.email@example.com");
        user.setPassword("password123");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setUserName("New Name");
        request.setEmail("new.email@example.com");

        // Act
        userService.updateUser(savedUser.getId(), request);

        // Assert
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("New Name", updatedUser.getUserName());
        assertEquals("new.email@example.com", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser_success() {
        // Arrange

        User user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        User otherUser = new User();
        otherUser.setUserName("Jane Doe");
        otherUser.setEmail("jane.doe@example.com");
        otherUser.setPassword("password123");
        otherUser.setRole(role);
        otherUser.setCreatedAt(LocalDateTime.now());
        User savedOtherUser = userRepository.save(otherUser);

        AppAccount appUser = new AppAccount();
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setBalance(0L);
        appUser.setUser(savedUser);
        appAccountRepository.save(appUser);

        AppAccount appOtherUser = new AppAccount();
        appOtherUser.setCreatedAt(LocalDateTime.now());
        appOtherUser.setBalance(0L);
        appOtherUser.setUser(savedOtherUser);
        appAccountRepository.save(appOtherUser);

        UserRelation relation = new UserRelation();
        relation.setUser(savedUser);
        relation.setUserId(savedUser.getId());
        relation.setRelatedUser(savedOtherUser);
        relation.setUserRelationId(savedOtherUser.getId());
        relation.setStatus(true);
        relation.setCreatedAt(LocalDateTime.now());

        Transaction transaction = new Transaction();
        transaction.setAmount(500L);
        transaction.setUserSender(user);
        transaction.setUserReceiver(otherUser);
        transaction.setTransactionDate(LocalDateTime.now());
        user.addSenderTransactions(transaction);
        transactionRepository.save(transaction);

        savedUser.setAppAccount(appUser);
        userRepository.save(savedUser);

        // Act
        userService.deleteUser(savedUser.getId());

        // Assert
        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
        assertFalse(appAccountRepository.findByUserId(user.getId()).isPresent());
        assertFalse(userRelationRepository.findByUserIdAndUserRelationId(savedUser.getId(), savedOtherUser.getId()).isPresent());
        assertTrue(transactionRepository.findAll().isEmpty());
    }

    @Test
    void testDeleteUser_userNotFound_throwsException() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(999));
    }

    @Test
    void testSoftDeleteUser()
    {
        User user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        userService.softDeleteUser(user.getId());
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertTrue(updatedUser.isDeleted());
    }

    @Test
    void testCancelSoftDeleteUser()
    {
        User user = new User();
        user.setUserName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        userService.softDeleteUser(user.getId());
        userService.cancelSoftDeleteUser(user.getId());
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertFalse(updatedUser.isDeleted());
    }


}
