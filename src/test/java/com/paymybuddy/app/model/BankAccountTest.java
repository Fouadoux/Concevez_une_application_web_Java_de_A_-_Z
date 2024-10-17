package com.paymybuddy.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BankAccount class.
 */
class BankAccountTest {

    private BankAccount bankAccount;
    private AppUser appUser;

    /**
     * Set up a sample BankAccount and AppUser instance before each test.
     */
    @BeforeEach
    public void setUp() {
        // Create a sample user
        appUser = new AppUser();
        appUser.setUserName("TestUser");

        // Create a sample bank account
        bankAccount = new BankAccount();
        bankAccount.setUser(appUser);
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
        assertEquals(appUser, bankAccount.getUser());
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
     * Test to ensure that the BankAccount is correctly associated with an AppUser.
     */
    @Test
    public void testUserAssociation() {
        assertEquals(appUser, bankAccount.getUser());
    }
}
