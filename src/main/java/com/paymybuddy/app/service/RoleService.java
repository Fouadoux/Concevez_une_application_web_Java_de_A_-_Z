package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.RoleAlreadyExistsException;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j  // Use SLF4J for logging
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;

    public RoleService(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    /**
     * Create a new role.
     *
     * @param role The role to create
     * @return The created role
     * @throws RoleAlreadyExistsException if the role already exists
     */
    public Role createRole(Role role) {
        if (roleRepository.findByRoleName(role.getRoleName()).isPresent()) {
            log.error("Role with name '{}' already exists.", role.getRoleName());
            throw new RoleAlreadyExistsException("Role already exists");
        }

        Role savedRole;
        try {
            savedRole = roleRepository.save(role);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save role", e);
        }

        log.info("Role with name '{}' created successfully.", savedRole.getRoleName());
        return savedRole;
    }

    /**
     * Retrieve all roles.
     *
     * @return A list of all roles
     */
    public List<Role> getAllRoles() {
        log.info("Fetching all roles.");
        List<Role> roleList = roleRepository.findAll();
        log.info("Found {} roles.", roleList.size());
        return roleList;
    }

    /**
     * Find a role by its ID.
     *
     * @param id The ID of the role
     * @return The role with the given ID
     * @throws EntityNotFoundException if the role is not found
     */
    public Role getRoleById(int id) {
        log.info("Fetching role by ID: {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new EntityNotFoundException("Role not found with ID: " + id);
                });
    }

    /**
     * Update an existing role.
     *
     * @param id The ID of the role to update
     * @param role The new role details
     * @return A message indicating successful update
     * @throws EntityNotFoundException if the role is not found
     */
    public String updateRole(int id, Role role) {
        log.info("Updating role with ID: {}", id);
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new EntityNotFoundException("Role not found with ID: " + id);
                });

        existingRole.setRoleName(role.getRoleName());
        roleRepository.save(existingRole);
        log.info("Role with ID: {} updated successfully", id);
        return "Role updated successfully";
    }

    /**
     * Delete a role by its ID.
     *
     * @param id The ID of the role to delete
     * @throws EntityNotFoundException if the role is not found
     */
    public void deleteRole(int id) {
        log.info("Deleting role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new EntityNotFoundException("Role not found with ID: " + id);
                });

        roleRepository.delete(role);
        log.info("Role with ID: {} deleted successfully", id);
    }

    @Transactional
    public void changeDailyLimit(String roleName, long dailyLimit) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name must not be null or blank.");
        }
        if (dailyLimit == 0) {
            throw new IllegalArgumentException("Daily limit must be a positive value.");
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        role.setDailyLimit(dailyLimit);

        try {
            roleRepository.save(role);
        } catch (Exception e) {
            throw new EntitySaveException("Error while updating the daily limit for role: " + roleName, e);
        }
    }

    public long getTransactionLimitForUser(int userId) {
        User user=userService.getUserById(userId);
        return user.getRole().getDailyLimit();
    }


}
