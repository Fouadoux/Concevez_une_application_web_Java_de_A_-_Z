package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        User existingUser = new User();
        existingUser.setUserName("existingUser");

        when(userRepository.findByUserName("existingUser")).thenReturn(existingUser);

        User newUser = new User();
        newUser.setUserName("existingUser");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(newUser));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_DefaultRoleNotFound_ThrowsException() {
        User newUser = new User();
        newUser.setUserName("newUser");

        when(userRepository.findByUserName("newUser")).thenReturn(null);
        when(roleRepository.findByRoleName("user")).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.createUser(newUser));

        assertEquals("Default role 'user' not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ValidUser_Success() {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("password");

        Role userRole = new Role();
        userRole.setRoleName("user");

        when(userRepository.findByUserName("newUser")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("user")).thenReturn(userRole);

        String result = userService.createUser(newUser);

        assertEquals("User registered successfully", result);
        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals(userRole, newUser.getRole());
        verify(userRepository).save(newUser);
    }

    @Test
    void getAllUsers_ReturnsUserList() {
        User user1 = new User();
        user1.setUserName("user1");
        User user2 = new User();
        user2.setUserName("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        User user = new User();
        user.setUserName("user1");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(user, result);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById(1));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateUserRole_RoleNotFound_ThrowsException() {
        User user = new User();
        user.setUserName("user1");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("admin")).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserRole(1, "admin"));

        assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void updateUserRole_ValidRole_Success() {
        User user = new User();
        user.setUserName("user1");

        Role adminRole = new Role();
        adminRole.setRoleName("admin");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("admin")).thenReturn(adminRole);

        String result = userService.updateUserRole(1, "admin");

        assertEquals("User role updated successfully", result);
        assertEquals(adminRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_UserExists_Success() {
        User user = new User();
        user.setId(1);
        user.setUserName("testUser");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = userService.deleteUser(1);

        assertEquals("User deleted successfully", result);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUser(1));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }
}
