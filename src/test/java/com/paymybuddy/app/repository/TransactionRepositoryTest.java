package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User sender;
    private User receiver;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        Role role=new Role();
        role.setRoleName("USER");
        roleRepository.save(role);

        sender = new User();
        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setRole(role);
        userRepository.save(sender);

        receiver = new User();
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        receiver.setRole(role);
        userRepository.save(receiver);

        transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(100);
        transaction.setAmountWithFee(105);
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
        savedTransaction.setAmount(200);
        Transaction updatedTransaction = transactionRepository.save(savedTransaction);

        // Check that the amount is updated
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(updatedTransaction.getId());
        assertTrue(retrievedTransaction.isPresent());
        assertEquals(200, retrievedTransaction.get().getAmount());
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
