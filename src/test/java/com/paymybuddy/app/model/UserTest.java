package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link User} entity.
 * This class contains unit tests to verify the creation, addition, and removal of transactions
 * for {@link User}.
 */
class UserTest {

    private User sender;
    private User receiver;
    private Transaction transaction;

    /**
     * Sets up the test environment before each test.
     * Initializes two {@link User} objects (sender and receiver) and one {@link Transaction} object.
     */
    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setUserName("Sender");

        receiver = new User();
        receiver.setUserName("Receiver");

        transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDescription("Test Transaction");
    }

    /**
     * Tests the creation of an {@link User} and verifies that the fields are set correctly.
     * Specifically checks the user name, email, and password.
     */
    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUserName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        assertEquals("Test User", user.getUserName(), "User name should be 'Test User'");
        assertEquals("test@example.com", user.getEmail(), "Email should be 'test@example.com'");
        assertEquals("password123", user.getPassword(), "Password should be 'password123'");
    }

    /**
     * Tests the addition of a sender transaction to the {@link User}.
     * Verifies that the transaction is added to the sender's transaction list and that the transaction's sender is set correctly.
     */
    @Test
    public void testAddSenderTransaction() {
        sender.addSenderTransactions(transaction);

        assertEquals(1, sender.getSenderTransactions().size(), "Sender should have 1 transaction");
        assertEquals(sender, transaction.getUserSender(), "Transaction's sender should match the sender");
    }

    /**
     * Tests the addition of a receiver transaction to the {@link User}.
     * Verifies that the transaction is added to the receiver's transaction list and that the transaction's receiver is set correctly.
     */
    @Test
    public void testAddReceiverTransaction() {
        receiver.addReceiverTransactions(transaction);

        assertEquals(1, receiver.getReceiverTransactions().size(), "Receiver should have 1 transaction");
        assertEquals(receiver, transaction.getUserReceiver(), "Transaction's receiver should match the receiver");
    }

    /**
     * Tests the removal of a sender transaction from the {@link User}.
     * Verifies that the transaction is removed from the sender's transaction list and that the transaction's sender is set to null.
     */
    @Test
    public void testRemoveSenderTransaction() {
        sender.addSenderTransactions(transaction);
        sender.removeSenderTransactions(transaction);

        assertEquals(0, sender.getSenderTransactions().size(), "Sender should have 0 transactions");
        assertNull(transaction.getUserSender(), "Transaction's sender should be null after removal");
    }

    /**
     * Tests the removal of a receiver transaction from the {@link User}.
     * Verifies that the transaction is removed from the receiver's transaction list and that the transaction's receiver is set to null.
     */
    @Test
    public void testRemoveReceiverTransaction() {
        receiver.addReceiverTransactions(transaction);
        receiver.removeReceiverTransactions(transaction);

        assertEquals(0, receiver.getReceiverTransactions().size(), "Receiver should have 0 transactions");
        assertNull(transaction.getUserReceiver(), "Transaction's receiver should be null after removal");
    }
}
