package com.paymybuddy.app.controller;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //créer un nouvelle utilisateur
    public ResponseEntity<?> registerUser(@RequestBody User user){
        return userService.createUser(user);
    }

    //Obtenir la liste des utilisateur
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Obtenir un utilisateur par ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // Mettre à jour un utilisateur (userName et email)
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    //Mettre à jour le role
    @PutMapping("/users/{id}/role/{roleName}")
    public ResponseEntity<?> updateUserRole(@PathVariable int id,@PathVariable String roleName){
        return userService.updateUserRole(id, roleName);
    }


    // Supprimer un utilisateur
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

}
