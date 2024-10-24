package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j  // Utilise SLF4J pour le logging
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // create user with password encoder
    public ResponseEntity<?> createUser(User user) {
        log.info("Creating user with username: {}", user.getUserName());

        if (userRepository.findByUserName(user.getUserName()) != null) {
            log.error("Username already exists: {}", user.getUserName());
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Password encoded for user: {}", user.getUserName());

        // Assigner le rôle "user" par défaut
        Role userRole = roleRepository.findByRoleName("user");
        if (userRole == null) {
            log.error("Default role 'user' not found.");
            return ResponseEntity.badRequest().body("Default role 'user' not found");
        }
        user.setRole(userRole);
        log.info("Assigned default role 'user' to user: {}", user.getUserName());

        userRepository.save(user);
        log.info("User {} successfully registered.", user.getUserName());

        return ResponseEntity.ok("User registered successfully");
    }

    // find all users
    public List<User> getAllUsers() {
        log.info("Fetching all users.");
        List<User> users = (List<User>) userRepository.findAll();
        log.info("Found {} users.", users.size());
        return users;
    }

    // find user by id
    public ResponseEntity<User> getUserById(int id) {
        log.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    log.info("User found with ID: {}", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    log.error("User not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // update name and email
    public ResponseEntity<?> updateUser(int id, User user) {
        log.info("Updating user with ID: {}", id);
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUserName(user.getUserName());
            existingUser.setEmail(user.getEmail());
            userRepository.save(existingUser);
            log.info("User with ID: {} updated successfully", id);
            return ResponseEntity.ok("User updated successfully");
        }).orElseGet(() -> {
            log.error("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // delete user
    public ResponseEntity<?> deleteUser(int id) {
        log.info("Deleting user with ID: {}", id);
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            log.info("User with ID: {} deleted successfully", id);
            return ResponseEntity.ok("User deleted successfully");
        }).orElseGet(() -> {
            log.error("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Update role
    public ResponseEntity<?> updateUserRole(int id, String role) {
        log.info("Updating role for user with ID: {}", id);
        return userRepository.findById(id).map(user -> {
            Role newRole = roleRepository.findByRoleName(role);
            if (newRole == null) {
                log.error("Role not found: {}", role);
                return ResponseEntity.badRequest().body("Role not found");
            }
            user.setRole(newRole);
            userRepository.save(user);
            log.info("Role for user with ID: {} updated to {}", id, role);
            return ResponseEntity.ok("User role updated successfully");
        }).orElseGet(() -> {
            log.error("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }
}
