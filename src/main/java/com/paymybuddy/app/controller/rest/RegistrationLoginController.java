package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling user registration and login-related operations.
 * Provides an endpoint to register a new user.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RegistrationLoginController {

    private final RegistrationService registrationService;

    /**
     * Endpoint to register a new user.
     * This method accepts user registration details, validates them, and delegates the registration process to the service layer.
     *
     * @param registerDTO The details of the user to be registered
     * @return A ResponseEntity indicating the result of the registration process
     */
    @PostMapping("/api/register")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute RegisterDTO registerDTO) {
        try {
            log.info("Registering user with email: {}", registerDTO.getEmail());

            registrationService.registerUser(registerDTO);

            log.info("User registered successfully with email: {}", registerDTO.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Registration successful. Please login.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error during registration for email {}: {}", registerDTO.getEmail(), e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error during user registration for email {}: ", registerDTO.getEmail(), e);

            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to register user. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
