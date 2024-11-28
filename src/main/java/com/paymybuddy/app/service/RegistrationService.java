package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for handling user registration operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registerDTO the registration details provided by the user.
     * @throws IllegalArgumentException if the email is invalid or already in use.
     * @throws RuntimeException if the registration process fails.
     */
    public void registerUser(RegisterDTO registerDTO) {
        log.info("Attempting to register user with email: {}", registerDTO.getEmail());

        // Validate email format
        if (!EmailValidationService.isValidEmail(registerDTO.getEmail())) {
            log.error("Invalid email format: {}", registerDTO.getEmail());
            throw new IllegalArgumentException("Invalid email format: " + registerDTO.getEmail());
        }

        // Check if the email is already in use
        if (userService.existsByEmail(registerDTO.getEmail())) {
            log.error("Registration failed. Email already in use: {}", registerDTO.getEmail());
            throw new EntityAlreadyExistsException("Registration failed. Email already in use: "+ registerDTO.getEmail());
        }

        // Create and register the new user
        User user = new User();
        user.setUserName(registerDTO.getUserName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword()); // Assume password is already hashed if needed

        try {
            userService.registerAndCreateAccount(user);
            log.info("User registered successfully with email: {}", registerDTO.getEmail());
        } catch (Exception e) {
            log.error("Failed to register user with email: {}", registerDTO.getEmail(), e);
            throw new RuntimeException("Failed to register user.", e);
        }
    }
}
