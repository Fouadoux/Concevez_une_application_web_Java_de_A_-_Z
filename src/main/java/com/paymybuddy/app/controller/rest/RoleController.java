package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Endpoint to create a new role.
     *
     * @param role The role information to create
     * @return The created role or an error message if the role already exists
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role newRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    /**
     * Endpoint to retrieve all existing roles.
     *
     * @return A list of roles
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roleList = roleService.getAllRoles();
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
        Role role = roleService.getRoleById(roleId);
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
        String updateMessage = roleService.updateRole(roleId, role);
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
        roleService.deleteRole(roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }

    @PutMapping("/dailyLimit/role/{roleName}/limit/{dailyLimit}")
    public ResponseEntity<String> updateDailyLimit(@PathVariable String roleName, @PathVariable long dailyLimit) {
        roleService.changeDailyLimit(roleName, dailyLimit);
        return ResponseEntity.ok("Daily limit updated successfully");
    }

}
