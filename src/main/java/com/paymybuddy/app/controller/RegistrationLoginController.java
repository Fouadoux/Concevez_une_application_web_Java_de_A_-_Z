package com.paymybuddy.app.controller;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class RegistrationLoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        userService.createUser(user);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword()));
        return ResponseEntity.ok("Login successful");
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
