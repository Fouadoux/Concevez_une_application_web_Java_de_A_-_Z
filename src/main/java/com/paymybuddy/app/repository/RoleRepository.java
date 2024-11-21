package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Méthode pour trouver un rôle par son nom
     Optional<Role> findByRoleName(String roleName);
}
