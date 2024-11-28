package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.UpdateUserRequestDTO;
import com.paymybuddy.app.dto.UserDTO;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user operations including creation, retrieval, update, and deletion.
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppAccountService appAccountService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AppAccountService appAccountService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.appAccountService = appAccountService;
    }

    /**
     * Creates a new user with an encoded password and assigns a default "USER" role.
     *
     * @param user The user to be created.
     * @return The created user.
     */
    @Transactional
    public User createUser(User user) {
        log.info("Creating user with username: {}", user.getUserName());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Password encoded for user: {}", user.getUserName());

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Default role 'USER' not found"));

        user.setRole(userRole);

        try {
            User newUser = userRepository.save(user);
            log.info("User '{}' successfully registered.", user.getUserName());
            return newUser;
        } catch (Exception e) {
            log.error("Failed to save user: {}", user.getUserName(), e);
            throw new EntitySaveException("Failed to create user.", e);
        }
    }

    /**
     * Retrieves all users in the system.
     *
     * @return A list of all users.
     */
    public List<User> getAllUsers() {
        log.info("Fetching all users.");
        List<User> users = userRepository.findAll();
        log.info("Found {} users.", users.size());
        return users;
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to find.
     * @return The found user.
     * @throws EntityNotFoundException if the user is not found.
     */
    public User getUserById(int id) {
        log.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found with ID: " + id);
                });
    }

    /**
     * Updates a user's details including username, email, and password.
     *
     * @param userId  The ID of the user to update.
     * @param request The request containing updated details.
     */
    public void updateUser(int userId, UpdateUserRequestDTO request) {
        log.info("Updating user with ID: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (request.getUserName() != null && !request.getUserName().isBlank()) {
            existingUser.setUserName(request.getUserName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!EmailValidationService.isValidEmail(request.getEmail())) {
                log.error("Invalid email format: {}", request.getEmail());
                throw new IllegalArgumentException("Invalid email format: " + request.getEmail());
            }
            existingUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        try {
            userRepository.save(existingUser);
            log.info("User with ID: {} updated successfully.", userId);
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userId, e);
            throw new EntitySaveException("Failed to update user with ID: " + userId, e);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return A success message if the user is deleted.
     */
    @Transactional
    public String deleteUser(int id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found with ID: " + id);
                });

        try {
            userRepository.delete(user);
            log.info("User with ID: {} deleted successfully.", id);
            return "User deleted successfully.";
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", id, e);
            throw new EntityDeleteException("Failed to delete user with ID: " + id, e);
        }
    }

    /**
     * Updates the role of a user.
     *
     * @param id   The ID of the user whose role is being updated.
     * @param role The new role name to assign.
     * @return A success message if the role is updated.
     */
    public String updateUserRole(int id, String role) {
        log.info("Updating role for user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        Role newRole = roleRepository.findByRoleName(role)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + role));

        user.setRole(newRole);

        try {
            userRepository.save(user);
            log.info("Role for user with ID: {} updated to '{}'.", id, role);
            return "User role updated successfully.";
        } catch (Exception e) {
            log.error("Failed to update role for user with ID: {}", id, e);
            throw new EntitySaveException("Failed to update role for user with ID: " + id, e);
        }
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user to retrieve.
     * @return The found user.
     */
    public User getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        if (!EmailValidationService.isValidEmail(email)) {
            log.error("Invalid email format: {}", email);
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
    }

    /**
     * Checks if a user exists by their email.
     *
     * @param email The email to check.
     * @return True if the user exists, otherwise false.
     */
    public boolean existsByEmail(String email) {
        log.info("Checking existence of user by email: {}", email);

        if (!EmailValidationService.isValidEmail(email)) {
            log.error("Invalid email format: {}", email);
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Registers a new user and creates an associated account.
     *
     * @param user The user to register.
     */
    @Transactional
    public void registerAndCreateAccount(User user) {
        log.info("Registering user and creating account for username: {}", user.getUserName());
        User savedUser = createUser(user);
        appAccountService.createAccountForUser(savedUser.getId());
        log.info("Account created successfully for user ID: {}", savedUser.getId());
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity to convert.
     * @return The converted UserDTO.
     */
    public UserDTO convertToDTO(User user) {
        log.info("Converting user '{}' to DTO.", user.getUserName());

        UserDTO dto = new UserDTO();
        dto.setName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().getRoleName());
        dto.setCreatedAt(user.getCreatedAt());

        log.info("User '{}' converted to DTO successfully.", user.getUserName());
        return dto;
    }

    /**
     * Converts a list of User entities to a list of UserDTOs.
     *
     * @param users The list of User entities.
     * @return The list of UserDTOs.
     */
    public List<UserDTO> convertToDTOList(List<User> users) {
        log.info("Converting list of users to DTOs.");
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters users by role and converts the results to UserDTOs.
     *
     * @param users The list of users to filter.
     * @param role  The role to filter by.
     * @return The list of UserDTOs matching the role.
     */
    public List<UserDTO> getFindByRole(List<User> users, String role) {
        log.info("Filtering users by role: {}", role);
        return users.stream()
                .filter(user -> user.getRole().getRoleName().equalsIgnoreCase(role))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    /**
     * Finds the username of a user by their ID.
     *
     * @param userId The ID of the user.
     * @return The username of the user.
     * @throws EntityNotFoundException if the user is not found.
     */

    public String findUsernameByUserId(Integer userId) {
        log.info("Fetching username for user ID: {}", userId);
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });
        return user.getUserName();
    }

}
