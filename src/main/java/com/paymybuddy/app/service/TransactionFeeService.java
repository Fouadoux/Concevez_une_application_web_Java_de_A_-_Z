package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidTransactionFeeException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.TransactionFeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for managing transaction fees.
 */
@Slf4j
@Service
public class TransactionFeeService {

    private final TransactionFeeRepository transactionFeeRepository;

    public TransactionFeeService(TransactionFeeRepository transactionFeeRepository) {
        this.transactionFeeRepository = transactionFeeRepository;
    }

    /**
     * Creates a new transaction fee.
     *
     * @param fee the details of the transaction fee.
     * @return the created transaction fee.
     * @throws InvalidTransactionFeeException if the fee percentage is invalid.
     * @throws EntitySaveException if the transaction fee cannot be saved.
     */
    public TransactionFee createTransactionFee(TransactionFee fee) {
        log.info("Creating a new transaction fee with percentage: {}‰", fee.getPercentage());

        if (fee.getPercentage() <= 0) {
            log.error("Invalid transaction fee percentage: {}", fee.getPercentage());
            throw new InvalidTransactionFeeException("The transaction fee percentage must be greater than zero.");
        }

        fee.setEffectiveDate(LocalDateTime.now());

        try {
            TransactionFee savedFee = transactionFeeRepository.save(fee);
            log.info("Transaction fee created successfully with ID: {}", savedFee.getId());
            return savedFee;
        } catch (Exception e) {
            log.error("Failed to save transaction fee.", e);
            throw new EntitySaveException("Failed to save transaction fee.", e);
        }
    }

    /**
     * Retrieves the active transaction fee.
     *
     * @return the active transaction fee.
     * @throws EntityNotFoundException if no active transaction fee is found.
     */
    public TransactionFee getActiveTransactionFee() {
        log.info("Fetching the active transaction fee.");
        return transactionFeeRepository.findTopByOrderByEffectiveDateDesc()
                .orElseThrow(() -> {
                    log.error("No active transaction fee found.");
                    return new EntityNotFoundException("No active transaction fee found.");
                });
    }

    /**
     * Updates the percentage of an existing transaction fee.
     *
     * @param id the ID of the transaction fee to update.
     * @param newPercentage the new fee percentage (in thousandths, e.g., 2500 for 2.5%).
     * @return the updated transaction fee.
     * @throws InvalidTransactionFeeException if the new percentage is invalid.
     * @throws EntityNotFoundException if the transaction fee is not found.
     */
    public TransactionFee updateTransactionFeePercentage(int id, long newPercentage) {
        log.info("Updating transaction fee with ID: {} to new percentage: {}‰", id, newPercentage);

        if (newPercentage <= 0) {
            log.error("Invalid new transaction fee percentage: {}", newPercentage);
            throw new InvalidTransactionFeeException("The transaction fee percentage must be greater than zero.");
        }

        return transactionFeeRepository.findById(id).map(fee -> {
            fee.setPercentage(newPercentage);
            TransactionFee updatedFee = transactionFeeRepository.save(fee);
            log.info("Transaction fee with ID: {} updated successfully.", id);
            return updatedFee;
        }).orElseThrow(() -> {
            log.error("Transaction fee not found with ID: {}", id);
            return new EntityNotFoundException("Transaction fee not found with ID: " + id);
        });
    }

    /**
     * Deletes a transaction fee by its ID.
     *
     * @param id the ID of the transaction fee to delete.
     * @throws EntityNotFoundException if the transaction fee is not found.
     * @throws EntityDeleteException if the deletion fails.
     */
    public void deleteTransactionFee(int id) {
        log.info("Deleting transaction fee with ID: {}", id);

        TransactionFee feeToDelete = transactionFeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction fee not found with ID: {}", id);
                    return new EntityNotFoundException("Transaction fee not found with ID: " + id);
                });

        try {
            transactionFeeRepository.delete(feeToDelete);
            log.info("Transaction fee with ID: {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Failed to delete transaction fee with ID: {}", id, e);
            throw new EntityDeleteException("Failed to delete transaction fee with ID: " + id, e);
        }
    }

    /**
     * Calculates the fee for a given transaction amount.
     *
     * @param transactionAmount the transaction amount (in cents).
     * @return the calculated fee (in cents).
     * @throws IllegalArgumentException if the transaction amount is invalid.
     */
    public long calculateFeeForTransaction(long transactionAmount) {
        log.info("Calculating transaction fee for amount: {} cents", transactionAmount);

        if (transactionAmount <= 0) {
            log.error("Invalid transaction amount: {}", transactionAmount);
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        TransactionFee activeFee = getActiveTransactionFee();

        // Retrieve the fee percentage (in thousandths)
        long feePercentage = activeFee.getPercentage();

        // Calculate the fee (in cents)
        long calculatedFee = (transactionAmount * feePercentage) / 100000;
        log.info("Calculated fee for amount {} cents is {} cents using percentage {}‰.", transactionAmount, calculatedFee, feePercentage);
        return calculatedFee;
    }
}
