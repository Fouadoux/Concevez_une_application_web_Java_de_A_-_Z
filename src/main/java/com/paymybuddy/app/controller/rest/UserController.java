package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     *
     * @param user The user to be registered
     * @return A success message if the user is registered successfully
     */
    @PostMapping("/users/register")
    public String registerUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Get a list of all users
     *
     * @return A list of users
     */
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Get a user by ID
     *
     * @param id The ID of the user to retrieve
     * @return The user with the given ID
     */
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    /**
     * Update a user’s username and email
     *
     * @param id   The ID of the user to update
     * @param user The user data to update
     * @return A success message if the user is updated successfully
     */
    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable int id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    /**
     * Update a user's role
     *
     * @param id       The ID of the user whose role is being updated
     * @param roleName The new role name to assign to the user
     * @return A success message if the user's role is updated successfully
     */
    @PutMapping("/users/{id}/role/{roleName}")
    public String updateUserRole(@PathVariable int id, @PathVariable String roleName) {
        return userService.updateUserRole(id, roleName);
    }

    /**
     * Delete a user by ID
     *
     * @param id The ID of the user to delete
     * @return A success message if the user is deleted successfully
     */
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }


    //-----------------------------------------------------------
    @GetMapping("/currentUser")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Rechercher par email si l'identifiant de connexion est l'email
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = userService.getUserIdByEmail(userDetails.getUsername());
            response.put("id", userId);
            response.put("username", userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Utilisateur non trouvé"));
        }
    }

}
