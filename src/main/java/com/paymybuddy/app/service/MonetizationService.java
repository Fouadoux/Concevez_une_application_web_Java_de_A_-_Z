package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.MonetizationDTO;
import com.paymybuddy.app.entity.Monetization;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.MonetizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing monetization operations, including saving transaction fees and retrieving monetization data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonetizationService {

    private final MonetizationRepository monetizationRepository;

    /**
     * Saves the monetization (tax/fee) for a given transaction.
     *
     * @param transaction the transaction for which monetization is to be calculated and saved.
     * @throws EntitySaveException if saving the monetization fails.
     */
    @Transactional
    public void saveMonetization(Transaction transaction) {
        log.info("Saving monetization for transaction ID: {}", transaction.getId());

        // Calculate the tax
        long tax = transaction.getAmountWithFee() - transaction.getAmount();

        // Create and save the Monetization entity
        Monetization monetization = new Monetization();
        monetization.setTransaction(transaction);
        monetization.setResult(tax);

        try {
            monetizationRepository.save(monetization);
            log.info("Monetization saved successfully for transaction ID: {}, tax: {}", transaction.getId(), tax);
        } catch (Exception e) {
            log.error("Failed to save monetization for transaction ID: {}", transaction.getId(), e);
            throw new EntitySaveException("Failed to save tax monetization.", e);
        }
    }

    /**
     * Calculates the total result of all monetizations (sum of fees collected).
     *
     * @return the total monetization result.
     * @throws EntityNotFoundException if no monetization records are found.
     */
    @Transactional
    public long getTotalResult() {
        log.info("Calculating total monetization result.");
        return monetizationRepository.calculateTotalResult()
                .orElseThrow(() -> {
                    log.error("No monetization records found.");
                    return new EntityNotFoundException("No monetization records found.");
                });
    }

    /**
     * Converts a Monetization entity to its DTO representation.
     *
     * @param monetization the monetization entity to convert.
     * @return the MonetizationDTO.
     */
    public MonetizationDTO toDTO(Monetization monetization) {
        log.info("Converting Monetization entity to DTO for transaction ID: {}", monetization.getTransaction().getId());

        MonetizationDTO dto = new MonetizationDTO();
        dto.setTransactionId(monetization.getTransaction().getId());
        dto.setResult(monetization.getResult());

        log.info("Conversion to DTO completed for transaction ID: {}", monetization.getTransaction().getId());
        return dto;
    }

    /**
     * Finds monetization for a given transaction ID and returns it as a DTO.
     *
     * @param transactionId the ID of the transaction whose monetization is to be retrieved.
     * @return the MonetizationDTO containing the monetization details.
     * @throws EntityNotFoundException if no monetization is found for the given transaction ID.
     */
    @Transactional
    public MonetizationDTO findMonetizationByTransactionIdWithDTO(int transactionId) {
        log.info("Finding monetization for transaction ID: {}", transactionId);

        Monetization monetization = monetizationRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> {
                    log.error("Monetization not found for transaction ID: {}", transactionId);
                    return new EntityNotFoundException("Monetization not found for transaction ID: " + transactionId);
                });

        log.info("Monetization found for transaction ID: {}", transactionId);
        return toDTO(monetization);
    }
}
