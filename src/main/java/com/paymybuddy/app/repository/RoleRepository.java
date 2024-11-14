package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Méthode pour trouver un rôle par son nom
     Role findByRoleName(String roleName);
}
