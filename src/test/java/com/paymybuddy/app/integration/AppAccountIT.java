package com.paymybuddy.app.integration;


import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AppAccountIT {

    @Autowired
    private AppAccountRepository appAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    public void testCreationTimestamps() {
        User user = new User();
        user.setUserName("testUser");
        user.setEmail("test@example.fr");
        user.setPassword("password123");

        Role userRole = new Role();
        userRole.setRoleName("user");
        roleRepository.save(userRole);

        user.setRole(userRole);

        userRepository.save(user);

        AppAccount account = new AppAccount();
        account.setUser(user);
        account.setBalance(100);

        AppAccount savedAccount = appAccountRepository.save(account);

        // Vérifier que le champ createdAt est automatiquement rempli
        assertNotNull(savedAccount.getCreatedAt(), "The created_at timestamp should be generated automatically");
        assertNotNull(savedAccount.getLastUpdate(), "The last_update timestamp should be generated automatically");

        // Obtenir la valeur de lastUpdate après la création
        LocalDateTime initialLastUpdate = account.getLastUpdate();

        // Mettre à jour le compte
        savedAccount.setBalance(200);
        appAccountRepository.save(savedAccount);
        appAccountRepository.flush();

        // Recharger l'entité pour vérifier la mise à jour du timestamp
        AppAccount updatedAccount = appAccountRepository.findById(account.getId()).orElseThrow();

        // Vérifier que le champ lastUpdate a bien été mis à jour
        assertTrue(updatedAccount.getLastUpdate().isAfter(initialLastUpdate), "The last_update timestamp should be updated");

    }
}
