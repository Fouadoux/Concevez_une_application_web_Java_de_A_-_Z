package com.paymybuddy.app.controller;


import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Endpoint pour créer un nouveau rôle.
     *
     * @param role Les informations du rôle à créer
     * @return Le rôle créé ou un message d'erreur si le rôle existe déjà
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    /**
     * Endpoint pour récupérer tous les rôles existants.
     *
     * @return La liste des rôles
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRole();
    }

    /**
     * Endpoint pour récupérer un rôle par son ID.
     *
     * @param roleId L'ID du rôle à récupérer
     * @return Le rôle correspondant ou une réponse d'erreur si non trouvé
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable int roleId) {
        return roleService.getRoleById(roleId);
    }

    /**
     * Endpoint pour mettre à jour un rôle existant.
     *
     * @param roleId L'ID du rôle à mettre à jour
     * @param role Les nouvelles informations du rôle
     * @return Une réponse indiquant le succès ou une erreur si le rôle n'existe pas
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable int roleId, @RequestBody Role role) {
        return roleService.UpdateRole(roleId, role);
    }

    /**
     * Endpoint pour supprimer un rôle par son ID.
     *
     * @param roleId L'ID du rôle à supprimer
     * @return Une réponse indiquant le succès ou une erreur si le rôle n'existe pas
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable int roleId) {
        return roleService.deleteRole(roleId);
    }
}

