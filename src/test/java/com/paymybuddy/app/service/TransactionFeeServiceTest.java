package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidTransactionFeeException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.TransactionFeeRepository;
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

class TransactionFeeServiceTest {

    @Mock
    private TransactionFeeRepository transactionFeeRepository;

    @InjectMocks
    private TransactionFeeService transactionFeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransactionFee_InvalidPercentage_ThrowsException() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.ZERO);

        InvalidTransactionFeeException exception = assertThrows(InvalidTransactionFeeException.class, () ->
                transactionFeeService.createTransactionFee(fee));

        assertEquals("The transaction fee percentage must be greater than zero.", exception.getMessage());
        verify(transactionFeeRepository, never()).save(any(TransactionFee.class));
    }

    @Test
    void createTransactionFee_ValidFee_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.valueOf(5));

        when(transactionFeeRepository.save(any(TransactionFee.class))).thenReturn(fee);

        TransactionFee savedFee = transactionFeeService.createTransactionFee(fee);

        assertNotNull(savedFee);
        assertEquals(BigDecimal.valueOf(5), savedFee.getPercentage());
        verify(transactionFeeRepository).save(fee);
    }

    @Test
    void getActiveTransactionFee_NoActiveFee_ThrowsException() {
        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionFeeService.getActiveTransactionFee());

        assertEquals("No active transaction fee found.", exception.getMessage());
    }

    @Test
    void getActiveTransactionFee_ValidFee_ReturnsFee() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.valueOf(5));

        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.of(fee));

        TransactionFee activeFee = transactionFeeService.getActiveTransactionFee();

        assertNotNull(activeFee);
        assertEquals(BigDecimal.valueOf(5), activeFee.getPercentage());
    }

    @Test
    void updateTransactionFeePercentage_InvalidPercentage_ThrowsException() {
        InvalidTransactionFeeException exception = assertThrows(InvalidTransactionFeeException.class, () ->
                transactionFeeService.updateTransactionFeePercentage(1, BigDecimal.ZERO));

        assertEquals("The transaction fee percentage must be greater than zero.", exception.getMessage());
        verify(transactionFeeRepository, never()).save(any(TransactionFee.class));
    }

    @Test
    void updateTransactionFeePercentage_FeeNotFound_ThrowsException() {
        when(transactionFeeRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionFeeService.updateTransactionFeePercentage(1, BigDecimal.valueOf(10)));

        assertEquals("Transaction fee not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateTransactionFeePercentage_ValidFee_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.valueOf(5));

        when(transactionFeeRepository.findById(1)).thenReturn(Optional.of(fee));
        when(transactionFeeRepository.save(any(TransactionFee.class))).thenReturn(fee);

        TransactionFee updatedFee = transactionFeeService.updateTransactionFeePercentage(1, BigDecimal.valueOf(10));

        assertNotNull(updatedFee);
        assertEquals(BigDecimal.valueOf(10), updatedFee.getPercentage());
        verify(transactionFeeRepository).save(fee);
    }

    @Test
    void deleteTransactionFee_FeeNotFound_ThrowsException() {
        when(transactionFeeRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionFeeService.deleteTransactionFee(1));

        assertEquals("Transaction fee not found with ID: 1", exception.getMessage());
        verify(transactionFeeRepository, never()).delete(any(TransactionFee.class));
    }

    @Test
    void deleteTransactionFee_ValidFee_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setFeeId(1);

        when(transactionFeeRepository.findById(1)).thenReturn(Optional.of(fee));

        transactionFeeService.deleteTransactionFee(1);

        verify(transactionFeeRepository, times(1)).delete(fee);
    }

    @Test
    void calculateFeeForTransaction_InvalidAmount_ThrowsException() {
        // Arrange: Configure un frais de transaction valide afin que getActiveTransactionFee() ne lève pas une exception.
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.valueOf(5));
        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.of(fee));

        // Act & Assert: Maintenant, vous pouvez tester l'argument invalide pour calculateFeeForTransaction.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                transactionFeeService.calculateFeeForTransaction(BigDecimal.ZERO));

        assertEquals("Transaction amount must be greater than zero.", exception.getMessage());
    }


    @Test
    void calculateFeeForTransaction_ValidAmount_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.valueOf(5));

        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.of(fee));

        BigDecimal transactionAmount = BigDecimal.valueOf(100);
        BigDecimal calculatedFee = transactionFeeService.calculateFeeForTransaction(transactionAmount);

        assertNotNull(calculatedFee);
        assertEquals(BigDecimal.valueOf(5.0).setScale(1), calculatedFee.setScale(1));
    }
}
