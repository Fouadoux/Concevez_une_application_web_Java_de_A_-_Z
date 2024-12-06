package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.MonetizationDTO;
import com.paymybuddy.app.entity.Monetization;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.MonetizationRepository;
import com.paymybuddy.app.service.MonetizationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class MonetizationServiceTest {

    @Mock
    private MonetizationRepository monetizationRepository;

    @InjectMocks
    private MonetizationService monetizationService;

    public MonetizationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShouldReturnTotalResult() {
        // Arrange
        when(monetizationRepository.calculateTotalResult()).thenReturn(Optional.of(300L));

        // Act
        long total = monetizationService.getTotalResult();

        // Assert
        assertEquals(300L, total);
    }

    @Test
    public void testShouldThrowExceptionWhenNoMonetizationFound() {
        // Arrange
        when(monetizationRepository.calculateTotalResult()).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            monetizationService.getTotalResult();
        });

        assertEquals("No monetization records found.", exception.getMessage());
    }

    @Test
    void testSaveMonetization_success() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(10000);
        transaction.setAmountWithFee(10250);

        when(monetizationRepository.save(any(Monetization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        monetizationService.saveMonetization(transaction);

        // Assert
        verify(monetizationRepository, times(1)).save(any(Monetization.class));
    }

    @Test
    void testSaveMonetization_shouldThrowException_whenRepositoryFails() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(10000);
        transaction.setAmountWithFee(10250);

        doThrow(new RuntimeException("Database error"))
                .when(monetizationRepository).save(any(Monetization.class));

        // Act & Assert
        assertThrows(EntitySaveException.class, () -> monetizationService.saveMonetization(transaction));

        verify(monetizationRepository, times(1)).save(any(Monetization.class));
    }

    @Test
    void testFindMonetizationByTransactionIdWithDTO_success() {
        // Arrange
        Monetization monetization = new Monetization();
        monetization.setResult(250L);
        Transaction transaction = new Transaction();
        transaction.setId(1);
        monetization.setTransaction(transaction);

        when(monetizationRepository.findByTransactionId(1)).thenReturn(Optional.of(monetization));

        // Act
        MonetizationDTO dto = monetizationService.findMonetizationByTransactionIdWithDTO(1);

        // Assert
        assertEquals(1, dto.getTransactionId());
        assertEquals(250L, dto.getResult());
    }

}
