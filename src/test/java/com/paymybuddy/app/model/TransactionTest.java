package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private User sender;
    private User receiver;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password123");

        receiver = new User();
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password456");

        transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(100);
        transaction.setDescription("Test Transaction");
        transaction.setTransactionDate(LocalDateTime.now());

    }

    @Test
    public void testCreateTransaction() {
        assertNotNull(transaction);
        assertEquals(sender, transaction.getUserSender());
        assertEquals(receiver, transaction.getUserReceiver());
        assertEquals(100, transaction.getAmount());
        assertEquals("Test Transaction", transaction.getDescription());
        assertNotNull(transaction.getTransactionDate());
    }


    @Test
    public void testTransactionAmountUpdate() {
        transaction.setAmount(150);
        assertEquals(150, transaction.getAmount());
    }
}
