package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.MonetizationDTO;
import com.paymybuddy.app.service.MonetizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling monetization-related operations.
 * Provides endpoints to retrieve monetization data for transactions and total monetization.
 */
@Slf4j
@RestController
@RequestMapping("/api/monetization")
@RequiredArgsConstructor
public class MonetizationController {

    private final MonetizationService monetizationService;

    /**
     * Endpoint to retrieve monetization information for a specific transaction by its ID.
     * This method is secured to allow access only for users with "ROLE_ADMIN".
     *
     * @param transactionId The ID of the transaction
     * @return The monetization details for the specified transaction
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<MonetizationDTO> findMonetizationByTransactionIdWithDTO(@PathVariable int transactionId) {
        log.info("Fetching monetization details for transaction ID: {}", transactionId);
        MonetizationDTO monetizationDTO = monetizationService.findMonetizationByTransactionIdWithDTO(transactionId);
        log.info("Monetization details for transaction ID {}: {}", transactionId, monetizationDTO);
        return ResponseEntity.ok(monetizationDTO);
    }

    /**
     * Endpoint to retrieve the total monetization accumulated in the system.
     * This method is secured to allow access only for users with "ROLE_ADMIN".
     *
     * @return The total monetization formatted as a string with two decimal places
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/total")
    public ResponseEntity<String> getTotalMonetization(){
        log.info("Fetching total monetization value");
        long totalResult = monetizationService.getTotalResult();
        double result = totalResult / 100.0;  // Convert the result from cents to dollars (or appropriate currency)
        String formattedResult = String.format("%.2f", result);
        log.info("Total monetization value: {}", formattedResult);
        return ResponseEntity.ok(formattedResult);
    }
}
