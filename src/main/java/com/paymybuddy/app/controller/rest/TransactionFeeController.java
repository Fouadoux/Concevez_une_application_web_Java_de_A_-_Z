package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.service.TransactionFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactionfee")
public class TransactionFeeController {

    private final TransactionFeeService transactionFeeService;

    public TransactionFeeController(TransactionFeeService transactionFeeService) {
        this.transactionFeeService = transactionFeeService;
    }

    /**
     * Endpoint to get the currently active transaction fee.
     *
     * @return The active TransactionFee
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<TransactionFee> getActiveTransactionFee() {
        TransactionFee activeFee = transactionFeeService.getActiveTransactionFee();
        return ResponseEntity.ok(activeFee);
    }

    /**
     * Endpoint to create a new transaction fee.
     *
     * @param fee The TransactionFee to create
     * @return The created TransactionFee with HTTP status 201 (CREATED)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<TransactionFee> createTransactionFee(@RequestBody TransactionFee fee) {
        TransactionFee newFee = transactionFeeService.createTransactionFee(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFee);
    }

    /**
     * Endpoint to update the percentage of an existing transaction fee.
     *
     * @param id         The ID of the transaction fee to update
     * @param newPercent The new percentage to set
     * @return The updated TransactionFee
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/id/{id}/percent/{newPercent}")
    public ResponseEntity<TransactionFee> updateTransactionFeePercentage(@PathVariable int id,
                                                                         @PathVariable long newPercent) {
        TransactionFee updatedFee = transactionFeeService.updateTransactionFeePercentage(id, newPercent);
        return ResponseEntity.ok(updatedFee);
    }

    /**
     * Endpoint to delete a transaction fee by its ID.
     *
     * @param id The ID of the transaction fee to delete
     * @return A confirmation message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteTransactionFee(@PathVariable int id) {
        transactionFeeService.deleteTransactionFee(id);
        return ResponseEntity.ok("Transaction fee deleted successfully");
    }
}
