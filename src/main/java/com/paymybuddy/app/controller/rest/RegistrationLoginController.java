package com.paymybuddy.app.controller.rest;


import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.AppAccountService;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegistrationLoginController {

    private final UserService userService;
    private final AppAccountService appAccountService;


    @PostMapping("/api/register")
    public ResponseEntity<?> registerUser(@ModelAttribute RegisterDTO registerDTO) {
        try {
            log.info("Registering user with email: {}", registerDTO.getEmail());

            // Vérifier si l'utilisateur existe déjà
            if (userService.existsByEmail(registerDTO.getEmail())) {
                log.warn("Email already in use: {}", registerDTO.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Email already in use"));
            }



            // Créer et enregistrer l'utilisateur
            User user = new User();
            user.setUserName(registerDTO.getUserName());
            user.setEmail(registerDTO.getEmail());
            user.setPassword(registerDTO.getPassword());

            userService.registerAndCreateAccount(user);

            log.info("User registered successfully with email: {}", registerDTO.getEmail());
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/login")).build();

        } catch (Exception e) {
            log.error("Error during user registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to register user"));
        }
    }

}
