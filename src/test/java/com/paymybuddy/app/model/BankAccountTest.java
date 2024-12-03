package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class BankAccountTest {

    private BankAccount bankAccount;
    private User user;

    @BeforeEach
    public void setUp() {
        // Create a sample user
        user = new User();
        user.setUserName("TestUser");

        // Create a sample bank account
        bankAccount = new BankAccount();
        bankAccount.setUser(user);
        bankAccount.setAmount(500);
        bankAccount.setExternalBankAccountNumber("1234-5678-9101");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);
    }

    @Test
    public void testCreateBankAccount() {
        assertNotNull(bankAccount);
        assertEquals(500, bankAccount.getAmount());
        assertEquals("1234-5678-9101", bankAccount.getExternalBankAccountNumber());
        assertNotNull(bankAccount.getTransferDate());
        assertTrue(bankAccount.isStatus());
        assertEquals(user, bankAccount.getUser());
    }

    @Test
    public void testUpdateAmount() {
        bankAccount.setAmount(750);
        assertEquals(750, bankAccount.getAmount());
    }

    @Test
    public void testUpdateStatus() {
        bankAccount.setStatus(false);
        assertFalse(bankAccount.isStatus());
    }

    @Test
    public void testUpdateTransferDate() {
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        bankAccount.setTransferDate(newDate);
        assertEquals(newDate, bankAccount.getTransferDate());
    }

    @Test
    public void testUpdateBankAccountNumber() {
        bankAccount.setExternalBankAccountNumber("9876-5432-1098");
        assertEquals("9876-5432-1098", bankAccount.getExternalBankAccountNumber());
    }

    @Test
    public void testUserAssociation() {
        assertEquals(user, bankAccount.getUser());
    }
}
