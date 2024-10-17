package com.paymybuddy.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private AppUser sender;
    private AppUser receiver;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        // Création d'un utilisateur "sender" et "receiver"
        sender = new AppUser();
        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password123");

        receiver = new AppUser();
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password456");

        // Création d'une transaction avec une date de transaction initiale
        transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(100.0f);
        transaction.setDescription("Test Transaction");
        transaction.setTransactionDate(LocalDateTime.now()); // Définir la date actuelle

    }

    @Test
    public void testCreateTransaction() {
        assertNotNull(transaction);
        assertEquals(sender, transaction.getUserSender());
        assertEquals(receiver, transaction.getUserReceiver());
        assertEquals(100.0f, transaction.getAmount());
        assertEquals("Test Transaction", transaction.getDescription());
        assertNotNull(transaction.getTransactionDate());
    }


    @Test
    public void testTransactionAmountUpdate() {
        // Mise à jour du montant de la transaction
        transaction.setAmount(150.0f);
        assertEquals(150.0f, transaction.getAmount());
    }
}
