package com.paymybuddy.app.controller;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
