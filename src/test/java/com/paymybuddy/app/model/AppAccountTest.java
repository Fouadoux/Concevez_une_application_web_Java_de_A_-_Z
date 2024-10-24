package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import jakarta.validation.*;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.opt.Const;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link AppAccount} entity.
 * This class contains unit tests to verify the creation, update, and relationship between
 * {@link AppAccount} and {@link User}.
 */
class AppAccountTest {

    /**
     * Tests the creation of an {@link AppAccount} and verifies that all fields are set correctly.
     * Specifically checks that the account has been created, the balance is correctly set,
     * the associated {@link User} is set, and the last update date is not null.
     */
    @Test
    public void testCreateAppAccount() {
        User user = new User();
        user.setUserName("testUser");

        AppAccount account = new AppAccount();
        account.setUser(user);
        account.setBalance(100.0f);
        account.setLastUpdate(LocalDateTime.now());

        assertNotNull(account, "Account should not be null");
        assertEquals(100.0f, account.getBalance(), "Balance should be 100.0");
        assertEquals(user, account.getUser(), "User should match the one assigned to the account");
        assertNotNull(account.getLastUpdate(), "Last update should not be null");
    }

    /**
     * Tests the balance update functionality of {@link AppAccount}.
     * Verifies that the balance is updated correctly.
     */
    @Test
    public void testUpdateBalance() {
        AppAccount account = new AppAccount();
        account.setBalance(100.0f);

        account.setBalance(150.0f);  // Update the balance to 150.0

        assertEquals(150.0f, account.getBalance(), "Balance should be updated to 150.0");
    }

    /**
     * Tests the update of the last update timestamp in {@link AppAccount}.
     * Verifies that the last update date is set correctly.
     */
    @Test
    public void testUpdateLastUpdate() {
        AppAccount account = new AppAccount();
        LocalDateTime now = LocalDateTime.now();

        account.setLastUpdate(now);

        assertEquals(now, account.getLastUpdate(), "Last update should match the provided value");
    }

    /**
     * Tests the relationship between {@link AppAccount} and {@link User}.
     * Verifies that the user assigned to the account is correctly retrieved.
     */
    @Test
    public void testAppAccountUserRelation() {
        User user = new User();
        user.setUserName("userTest");

        AppAccount account = new AppAccount();
        account.setUser(user);

        assertEquals(user, account.getUser(), "The user associated with the account should be the one assigned");
    }

    @Test
    void testBalanceValidation(){

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        AppAccount account = new AppAccount();
        account.setBalance(-5f);


        Set<ConstraintViolation<AppAccount>> violations = validator.validate(account);

        assertFalse(violations.isEmpty(), "Validation should fail for negative balance");
    }
}
