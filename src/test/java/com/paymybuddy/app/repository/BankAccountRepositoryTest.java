package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
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

    @Autowired
    private RoleRepository roleRepository;

    private BankAccount bankAccount;

    /**
     * Set up a sample BankAccount and User instance before each test.
     */
    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setRoleName("user");
        roleRepository.save(role);

        // Create a sample user
        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(role);

        // Save the user before creating the bank account
        user = userRepository.save(user);

        // Create a sample bank account
        bankAccount = new BankAccount();
        bankAccount.setTransferId(1);
        bankAccount.setUser(user);
        bankAccount.setAmount(BigDecimal.valueOf(500.0));
        bankAccount.setExternalBankAccountNumber("1234-5678-9101");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);
       // bankAccountRepository.save(bankAccount);
    }

    /**
     * Test to save a BankAccount entity.
     */
    @Test
    public void testSaveBankAccount() {
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        assertNotNull(savedAccount);
        assertEquals("1234-5678-9101", savedAccount.getExternalBankAccountNumber());
        assertEquals(BigDecimal.valueOf(500.0), savedAccount.getAmount());
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
        savedAccount.setAmount(BigDecimal.valueOf(750.0));

        BankAccount updatedAccount = bankAccountRepository.save(savedAccount);
        assertEquals(BigDecimal.valueOf(750.0), updatedAccount.getAmount());
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
        invalidAccount.setAmount(BigDecimal.valueOf(500.0));
        invalidAccount.setExternalBankAccountNumber("5678-9101-1121");
        invalidAccount.setTransferDate(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> bankAccountRepository.save(invalidAccount));
    }
}
