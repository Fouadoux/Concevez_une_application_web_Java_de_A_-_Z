package com.paymybuddy.app.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link AppAccount} entity.
 * This class contains unit tests to verify the creation, update, and relationship between
 * {@link AppAccount} and {@link AppUser}.
 */
class AppAccountTest {

    /**
     * Tests the creation of an {@link AppAccount} and verifies that all fields are set correctly.
     * Specifically checks that the account has been created, the balance is correctly set,
     * the associated {@link AppUser} is set, and the last update date is not null.
     */
    @Test
    public void testCreateAppAccount() {
        AppUser user = new AppUser();
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
     * Tests the relationship between {@link AppAccount} and {@link AppUser}.
     * Verifies that the user assigned to the account is correctly retrieved.
     */
    @Test
    public void testAppAccountUserRelation() {
        AppUser user = new AppUser();
        user.setUserName("userTest");

        AppAccount account = new AppAccount();
        account.setUser(user);

        assertEquals(user, account.getUser(), "The user associated with the account should be the one assigned");
    }

}
