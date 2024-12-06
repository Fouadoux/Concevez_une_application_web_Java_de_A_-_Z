package com.paymybuddy.app.integration;

import com.paymybuddy.app.entity.Monetization;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.MonetizationRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.TransactionRepository;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.MonetizationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MonetizationIT {

    @Autowired
    private MonetizationService monetizationService;

    @Autowired
    private MonetizationRepository monetizationRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void shouldSaveTransactionFeeCorrectly() {

        Role role = new Role();
        role.setRoleName("USER");

        roleRepository.save(role);

        // Arrange
        User sender = new User();
        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setCreatedAt(LocalDateTime.now());
        sender.setRole(role);
        sender = userRepository.save(sender);

        User receiver = new User();
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        receiver.setCreatedAt(LocalDateTime.now());

        receiver.setRole(role);
        receiver = userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(10000);
        transaction.setAmountWithFee(10250);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction = transactionRepository.save(transaction);


        long feeAmount = 250;

        // Act
        monetizationService.saveMonetization(transaction);

        // Assert
        Optional<Monetization> result = monetizationRepository.findByTransactionId(transaction.getId());
        assertTrue(result.isPresent());
        assertEquals(feeAmount, result.get().getResult());
    }

    @Test
    public void testShouldReturnTotalResult() {

        // Arrange

        Role role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        roleRepository.flush();

        // Arrange
        User sender = new User();
        sender.setUserName("Sender");
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setCreatedAt(LocalDateTime.now());
        sender.setRole(role);
        sender = userRepository.save(sender);

        User receiver = new User();
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        receiver.setCreatedAt(LocalDateTime.now());
        receiver.setRole(role);
        receiver = userRepository.save(receiver);

        Transaction t1 = new Transaction();
        t1.setUserSender(sender);
        t1.setUserReceiver(receiver);
        t1.setAmount(10000);
        t1.setAmountWithFee(10250);
        t1.setTransactionDate(LocalDateTime.now());
        t1 = transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setUserSender(sender);
        t2.setUserReceiver(receiver);
        t2.setAmount(10000);
        t2.setAmountWithFee(10250);
        t2.setTransactionDate(LocalDateTime.now());
        t2 = transactionRepository.save(t2);


        Monetization m1 = new Monetization();
        m1.setResult(100);
        m1.setTransaction(t1);
        monetizationRepository.save(m1);

        Monetization m2 = new Monetization();
        m2.setResult(200);
        m2.setTransaction(t2);
        monetizationRepository.save(m2);

        // Act
        long total = monetizationService.getTotalResult();

        // Assert
        assertEquals(300, total);
    }
}