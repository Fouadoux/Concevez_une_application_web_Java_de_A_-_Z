package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.RegistrationService;
import com.paymybuddy.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");

        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> registrationService.registerUser(registerDTO));

        verify(userService, times(1)).existsByEmail("test@example.com");
    }

    @Test
    void testRegisterUser_ShouldCallUserService_WhenValidData() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setUserName("Test User");
        registerDTO.setPassword("password123");

        when(userService.existsByEmail("test@example.com")).thenReturn(false);

        // Act
        registrationService.registerUser(registerDTO);

        // Assert
        verify(userService, times(1)).existsByEmail("test@example.com");
        verify(userService, times(1)).registerAndCreateAccount(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setUserName("Test User");
        registerDTO.setPassword("password123");


        when(userService.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () ->
                registrationService.registerUser(registerDTO));

        assertEquals("Registration failed. Email already in use: "+ registerDTO.getEmail(), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailInvalidFormat_ThrowsException() {

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example..com");
        registerDTO.setUserName("Test User");
        registerDTO.setPassword("password123");


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.registerUser(registerDTO));

        assertEquals("Invalid email format: " + registerDTO.getEmail(), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

}
