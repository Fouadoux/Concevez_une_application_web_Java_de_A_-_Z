package com.paymybuddy.app.repository;

import com.paymybuddy.app.model.AppUser;
import com.paymybuddy.app.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser sender;
    private AppUser receiver;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        sender = new AppUser();
        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        appUserRepository.save(sender);

        receiver = new AppUser();
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        appUserRepository.save(receiver);

        transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(100.0f);
        transaction.setDescription("Initial Transaction");
        transaction.setTransactionDate(LocalDateTime.now());
    }

    @Test
    public void testSaveTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);

        assertNotNull(savedTransaction);
        assertEquals(transaction.getAmount(), savedTransaction.getAmount());
        assertEquals(transaction.getUserSender(), savedTransaction.getUserSender());
        assertEquals(transaction.getUserReceiver(), savedTransaction.getUserReceiver());
        assertNotNull(savedTransaction.getTransactionDate());
    }

    @Test
    public void testUpdateTransactionAmount() {
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update the amount
        savedTransaction.setAmount(200.0f);
        Transaction updatedTransaction = transactionRepository.save(savedTransaction);

        // Check that the amount is updated
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(updatedTransaction.getId());
        assertTrue(retrievedTransaction.isPresent());
        assertEquals(200.0f, retrievedTransaction.get().getAmount());
    }

    @Test
    public void testTransactionDateNotUpdatable() {

        Transaction saveTansac= transactionRepository.save(transaction);

        // Tente de mettre à jour la date
        saveTansac.setTransactionDate(LocalDateTime.now().plusDays(1));
        Transaction newSave =  transactionRepository.save(saveTansac);

        // Recharger la transaction depuis la base de données
        Transaction savedTransaction = transactionRepository.findById(newSave.getId()).orElseThrow();

        // Vérifier que la date n'a pas été mise à jour dans la base de données
        assertEquals(saveTansac.getTransactionDate().withNano(0), savedTransaction.getTransactionDate().withNano(0)); // comparaison sans les nanosecondes
    }
}
