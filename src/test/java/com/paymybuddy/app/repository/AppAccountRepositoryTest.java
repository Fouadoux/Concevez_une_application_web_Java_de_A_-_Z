package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the AppAccountRepository.
 * This class contains unit tests for CRUD operations on the AppAccount entity.
 */
@DataJpaTest
public class AppAccountRepositoryTest {

    @Autowired
    private AppAccountRepository appAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private AppAccount appAccount;

    /**
     * Sets up a sample AppAccount instance before each test.
     */
    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setRoleName("user");
        roleRepository.save(role);

        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
        user.setRole(role);

        appAccount = new AppAccount();
        appAccount.setUser(user);
        appAccount.setBalance(200);
        appAccount.setLastUpdate(LocalDateTime.now());

        User savedUser = userRepository.save(appAccount.getUser());
        appAccount.setUser(savedUser);
    }

    /**
     * Tests saving an AppAccount to the repository.
     * Verifies that the account is successfully saved and its balance is correctly stored.
     */
    @Test
    public void testSaveAppAccount() {

        AppAccount savedAccount = appAccountRepository.save(appAccount);

        assertNotNull(savedAccount);
        assertEquals(200, savedAccount.getBalance());
        assertEquals("TestUser", savedAccount.getUser().getUserName());
    }

    /**
     * Tests finding an AppAccount by its ID.
     * Verifies that the account can be retrieved from the repository using its ID.
     */
    @Test
    public void testFindAppAccountById() {
        AppAccount savedAccount = appAccountRepository.save(appAccount);
        Optional<AppAccount> foundAccount = appAccountRepository.findById(savedAccount.getAccountId());

        assertTrue(foundAccount.isPresent());
        assertEquals(savedAccount.getAccountId(), foundAccount.get().getAccountId());
    }

    /**
     * Tests updating an existing AppAccount in the repository.
     * Verifies that the balance of the account is successfully updated.
     */
    @Test
    public void testUpdateAppAccount() {
        AppAccount savedAccount = appAccountRepository.save(appAccount);
        savedAccount.setBalance(500);
        AppAccount updatedAccount = appAccountRepository.save(savedAccount);

        assertEquals(500, updatedAccount.getBalance());
    }

    /**
     * Tests deleting an AppAccount from the repository.
     * Verifies that the account is successfully removed from the repository.
     */
    @Test
    public void testDeleteAppAccount() {
        AppAccount savedAccount = appAccountRepository.save(appAccount);
        appAccountRepository.deleteById(savedAccount.getAccountId());

        Optional<AppAccount> deletedAccount = appAccountRepository.findById(savedAccount.getAccountId());
        assertFalse(deletedAccount.isPresent());
    }
}
