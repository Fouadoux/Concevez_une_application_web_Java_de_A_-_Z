package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.RoleAlreadyExistsException;
import com.paymybuddy.app.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j  // Use SLF4J for logging
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Create a new role.
     *
     * @param role The role to create
     * @return The created role
     * @throws RoleAlreadyExistsException if the role already exists
     */
    public Role createRole(Role role) {
        if (roleRepository.findByRoleName(role.getRoleName()) != null) {
            log.error("Role with name '{}' already exists.", role.getRoleName());
            throw new RoleAlreadyExistsException("Role already exists");
        }

        Role savedRole;
        try{
            savedRole = roleRepository.save(role);
        }catch (Exception e){
            throw new EntitySaveException("Failed to save role",e);
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
}
