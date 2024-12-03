package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.UpdateUserRequestDTO;
import com.paymybuddy.app.dto.UserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user-related operations.
 * Provides endpoints for updating user information, retrieving users by ID or role, and managing user roles.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint to update user information.
     * This method allows a user to update their personal details.
     *
     * @param userId  The ID of the user
     * @param request The new information to update
     * @return A response confirming the successful update
     */
    @PutMapping("/update/{userId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<String> updateUser(
            @PathVariable int userId,
            @RequestBody UpdateUserRequestDTO request
    ) {
        log.info("Updating user with ID: {} with new information: {}", userId, request);
        userService.updateUser(userId, request);
        log.info("User with ID: {} updated successfully", userId);
        return ResponseEntity.ok("Vos informations ont été mises à jour avec succès !");
    }

    /**
     * Endpoint to retrieve a list of all users.
     * This method is secured to allow access only for users with "ROLE_ADMIN".
     *
     * @return A list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOS = userService.convertToDTOList(users);
        log.info("Fetched {} users", userDTOS.size());
        return ResponseEntity.ok(userDTOS);
    }

    /**
     * Endpoint to retrieve a user by their ID.
     * This method is secured to allow access only for users with "ROLE_ADMIN".
     *
     * @param id The ID of the user
     * @return The user corresponding to the ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        log.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        UserDTO dto = userService.convertToDTO(user);
        log.info("Fetched user with ID: {}: {}", id, dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * Endpoint to update a user's role.
     * This method is secured to allow access only for users with "ROLE_ADMIN".
     *
     * @param id       The ID of the user
     * @param roleName The new role to assign
     * @return A response confirming the successful update
     */
    @PutMapping("/{id}/role/{roleName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateUserRole(
            @PathVariable int id,
            @PathVariable String roleName
    ) {
        log.info("Updating role for user with ID: {} to new role: {}", id, roleName);
        String response = userService.updateUserRole(id, roleName);
        log.info("User role updated for user ID: {}: {}", id, roleName);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to delete a user by their ID.
     * This method is secured to allow access only for users with "ROLE_ADMIN".
     *
     * @param id The ID of the user to delete
     * @return A response confirming the successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        log.info("Deleting user with ID: {}", id);
        String response = userService.deleteUser(id);
        log.info("User with ID: {} deleted successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve users by their role.
     *
     * @param role The role of the users to retrieve
     * @return A list of users with the specified role or a 404 (NOT FOUND) if no users are found
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getFindByRole(@PathVariable String role) {
        log.info("Fetching users with role: {}", role);
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOS = userService.getFindByRole(users, role);

        if (userDTOS.isEmpty()) {
            log.warn("No users found with role: {}", role);
            return ResponseEntity.status(404)
                    .body("No users found with role: " + role);
        }

        log.info("Found {} users with role: {}", userDTOS.size(), role);
        return ResponseEntity.ok(userDTOS);
    }

    //----------------------
    @PostMapping("/test")
    public void getTestRollback(){
        userService.performTransactionalOperation();

    }

}
