package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BankAccountRepository.
 */
@DataJpaTest
public class BankAccountRepositoryTest {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserRepository userRepository;

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
        user.setEmail("test@example.com");
        user.setPassword("password123");

        // Save the user before creating the bank account
        user = userRepository.save(user);

        // Create a sample bank account
        bankAccount = new BankAccount();
        bankAccount.setUser(user);
        bankAccount.setAmount(500.0f);
        bankAccount.setBankAccount("1234-5678-9101");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);
    }

    /**
     * Test to save a BankAccount entity.
     */
    @Test
    public void testSaveBankAccount() {
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        assertNotNull(savedAccount);
        assertEquals("1234-5678-9101", savedAccount.getBankAccount());
        assertEquals(500.0f, savedAccount.getAmount());
    }

    /**
     * Test to find a BankAccount entity by ID.
     */
    @Test
    public void testFindBankAccountById() {
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        Optional<BankAccount> retrievedAccount = bankAccountRepository.findById(savedAccount.getTransferId());

        assertTrue(retrievedAccount.isPresent());
        assertEquals(savedAccount.getTransferId(), retrievedAccount.get().getTransferId());
    }

    /**
     * Test to update the amount in a BankAccount entity.
     */
    @Test
    public void testUpdateBankAccountAmount() {
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        savedAccount.setAmount(750.0f);

        BankAccount updatedAccount = bankAccountRepository.save(savedAccount);
        assertEquals(750.0f, updatedAccount.getAmount());
    }

    /**
     * Test to delete a BankAccount entity.
     */
    @Test
    public void testDeleteBankAccount() {
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        bankAccountRepository.delete(savedAccount);

        Optional<BankAccount> deletedAccount = bankAccountRepository.findById(savedAccount.getTransferId());
        assertFalse(deletedAccount.isPresent());
    }

    /**
     * Test to ensure saving a BankAccount without a user throws an exception.
     */
    @Test
    public void testSaveBankAccountWithoutUser() {
        BankAccount invalidAccount = new BankAccount();
        invalidAccount.setAmount(500.0f);
        invalidAccount.setBankAccount("5678-9101-1121");
        invalidAccount.setTransferDate(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> bankAccountRepository.save(invalidAccount));
    }
}
