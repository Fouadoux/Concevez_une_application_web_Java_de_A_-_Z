package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.UpdateUserRequest;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



 @PutMapping("/users/update/{userId}")
 @PreAuthorize("#userId == principal.id")
    public ResponseEntity<String> updateUser(@PathVariable int userId,
            @RequestBody UpdateUserRequest request) {
        userService.updateUser(userId, request);
        return ResponseEntity.ok("Vos informations ont été mises à jour avec succès !");
    }

    /**
     * Register a new user
     *
     * @param //user The user to be registered
     * @return A success message if the user is registered successfully
     */
    @PostMapping("/users/register")
  /*  public void registerUser(@RequestBody User user) {
        userService.createUser(user);
    }*/

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
