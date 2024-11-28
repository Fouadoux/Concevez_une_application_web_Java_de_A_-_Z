package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;

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

            // Délégation au service pour effectuer l'enregistrement
            registrationService.registerUser(registerDTO);

            log.info("User registered successfully with email: {}", registerDTO.getEmail());
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/login")).build();

        } catch (IllegalArgumentException e) {
            log.warn("Error during registration for email {}: {}", registerDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error during user registration for email {}: ", registerDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to register user"));
        }
    }
}
