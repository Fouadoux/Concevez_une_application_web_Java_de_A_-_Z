package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    void createUser_ShouldReturnBadRequest_WhenUsernameExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setUserName("existingUser");

        when(userRepository.findByUserName("existingUser")).thenReturn(existingUser);

        // Act
        User newUser = new User();
        newUser.setUserName("existingUser");
        ResponseEntity<?> response = userService.createUser(newUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());        assertEquals("Username already exists", response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenDefaultRoleNotFound() {
        // Arrange
        User newUser = new User();
        newUser.setUserName("newUser");
        when(userRepository.findByUserName("newUser")).thenReturn(null);
        when(roleRepository.findByRoleName("user")).thenReturn(null);

        // Act
        ResponseEntity<?> response = userService.createUser(newUser);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Default role 'user' not found", response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully_WhenValid() {
        // Arrange
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("password");

        Role userRole = new Role();
        userRole.setRoleName("user");


        when(userRepository.findByUserName("newUser")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("user")).thenReturn(userRole);

        // Act
        ResponseEntity<?> response = userService.createUser(newUser);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals(userRole, newUser.getRole());
        verify(userRepository).save(newUser);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        User user1 = new User();
        user1.setUserName("user1");
        User user2 = new User();
        user2.setUserName("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setUserName("user1");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = userService.getUserById(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userService.getUserById(1);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void updateUserRole_ShouldReturnBadRequest_WhenRoleNotFound() {
        // Arrange
        User user = new User();
        user.setUserName("user1");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("admin")).thenReturn(null);

        // Act
        ResponseEntity<?> response = userService.updateUserRole(1, "admin");

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Role not found", response.getBody());
    }

    @Test
    void updateUserRole_ShouldUpdateRoleSuccessfully_WhenValid() {
        // Arrange
        User user = new User();
        user.setUserName("user1");

        Role adminRole = new Role();
        adminRole.setRoleName("admin");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName("admin")).thenReturn(adminRole);

        // Act
        ResponseEntity<?> response = userService.updateUserRole(1, "admin");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User role updated successfully", response.getBody());
        assertEquals(adminRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_ShouldReturnOk_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUserName("testUser");

        // Simuler la présence de l'utilisateur dans la base de données
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = userService.deleteUser(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());

        // Vérifier que la méthode delete a bien été appelée une fois
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        // Simuler l'absence de l'utilisateur dans la base de données
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userService.deleteUser(1);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, never()).delete(any(User.class)); // S'assurer que la méthode delete n'est jamais appelée
    }
}
