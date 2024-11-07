package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.LoginDTO;
import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RegistrationLoginController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@ModelAttribute RegisterDTO registerDTO) {
        // Vérifier si l'utilisateur existe déjà
        if (userService.existsByEmail(registerDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }

        // Créer un nouvel utilisateur à partir du DTO
        User user = new User();
        user.setUserName(registerDTO.getUserName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());

        // Enregistrer l'utilisateur
        try {
            userService.createUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/login")).build();    }
}
