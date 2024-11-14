package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.LoginDTO;
import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.AppAccountService;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegistrationLoginController {

    private final UserService userService;
    private final AppAccountService appAccountService;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@ModelAttribute RegisterDTO registerDTO) {
        try {
            log.info("Registering user with email: {}", registerDTO.getEmail());

            // Vérifier si l'utilisateur existe déjà
            if (userService.existsByEmail(registerDTO.getEmail())) {
                log.warn("Email already in use: {}", registerDTO.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
            }

            // Créer un nouvel utilisateur à partir du DTO
            log.info("Creating user object...");
            User user = new User();
            user.setUserName(registerDTO.getUserName());
            user.setEmail(registerDTO.getEmail());
            user.setPassword(registerDTO.getPassword());

            // Enregistrer l'utilisateur
            log.info("Saving user to database...");
            userService.createUser(user);

            User user1=userService.findByEmail(registerDTO.getEmail());
            appAccountService.createAccountForUser(user1.getId());

            log.info("User registered successfully with email: {}", registerDTO.getEmail());
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/login")).build();

        } catch (Exception e) {
            log.error("Error during user registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }
}
