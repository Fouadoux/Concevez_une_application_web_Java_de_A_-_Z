package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class UserTest {

    private User sender;
    private User receiver;
    private Transaction transaction;


    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setUserName("Sender");

        receiver = new User();
        receiver.setUserName("Receiver");

        transaction = new Transaction();
        transaction.setAmount(100);
        transaction.setDescription("Test Transaction");
    }


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

    @Test
    public void testAddSenderTransaction() {
        sender.addSenderTransactions(transaction);

        assertEquals(1, sender.getSenderTransactions().size(), "Sender should have 1 transaction");
        assertEquals(sender, transaction.getUserSender(), "Transaction's sender should match the sender");
    }

    @Test
    public void testAddReceiverTransaction() {
        receiver.addReceiverTransactions(transaction);

        assertEquals(1, receiver.getReceiverTransactions().size(), "Receiver should have 1 transaction");
        assertEquals(receiver, transaction.getUserReceiver(), "Transaction's receiver should match the receiver");
    }

    @Test
    public void testRemoveSenderTransaction() {
        sender.addSenderTransactions(transaction);
        sender.removeSenderTransactions(transaction);

        assertEquals(0, sender.getSenderTransactions().size(), "Sender should have 0 transactions");
        assertNull(transaction.getUserSender(), "Transaction's sender should be null after removal");
    }

    @Test
    public void testRemoveReceiverTransaction() {
        receiver.addReceiverTransactions(transaction);
        receiver.removeReceiverTransactions(transaction);

        assertEquals(0, receiver.getReceiverTransactions().size(), "Receiver should have 0 transactions");
        assertNull(transaction.getUserReceiver(), "Transaction's receiver should be null after removal");
    }
}
