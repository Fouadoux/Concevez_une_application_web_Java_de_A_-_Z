package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling transaction-related operations.
 * Provides endpoints for creating, retrieving, canceling transactions, and calculating transaction fees.
 */
@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Constructs an instance of TransactionController.
     *
     * @param transactionService Service to manage Transaction operations
     * @param userService        Service to manage User operations
     */
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Endpoint to create a new transaction between two users.
     * This method processes the transaction and returns a success message with the result.
     *
     * @param senderId    The ID of the sender user
     * @param receiverId  The ID of the receiver user
     * @param amount      The transaction amount
     * @param description The description of the transaction
     * @return A response message indicating the transaction status
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestParam int senderId,
                                                                 @RequestParam int receiverId,
                                                                 @RequestParam long amount,
                                                                 @RequestParam String description) {
        log.info("Creating transaction from user {} to user {} for amount {} with description: {}",
                senderId, receiverId, amount, description);

        String transactionResult = transactionService.createTransaction(senderId, receiverId, amount, description);

        // Creating a structured response
        Map<String, Object> response = new HashMap<>();
        response.put("message", transactionResult);
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());

        log.info("Transaction created successfully: {}", transactionResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint to retrieve the transaction history of a user.
     * This method returns a list of transactions sent and received by the user.
     *
     * @param userId The ID of the user
     * @return A list of transaction history for the user
     */
    @GetMapping("/allByUser/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(@PathVariable int userId) {
        log.info("Fetching transaction history for user with ID: {}", userId);

        List<Transaction> transactionList = transactionService.getTransactionHistoryByUserId(userId);
        List<TransactionDTO> transactionDTOs = transactionService.convertToDTOList(transactionList);

        log.info("Found {} transactions for user {}", transactionDTOs.size(), userId);

        return ResponseEntity.ok(transactionDTOs);
    }

    /**
     * Endpoint to cancel an existing transaction.
     * This method cancels the specified transaction and returns a success message.
     *
     * @param transactionId The ID of the transaction to cancel
     * @return A response message indicating the cancellation result
     */
    @DeleteMapping("/cancel/{transactionId}")
    public ResponseEntity<String> cancelTransaction(@PathVariable int transactionId) {
        log.info("Attempting to cancel transaction with ID: {}", transactionId);

        String cancelMessage = transactionService.cancelTransaction(transactionId);

        log.info("Transaction cancellation result for ID {}: {}", transactionId, cancelMessage);

        return ResponseEntity.ok(cancelMessage);
    }

    /**
     * Endpoint to calculate the total fees for all transactions.
     * This method calculates and returns the total amount of transaction fees.
     *
     * @return The total transaction fees
     */
    @GetMapping("/fee")
    public ResponseEntity<Long> calculateTotalFees() {
        log.info("Calculating total transaction fees");

        long totalFees = transactionService.calculateTotalFees();

        log.info("Total transaction fees calculated: {}", totalFees);

        return ResponseEntity.ok(totalFees);
    }
}
