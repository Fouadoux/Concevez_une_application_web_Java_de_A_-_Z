package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InsufficientBalanceException;
import com.paymybuddy.app.repository.TransactionRepository;
import com.paymybuddy.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private MonetizationService monetizationService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionFeeService transactionFeeService;

    @Mock
    private AppAccountService appAccountService;

    @Mock
    private UserService userService;

    @Mock
    private UserRelationService userRelationService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_success() {
        // Arrange
        int senderId = 1;
        int receiverId = 2;
        long amount = 100;
        String description = "Test transaction";
        long amountCent = amount * 100;

        User sender = new User();
        sender.setId(senderId);

        User receiver = new User();
        receiver.setId(receiverId);

        when(userService.getUserById(senderId)).thenReturn(sender);
        when(userService.getUserById(receiverId)).thenReturn(receiver);
        when(userRelationService.checkRelation(senderId, receiverId)).thenReturn(true);
        when(appAccountService.getBalanceById(senderId)).thenReturn(Optional.of(20000L));
        when(transactionFeeService.calculateFeeForTransaction(amountCent)).thenReturn(500L);
        when(roleService.getTransactionLimitForUser(senderId)).thenReturn(50000L);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(monetizationService).saveMonetization(any(Transaction.class));

        // Act
        String result = transactionService.createTransaction(senderId, receiverId, amount, description);

        // Assert
        assertEquals("Transaction successful", result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(appAccountService, times(1)).updateBalanceByUserId(senderId, -(amountCent + 500L));
        verify(appAccountService, times(1)).updateBalanceByUserId(receiverId, amountCent);
    }

    @Test
    void createTransaction_noRelationExists() {
        // Arrange
        int senderId = 1;
        int receiverId = 2;
        long amount = 100;
        String description = "Test transaction";

        User sender = new User();
        sender.setId(senderId);

        User receiver = new User();
        receiver.setId(receiverId);

        when(userService.getUserById(senderId)).thenReturn(sender);
        when(userService.getUserById(receiverId)).thenReturn(receiver);
        when(userRelationService.checkRelation(senderId, receiverId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                transactionService.createTransaction(senderId, receiverId, amount, description));
    }

    @Test
    void createTransaction_insufficientBalance() {
        // Arrange
        int senderId = 1;
        int receiverId = 2;
        long amount = 100;
        String description = "Test transaction";
        long amountCent = amount * 100;

        User sender = new User();
        sender.setId(senderId);

        User receiver = new User();
        receiver.setId(receiverId);

        when(userService.getUserById(senderId)).thenReturn(sender);
        when(userService.getUserById(receiverId)).thenReturn(receiver);
        when(userRelationService.checkRelation(senderId, receiverId)).thenReturn(true);
        when(appAccountService.getBalanceById(senderId)).thenReturn(Optional.of(0L));
        when(transactionFeeService.calculateFeeForTransaction(amountCent)).thenReturn(1000L);
        when(roleService.getTransactionLimitForUser(senderId)).thenReturn(50000L);

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.createTransaction(senderId, receiverId, amount, description));
    }

    @Test
    void createTransaction_transactionLimitExceeded() {
        // Arrange
        int senderId = 1;
        int receiverId = 2;
        long amount = 100;
        String description = "Test transaction";
        long amountCent = amount * 100;

        User sender = new User();
        sender.setId(senderId);

        User receiver = new User();
        receiver.setId(receiverId);

        when(userService.getUserById(senderId)).thenReturn(sender);
        when(userService.getUserById(receiverId)).thenReturn(receiver);
        when(userRelationService.checkRelation(senderId, receiverId)).thenReturn(true);
        when(appAccountService.getBalanceById(senderId)).thenReturn(Optional.of(20000L));
        when(transactionFeeService.calculateFeeForTransaction(amountCent)).thenReturn(500L);
        when(roleService.getTransactionLimitForUser(senderId)).thenReturn(5000L);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                transactionService.createTransaction(senderId, receiverId, amount, description));
    }

    @Test
    void getTransactionHistory_ByUserId_success() {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);

        Transaction transaction1 = new Transaction();
        transaction1.setTransactionDate(LocalDateTime.now().minusDays(1));

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate(LocalDateTime.now());

        user.setSenderTransactions(List.of(transaction1));
        user.setReceiverTransactions(List.of(transaction2));

        when(userService.getUserById(userId)).thenReturn(user);

        // Act
        List<Transaction> result = transactionService.getTransactionHistoryByUserId(userId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(transaction2, result.get(0));
        assertEquals(transaction1, result.get(1));
    }

    @Test
    void cancelTransaction_success() {
        // Arrange
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setTransactionDate(LocalDateTime.now());

        User sender = new User();
        sender.setId(1);
        transaction.setUserSender(sender);

        User receiver = new User();
        receiver.setId(2);
        transaction.setUserReceiver(receiver);

        transaction.setAmount(1000L);
        transaction.setAmountWithFee(1100L);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);

        // Act
        String result = transactionService.cancelTransaction(transactionId);

        // Assert
        assertEquals("Transaction canceled successfully", result);
        verify(transactionRepository, times(1)).delete(transaction);
        verify(appAccountService, times(1)).updateBalanceByUserId(sender.getId(), transaction.getAmountWithFee());
        verify(appAccountService, times(1)).updateBalanceByUserId(receiver.getId(), -transaction.getAmount());
    }

    @Test
    void cancelTransaction_transactionNotFound() {
        // Arrange
        int transactionId = 1;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                transactionService.cancelTransaction(transactionId));
    }

    @Test
    void cancelTransaction_outsideCancellationWindow() {
        // Arrange
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setTransactionDate(LocalDateTime.now().minusDays(2));

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                transactionService.cancelTransaction(transactionId));
    }

    @Test
    void calculateTotalFees_success() {
        // Arrange
        Transaction transaction1 = new Transaction();
        transaction1.setAmount(1000L);
        transaction1.setAmountWithFee(1100L);

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(2000L);
        transaction2.setAmountWithFee(2200L);

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        // Act
        long result = transactionService.calculateTotalFees();

        // Assert
        assertEquals(300L, result);
    }

    @Test
    void checkTransactionLimit_success() {
        // Arrange
        int userId = 1;
        long dailyLimit = 50000L;
        long transactionAmount = 10000L;

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        when(roleService.getTransactionLimitForUser(userId)).thenReturn(dailyLimit);
        when(transactionRepository.calculateTotalSentByUserAndDateRange(any(User.class), eq(startOfDay), eq(endOfDay))).thenReturn(20000L);

        // Act
        boolean result = transactionService.checkTransactionLimit(userId, transactionAmount);

        // Assert
        assertTrue(result);
    }

    @Test
    void checkTransactionLimit_exceeded() {
        // Arrange
        int userId = 1;
        long dailyLimit = 5000L;
        long transactionAmount = 40000L;

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        when(roleService.getTransactionLimitForUser(userId)).thenReturn(dailyLimit);
        when(transactionRepository.calculateTotalSentByUserAndDateRange(any(User.class), eq(startOfDay), eq(endOfDay))).thenReturn(20000L);

        // Act
        boolean result = transactionService.checkTransactionLimit(userId, transactionAmount);

        // Assert
        assertFalse(result);
    }

    @Test
    void convertToDTO_success() {
        // Arrange
        Transaction transaction = new Transaction();
        User sender = new User();
        sender.setId(1);
        sender.setUserName("SenderName");

        User receiver = new User();
        receiver.setId(2);
        receiver.setUserName("ReceiverName");

        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(1000L);
        transaction.setAmountWithFee(1100L);
        transaction.setDescription("Test transaction");
        transaction.setTransactionDate(LocalDateTime.now());

        when(userService.findUsernameByUserId(sender.getId())).thenReturn("SenderName");
        when(userService.findUsernameByUserId(receiver.getId())).thenReturn("ReceiverName");

        // Act
        TransactionDTO dto = transactionService.convertToDTO(transaction);

        // Assert
        assertEquals(sender.getId(), dto.getSenderId());
        assertEquals("SenderName", dto.getSenderName());
        assertEquals(receiver.getId(), dto.getReceiverId());
        assertEquals("ReceiverName", dto.getReceiverName());
        assertEquals(transaction.getAmount(), dto.getAmount());
        assertEquals(transaction.getAmountWithFee(), dto.getAmountWithFee());
        assertEquals(transaction.getDescription(), dto.getDescription());
        assertEquals(transaction.getTransactionDate(), dto.getTransactionDate());
    }


}
