package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.RoleAlreadyExistsException;
import com.paymybuddy.app.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing roles and related operations.
 */
@Slf4j
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Creates a new role.
     *
     * @param role the role to create.
     * @return the created role.
     * @throws RoleAlreadyExistsException if a role with the same name already exists.
     * @throws EntitySaveException if saving the role fails.
     */
    public Role createRole(Role role) {
        log.info("Creating a new role with name: {}", role.getRoleName());

        if (roleRepository.findByRoleName(role.getRoleName()).isPresent()) {
            log.error("Role creation failed. Role with name '{}' already exists.", role.getRoleName());
            throw new RoleAlreadyExistsException("Role already exists");
        }

        try {
            Role savedRole = roleRepository.save(role);
            log.info("Role with name '{}' created successfully.", savedRole.getRoleName());
            return savedRole;
        } catch (Exception e) {
            log.error("Failed to save role with name: {}", role.getRoleName(), e);
            throw new EntitySaveException("Failed to save role", e);
        }
    }

    /**
     * Retrieves all roles.
     *
     * @return a list of all roles.
     */
    public List<Role> getAllRoles() {
        log.info("Fetching all roles.");
        List<Role> roles = roleRepository.findAll();
        log.info("Found {} roles.", roles.size());
        return roles;
    }

    /**
     * Finds a role by its ID.
     *
     * @param id the ID of the role.
     * @return the role with the given ID.
     * @throws EntityNotFoundException if no role is found with the given ID.
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
     * Updates an existing role.
     *
     * @param id   the ID of the role to update.
     * @param role the new role details.
     * @return a message indicating the successful update.
     * @throws EntityNotFoundException if no role is found with the given ID.
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
        log.info("Role with ID: {} updated successfully.", id);
        return "Role updated successfully";
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the role to delete.
     * @throws EntityNotFoundException if no role is found with the given ID.
     */
    public void deleteRole(int id) {
        log.info("Deleting role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new EntityNotFoundException("Role not found with ID: " + id);
                });

        roleRepository.delete(role);
        log.info("Role with ID: {} deleted successfully.", id);
    }


}
