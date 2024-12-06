package com.paymybuddy.app.integration;


import com.paymybuddy.app.entity.*;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InsufficientBalanceException;
import com.paymybuddy.app.repository.*;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserRelationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionServiceIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppAccountRepository appAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;

    @Autowired
    private TransactionFeeRepository transactionFeeRepository;

    private Role role;
    private User sender;
    private User receiver;
    private AppAccount senderApp;
    private AppAccount receiverApp;
    private UserRelation userRelation;
    private TransactionFee transactionFee;


    @BeforeEach
    void setUp() {

        role = new Role();
        sender = new User();
        receiver = new User();
        senderApp = new AppAccount();
        receiverApp = new AppAccount();

        role.setRoleName("USER");
        roleRepository.save(role);
        roleRepository.flush();


        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setCreatedAt(LocalDateTime.now());
        sender.setRole(role);
        sender = userRepository.save(sender);

        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        receiver.setCreatedAt(LocalDateTime.now());
        receiver.setRole(role);
        receiver = userRepository.save(receiver);

        senderApp.setBalance(200_00L);
        senderApp.setUser(sender);
        senderApp.setCreatedAt(LocalDateTime.now());
        senderApp.setDailyLimit(500_00L);
        appAccountRepository.save(senderApp);

        receiverApp.setBalance(50_00L);
        receiverApp.setUser(receiver);
        receiverApp.setCreatedAt(LocalDateTime.now());
        receiverApp.setDailyLimit(500_00L);
        appAccountRepository.save(receiverApp);

        userRelation = new UserRelation();
        userRelation.setUserId(sender.getId());
        userRelation.setUserRelationId(receiver.getId());
        userRelation.setUser(sender);
        userRelation.setRelatedUser(receiver);
        userRelation.setStatus(true);
        userRelation.setCreatedAt(LocalDateTime.now());
        userRelationRepository.save(userRelation);

        transactionFee = new TransactionFee();
        transactionFee.setEffectiveDate(LocalDateTime.now());
        transactionFee.setPercentage(5000);
        transactionFeeRepository.save(transactionFee);

    }

    @Test
    void testCreateTransaction_success() {

        //ACT
        String result = transactionService.createTransaction(sender.getId(), receiver.getId(), 50, "Payment for services");

        //Assert

        assertEquals("Transaction successful", result);

        List<Transaction> transactionList = transactionRepository.findAll();
        assertEquals(1, transactionList.size());
        Transaction transaction = transactionList.get(0);
        assertEquals(5000L, transaction.getAmount());
        assertTrue(transaction.getAmountWithFee() > transaction.getAmount());


    }

    @Test
    void testCreateTransaction_dailyLimitExceeded() {

        role.setRoleName("USER");
        roleRepository.save(role);
        roleRepository.flush();

        transactionService.createTransaction(sender.getId(), receiver.getId(), 150, "First payment");
        transactionService.createTransaction(sender.getId(), receiver.getId(), 10, "Second payment");

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.createTransaction(sender.getId(), receiver.getId(), 100, "Transaction limit exceeded for the day."));
    }

    @Test
    void testCancelTransaction_success() {

        senderApp.setBalance(200_00L);
        senderApp.setUser(sender);
        senderApp.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(senderApp);

        receiverApp.setBalance(100_00L);
        receiverApp.setUser(receiver);
        receiverApp.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(receiverApp);


        Transaction transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(50_00L);
        transaction.setAmountWithFee(51_00L);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Act
        String result = transactionService.cancelTransaction(transaction.getId());

        // Assert
        assertEquals("Transaction canceled successfully", result);

        AppAccount senderAccount = appAccountRepository.findByUserId(sender.getId()).orElseThrow();
        AppAccount receiverAccount = appAccountRepository.findByUserId(receiver.getId()).orElseThrow();
        assertEquals(25100, senderAccount.getBalance());
        assertEquals(50_00L, receiverAccount.getBalance());
    }

    @Test
    void testGetTransactionHistoryByUserId_success() {
        // Arrange
        User user = new User();
        user.setUserName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("password");
        user.setRole(role);
        userRepository.save(user);

        User otherUser = new User();
        otherUser.setUserName("Bob");
        otherUser.setEmail("bob@example.com");
        otherUser.setPassword("password");
        otherUser.setRole(role);
        userRepository.save(otherUser);

        AppAccount app1 = new AppAccount();
        app1.setBalance(200_00L);
        app1.setUser(user);
        app1.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(app1);

        AppAccount app2 = new AppAccount();

        app2.setBalance(100_00L);
        app2.setUser(otherUser);
        app2.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(app2);

        UserRelation userRelation2 = new UserRelation();
        userRelation2.setUserId(user.getId());
        userRelation2.setUserRelationId(otherUser.getId());
        userRelation2.setUser(user);
        userRelation2.setRelatedUser(otherUser);
        userRelation2.setStatus(true);
        userRelation2.setCreatedAt(LocalDateTime.now());
        userRelationRepository.save(userRelation2);

        Transaction transaction1 = new Transaction();
        transaction1.setUserSender(user);
        transaction1.setUserReceiver(otherUser);
        transaction1.setAmount(50_00L);
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction1);


        user.addSenderTransactions(transaction1);
        otherUser.addReceiverTransactions(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setUserSender(otherUser);
        transaction2.setUserReceiver(user);
        transaction2.setAmount(30_00L);
        transaction2.setTransactionDate(LocalDateTime.now().minusDays(1));
        transactionRepository.save(transaction2);


        user.addSenderTransactions(transaction2);
        otherUser.addReceiverTransactions(transaction2);

        // Act
        List<Transaction> transactions = transactionService.getTransactionHistoryByUserId(user.getId());

        // Assert
        assertEquals(2, transactions.size());
        assertEquals(50_00L, transactions.get(0).getAmount());
        assertEquals(30_00L, transactions.get(1).getAmount());
    }

    @Test
    void testCalculateTotalFees_success() {
        // Arrange
        User user = new User();
        user.setUserName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("password");
        user.setRole(role);
        userRepository.save(user);

        User otherUser = new User();
        otherUser.setUserName("Bob");
        otherUser.setEmail("bob@example.com");
        otherUser.setPassword("password");
        otherUser.setRole(role);
        userRepository.save(otherUser);

        Transaction transaction1 = new Transaction();
        transaction1.setUserSender(user);
        transaction1.setUserReceiver(otherUser);
        transaction1.setAmount(50_00L);
        transaction1.setAmountWithFee(51_00L);
        transaction1.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setUserSender(otherUser);
        transaction2.setUserReceiver(user);
        transaction2.setAmount(30_00L);
        transaction2.setAmountWithFee(31_00L);
        transaction2.setTransactionDate(LocalDateTime.now().minusDays(1));
        transactionRepository.save(transaction2);

        // Act
        long totalFees = transactionService.calculateTotalFees();

        // Assert
        assertEquals(200L, totalFees);
    }

    @Test
    void testCancelTransaction_afterOneDay() {

        senderApp.setBalance(200_00L);
        senderApp.setUser(sender);
        senderApp.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(senderApp);

        receiverApp.setBalance(100_00L);
        receiverApp.setUser(receiver);
        receiverApp.setCreatedAt(LocalDateTime.now());
        appAccountRepository.save(receiverApp);

        Transaction transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(50_00L);
        transaction.setAmountWithFee(51_00L);
        transaction.setTransactionDate(LocalDateTime.now().minusDays(2));
        transactionRepository.save(transaction);

        // Act&Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                        transactionService.cancelTransaction(transaction.getId())
                , "Transaction cannot be canceled after 24 hours.");

        assertEquals("Transaction cannot be canceled after 24 hours.", exception.getMessage());
    }

    @Test
    void testCreateTransactionWithInsufficientBalance() {
        senderApp.setBalance(40_00L);
        appAccountRepository.save(senderApp);

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                transactionService.createTransaction(sender.getId(), receiver.getId(), 50, "Insufficient balance")
        );

        assertEquals("Insufficient balance for user ID: " + sender.getId(), exception.getMessage());
    }

    @Test
    void testCreateTransactionWithoutRelation() {
        userRelationRepository.delete(userRelation);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(sender.getId(), receiver.getId(), 50, "No relation exists")
        );

        assertEquals("No relation exists between the sender and receiver.", exception.getMessage());
    }

}
