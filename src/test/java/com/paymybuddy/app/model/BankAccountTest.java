package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BankAccount class.
 */
class BankAccountTest {

    private BankAccount bankAccount;
    private User user;

    /**
     * Set up a sample BankAccount and User instance before each test.
     */
    @BeforeEach
    public void setUp() {
        // Create a sample user
        user = new User();
        user.setUserName("TestUser");

        // Create a sample bank account
        bankAccount = new BankAccount();
        bankAccount.setUser(user);
        bankAccount.setAmount(500.0f);
        bankAccount.setBankAccount("1234-5678-9101");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);
    }

    /**
     * Test to ensure that a BankAccount is created correctly with its attributes.
     */
    @Test
    public void testCreateBankAccount() {
        assertNotNull(bankAccount);
        assertEquals(500.0f, bankAccount.getAmount());
        assertEquals("1234-5678-9101", bankAccount.getBankAccount());
        assertNotNull(bankAccount.getTransferDate());
        assertTrue(bankAccount.isStatus());
        assertEquals(user, bankAccount.getUser());
    }

    /**
     * Test to ensure that the BankAccount's balance can be updated.
     */
    @Test
    public void testUpdateAmount() {
        bankAccount.setAmount(750.0f);
        assertEquals(750.0f, bankAccount.getAmount());
    }

    /**
     * Test to ensure that the BankAccount's status can be updated.
     */
    @Test
    public void testUpdateStatus() {
        bankAccount.setStatus(false);
        assertFalse(bankAccount.isStatus());
    }

    /**
     * Test to ensure that the BankAccount's transfer date can be updated.
     */
    @Test
    public void testUpdateTransferDate() {
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        bankAccount.setTransferDate(newDate);
        assertEquals(newDate, bankAccount.getTransferDate());
    }

    /**
     * Test to ensure that the BankAccount's bank account number can be updated.
     */
    @Test
    public void testUpdateBankAccountNumber() {
        bankAccount.setBankAccount("9876-5432-1098");
        assertEquals("9876-5432-1098", bankAccount.getBankAccount());
    }

    /**
     * Test to ensure that the BankAccount is correctly associated with an User.
     */
    @Test
    public void testUserAssociation() {
        assertEquals(user, bankAccount.getUser());
    }
}
