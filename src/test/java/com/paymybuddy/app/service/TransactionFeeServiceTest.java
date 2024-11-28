package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidTransactionFeeException;
import com.paymybuddy.app.repository.TransactionFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    void testCreateTransactionFee_InvalidPercentage_ThrowsException() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(0);

        InvalidTransactionFeeException exception = assertThrows(InvalidTransactionFeeException.class, () ->
                transactionFeeService.createTransactionFee(fee));

        assertEquals("The transaction fee percentage must be greater than zero.", exception.getMessage());
        verify(transactionFeeRepository, never()).save(any(TransactionFee.class));
    }

    @Test
    void testCreateTransactionFee_ValidFee_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(5);

        when(transactionFeeRepository.save(any(TransactionFee.class))).thenReturn(fee);

        TransactionFee savedFee = transactionFeeService.createTransactionFee(fee);

        assertNotNull(savedFee);
        assertEquals(5, savedFee.getPercentage());
        verify(transactionFeeRepository).save(fee);
    }

    @Test
    void testGetActiveTransactionFee_NoActiveFee_ThrowsException() {
        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionFeeService.getActiveTransactionFee());

        assertEquals("No active transaction fee found.", exception.getMessage());
    }

    @Test
    void testGetActiveTransactionFee_ValidFee_ReturnsFee() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(5);

        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.of(fee));

        TransactionFee activeFee = transactionFeeService.getActiveTransactionFee();

        assertNotNull(activeFee);
        assertEquals(5, activeFee.getPercentage());
    }

    @Test
    void testUpdateTransactionFeePercentage_InvalidPercentage_ThrowsException() {
        InvalidTransactionFeeException exception = assertThrows(InvalidTransactionFeeException.class, () ->
                transactionFeeService.updateTransactionFeePercentage(1, 0));

        assertEquals("The transaction fee percentage must be greater than zero.", exception.getMessage());
        verify(transactionFeeRepository, never()).save(any(TransactionFee.class));
    }

    @Test
    void testUpdateTransactionFeePercentage_FeeNotFound_ThrowsException() {
        when(transactionFeeRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionFeeService.updateTransactionFeePercentage(1, 10));

        assertEquals("Transaction fee not found with ID: 1", exception.getMessage());
    }

    @Test
    void testUpdateTransactionFeePercentage_ValidFee_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(5);

        when(transactionFeeRepository.findById(1)).thenReturn(Optional.of(fee));
        when(transactionFeeRepository.save(any(TransactionFee.class))).thenReturn(fee);

        TransactionFee updatedFee = transactionFeeService.updateTransactionFeePercentage(1, 10);

        assertNotNull(updatedFee);
        assertEquals(10, updatedFee.getPercentage());
        verify(transactionFeeRepository).save(fee);
    }

    @Test
    void testDeleteTransactionFee_FeeNotFound_ThrowsException() {
        when(transactionFeeRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                transactionFeeService.deleteTransactionFee(1));

        assertEquals("Transaction fee not found with ID: 1", exception.getMessage());
        verify(transactionFeeRepository, never()).delete(any(TransactionFee.class));
    }

    @Test
    void testDeleteTransactionFee_ValidFee_Success() {
        TransactionFee fee = new TransactionFee();
        fee.setId(1);

        when(transactionFeeRepository.findById(1)).thenReturn(Optional.of(fee));

        transactionFeeService.deleteTransactionFee(1);

        verify(transactionFeeRepository, times(1)).delete(fee);
    }

    @Test
    void testCalculateFeeForTransaction_InvalidAmount_ThrowsException() {
        // Arrange: Configure un frais de transaction valide afin que getActiveTransactionFee() ne lève pas une exception.
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(5);
        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.of(fee));

        // Act & Assert: Maintenant, vous pouvez tester l'argument invalide pour calculateFeeForTransaction.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                transactionFeeService.calculateFeeForTransaction(0));

        assertEquals("Transaction amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void testCalculateFeeForTransaction_ValidAmount_Success() {
        // Arrange
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(5000); // 5%

        when(transactionFeeRepository.findTopByOrderByEffectiveDateDesc()).thenReturn(Optional.of(fee));

        long transactionAmount = 10000; // 100.00 unités en centimes

        // Act
        long calculatedFee = transactionFeeService.calculateFeeForTransaction(transactionAmount);

        // Assert
        assertEquals(500, calculatedFee, "The calculated fee should be 500 (5% of 10000).");
    }

}
