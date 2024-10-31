package com.paymybuddy.app.service;

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

import java.util.List;

@Slf4j
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

    /**
     * Creates a new user with an encoded password and assigns a default "user" role.
     *
     * @param user The user to be created
     * @return A success message if the user is created successfully
     */
    public String createUser(User user) {
        log.info("Creating user with username: {}", user.getUserName());

        if (userRepository.findByUserName(user.getUserName()) != null) {
            log.error("Username already exists: {}", user.getUserName());
            throw new IllegalArgumentException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Password encoded for user: {}", user.getUserName());

        Role userRole = roleRepository.findByRoleName("user");
        if (userRole == null) {
            log.error("Default role 'user' not found.");
            throw new EntityNotFoundException("Default role 'user' not found");
        }
        user.setRole(userRole);
        log.info("Assigned default role 'user' to user: {}", user.getUserName());

        userRepository.save(user);
        log.info("User {} successfully registered.", user.getUserName());

        return "User registered successfully";
    }

    /**
     * Fetches all users in the system.
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        log.info("Fetching all users.");
        List<User> users = (List<User>) userRepository.findAll();
        log.info("Found {} users.", users.size());
        return users;
    }

    /**
     * Finds a user by ID.
     *
     * @param id The ID of the user to find
     * @return The found user
     * @throws EntityNotFoundException if the user is not found
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
     * Updates the username and email of an existing user.
     *
     * @param id   The ID of the user to update
     * @param user The user details to update
     * @return A success message if the user is updated
     * @throws EntityNotFoundException if the user is not found
     */
    public String updateUser(int id, User user) {
        log.info("Updating user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found with ID: " + id);
                });

        existingUser.setUserName(user.getUserName());
        existingUser.setEmail(user.getEmail());
        userRepository.save(existingUser);
        log.info("User with ID: {} updated successfully", id);

        return "User updated successfully";
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete
     * @return A success message if the user is deleted
     * @throws EntityNotFoundException if the user is not found
     */
    public String deleteUser(int id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found with ID: " + id);
                });


        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new EntityDeleteException("Failed to delete user with ID: " + user.getId(), e);
        }



        log.info("User with ID: {} deleted successfully", id);

        return "User deleted successfully";
    }

    /**
     * Updates the role of a user.
     *
     * @param id   The ID of the user whose role is being updated
     * @param role The new role name to assign
     * @return A success message if the role is updated
     * @throws EntityNotFoundException if the user or role is not found
     */
    public String updateUserRole(int id, String role) {
        log.info("Updating role for user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found with ID: " + id);
                });

        Role newRole = roleRepository.findByRoleName(role);
        if (newRole == null) {
            log.error("Role not found: {}", role);
            throw new EntityNotFoundException("Role not found");
        }

        user.setRole(newRole);

        try{
            userRepository.save(user);
        }catch (Exception e){
            throw new EntitySaveException("Failed to save update user with ID: "+user.getId(),e);
        }
        log.info("Role for user with ID: {} updated to {}", id, role);

        return "User role updated successfully";
    }

    /**
     * Récupère un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur à rechercher
     * @return L'utilisateur trouvé
     * @throws EntityNotFoundException si aucun utilisateur n'est trouvé avec cette adresse e-mail
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
    }
}
