package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing roles within the system.
 * Provides endpoints for creating, retrieving, updating, and deleting roles.
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    /**
     * Constructs an instance of RoleController.
     *
     * @param roleService Service to manage Role operations
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Endpoint to create a new role.
     * This method creates a role with the provided information.
     *
     * @param role The role information to create
     * @return The created role with a 201 (CREATED) status
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        log.info("Creating new role with name: {}", role.getRoleName());
        Role newRole = roleService.createRole(role);
        log.info("Role created successfully: {}", newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    /**
     * Endpoint to retrieve all existing roles.
     *
     * @return A list of roles with a 200 (OK) status
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("Fetching all roles");
        List<Role> roleList = roleService.getAllRoles();
        log.info("Fetched {} roles", roleList.size());
        return ResponseEntity.ok(roleList);
    }

    /**
     * Endpoint to retrieve a role by its ID.
     *
     * @param roleId The ID of the role to retrieve
     * @return The corresponding role or an error response if not found
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable int roleId) {
        log.info("Fetching role with ID: {}", roleId);
        Role role = roleService.getRoleById(roleId);
        log.info("Fetched role with ID {}: {}", roleId, role);
        return ResponseEntity.ok(role);
    }

    /**
     * Endpoint to update an existing role.
     *
     * @param roleId The ID of the role to update
     * @param role The new role information
     * @return A response indicating success or an error if the role does not exist
     */
    @PutMapping("/{roleId}")
    public ResponseEntity<String> updateRole(@PathVariable int roleId, @RequestBody Role role) {
        log.info("Updating role with ID: {}. New role name: {}", roleId, role.getRoleName());
        String updateMessage = roleService.updateRole(roleId, role);
        log.info("Role updated successfully with ID: {}", roleId);
        return ResponseEntity.ok(updateMessage);
    }

    /**
     * Endpoint to delete a role by its ID.
     *
     * @param roleId The ID of the role to delete
     * @return A response indicating success or an error if the role does not exist
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable int roleId) {
        log.info("Deleting role with ID: {}", roleId);
        roleService.deleteRole(roleId);
        log.info("Role deleted successfully with ID: {}", roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }

    /**
     * Endpoint to update the daily limit for a specific role.
     *
     * @param roleName The name of the role
     * @param dailyLimit The new daily limit to set for the role
     * @return A response indicating success
     */
    @PutMapping("/dailyLimit/role/{roleName}/limit/{dailyLimit}")
    public ResponseEntity<String> updateDailyLimit(@PathVariable String roleName, @PathVariable long dailyLimit) {
        log.info("Updating daily limit for role {}: new limit {}", roleName, dailyLimit);
        roleService.changeDailyLimit(roleName, dailyLimit);
        log.info("Daily limit for role {} updated successfully to {}", roleName, dailyLimit);
        return ResponseEntity.ok("Daily limit updated successfully");
    }
}
