package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.InsufficientBalanceException;
import com.paymybuddy.app.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionFeeService transactionFeeService;

    @Mock
    private AppAccountService appAccountService;

    @Mock
    private UserRelationService userRelationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sender = new User();
        sender.setId(1);
        sender.setUserName("SenderUser");

        receiver = new User();
        receiver.setId(2);
        receiver.setUserName("ReceiverUser");

        transaction = new Transaction();
        transaction.setId(1);
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setAmountWithFee(BigDecimal.valueOf(105));
        transaction.setTransactionDate(LocalDateTime.now());
    }

    @Test
    void createTransaction_Success() {
        BigDecimal feeAmount = BigDecimal.valueOf(5);
        BigDecimal senderBalance = BigDecimal.valueOf(200);

        when(userRelationService.checkRelation(sender.getId(),receiver.getId())).thenReturn(true);
        when(transactionFeeService.calculateFeeForTransaction(transaction.getAmount())).thenReturn(feeAmount);
        when(appAccountService.getBalanceByIdInBigDecimal(sender.getId())).thenReturn(Optional.of(senderBalance));

        String result = transactionService.createTransaction(sender, receiver, transaction.getAmount(), "Test Transaction");

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(appAccountService, times(1)).updateBalanceByUserId(sender.getId(), BigDecimal.valueOf(-105));
        verify(appAccountService, times(1)).updateBalanceByUserId(receiver.getId(), BigDecimal.valueOf(100));
        assertEquals("Transaction successful", result);
    }

    @Test
    void createTransaction_InsufficientBalance() {
        BigDecimal feeAmount = BigDecimal.valueOf(5);
        BigDecimal senderBalance = BigDecimal.valueOf(50);

        when(userRelationService.checkRelation(sender.getId(),receiver.getId())).thenReturn(true);

        when(transactionFeeService.calculateFeeForTransaction(transaction.getAmount())).thenReturn(feeAmount);
        when(appAccountService.getBalanceByIdInBigDecimal(sender.getId())).thenReturn(Optional.of(senderBalance));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.createTransaction(sender, receiver, transaction.getAmount(), "Test Transaction"));
    }

    @Test
    void createTransaction_EntitySaveException() {
        BigDecimal feeAmount = BigDecimal.valueOf(5);
        BigDecimal senderBalance = BigDecimal.valueOf(200);

        when(userRelationService.checkRelation(sender.getId(),receiver.getId())).thenReturn(true);

        when(transactionFeeService.calculateFeeForTransaction(transaction.getAmount())).thenReturn(feeAmount);
        when(appAccountService.getBalanceByIdInBigDecimal(sender.getId())).thenReturn(Optional.of(senderBalance));
        doThrow(RuntimeException.class).when(transactionRepository).save(any(Transaction.class));

        assertThrows(EntitySaveException.class, () ->
                transactionService.createTransaction(sender, receiver, transaction.getAmount(), "Test Transaction"));
    }

    @Test
    void cancelTransaction_Success() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        doNothing().when(transactionRepository).delete(any(Transaction.class));

        String result = transactionService.cancelTransaction(transaction.getId());

        verify(transactionRepository, times(1)).delete(transaction);
        verify(appAccountService, times(1)).updateBalanceByUserId(sender.getId(), transaction.getAmountWithFee());
        verify(appAccountService, times(1)).updateBalanceByUserId(receiver.getId(), transaction.getAmount().negate());
        assertEquals("Transaction canceled successfully", result);
    }

    @Test
    void cancelTransaction_EntityNotFoundException() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                transactionService.cancelTransaction(transaction.getId()));
    }

    @Test
    void cancelTransaction_EntityDeleteException() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(userService.getUserById(sender.getId())).thenReturn(sender);
        when(userService.getUserById(receiver.getId())).thenReturn(receiver);
        doThrow(RuntimeException.class).when(transactionRepository).delete(any(Transaction.class));

        assertThrows(EntityDeleteException.class, () ->
                transactionService.cancelTransaction(transaction.getId()));
    }
}
