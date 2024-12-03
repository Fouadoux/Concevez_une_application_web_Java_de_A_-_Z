package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.InsufficientBalanceException;
import com.paymybuddy.app.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling transaction operations, including creation, retrieval, and cancellation of transactions.
 */
@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionFeeService transactionFeeService;
    private final AppAccountService appAccountService;
    private final UserService userService;
    private final UserRelationService userRelationService;
    private final RoleService roleService;
    private final MonetizationService monetizationService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionFeeService transactionFeeService,
                              AppAccountService appAccountService, UserService userService, UserRelationService userRelationService,
                              RoleService roleService, MonetizationService monetizationService) {
        this.transactionRepository = transactionRepository;
        this.transactionFeeService = transactionFeeService;
        this.appAccountService = appAccountService;
        this.userService = userService;
        this.userRelationService = userRelationService;
        this.roleService = roleService;
        this.monetizationService = monetizationService;
    }

    /**
     * Creates a transaction between a sender and receiver with a specified amount and description.
     *
     * @param senderId    The user sending the transaction.
     * @param receiverId  The user receiving the transaction.
     * @param amount      The amount being transferred (in cents).
     * @param description A description of the transaction.
     * @return A success message if the transaction is created successfully.
     * @throws EntityNotFoundException if the sender and receiver are not connected.
     * @throws InsufficientBalanceException if the sender's balance is insufficient.
     * @throws EntitySaveException if the transaction fails to save.
     */
    public String createTransaction(int senderId, int receiverId, long amount, String description) {
        log.info("Creating transaction from user {} to user {} with amount: {} and description: {}", senderId, receiverId, amount, description);

        long amountInCents = amount * 100;

        // Retrieve sender and receiver
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        log.debug("Sender: {}, Receiver: {}", sender, receiver);

        // Check relation between sender and receiver
        if (!userRelationService.checkRelation(sender.getId(), receiver.getId())) {
            log.warn("No relation exists between user {} and user {}", senderId, receiverId);
            throw new EntityNotFoundException("No relation exists between the sender and receiver.");
        }

        // Verify daily transaction limit
        if (!checkTransactionLimit(senderId, amountInCents)) {
            log.warn("Transaction limit exceeded for user {}", senderId);
            throw new IllegalStateException("Transaction limit exceeded for the day.");
        }

        // Verify sender's balance
        long senderBalance = appAccountService.getBalanceById(sender.getId())
                .orElseThrow(() -> {
                    log.error("Sender account not found with ID: {}", sender.getId());
                    return new EntityNotFoundException("Sender account not found with ID: " + sender.getId());
                });

        long feeAmount = transactionFeeService.calculateFeeForTransaction(amountInCents);
        long totalDeduction = amountInCents + feeAmount;

        if (senderBalance < totalDeduction) {
            log.error("Insufficient balance for user {}. Available: {}, Required: {}", senderId, senderBalance, totalDeduction);
            throw new InsufficientBalanceException("Insufficient balance for user ID: " + sender.getId());
        }

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(amountInCents);
        transaction.setAmountWithFee(totalDeduction);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        try {
            transactionRepository.save(transaction);
            log.info("Transaction saved successfully: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to save transaction: {}", e.getMessage(), e);
            throw new EntitySaveException("Failed to save transaction.", e);
        }

        // Update account balances
        appAccountService.updateBalanceByUserId(sender.getId(), -totalDeduction);
        appAccountService.updateBalanceByUserId(receiver.getId(), amountInCents);
        log.info("Balances updated for sender {} and receiver {}", senderId, receiverId);

        // Save monetization details
        monetizationService.saveMonetization(transaction);
        log.info("Transaction monetization saved for transaction {}", transaction.getId());

        return "Transaction successful";
    }

    /**
     * Retrieves the transaction history for a given user, including both sent and received transactions.
     *
     * @param userId The user whose transaction history is to be retrieved.
     * @return A list of all transactions where the user is either the sender or receiver.
     */
    public List<Transaction> getTransactionHistoryByUserId(int userId) {
        log.info("Fetching transaction history for user {}", userId);

        User user = userService.getUserById(userId);
        List<Transaction> transactionHistory = new ArrayList<>();
        transactionHistory.addAll(user.getSenderTransactions());
        transactionHistory.addAll(user.getReceiverTransactions());

        transactionHistory.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        log.info("Transaction history retrieved: {} transactions found", transactionHistory.size());
        return transactionHistory;
    }

    /**
     * Cancels a transaction by its ID, updating the balances of both the sender and receiver.
     *
     * @param transactionId The ID of the transaction to cancel.
     * @return A success message if the transaction is canceled.
     * @throws EntityNotFoundException if the transaction is not found.
     * @throws IllegalStateException if the transaction cannot be canceled (e.g., after 24 hours).
     * @throws EntityDeleteException if the transaction fails to delete.
     */
    public String cancelTransaction(int transactionId) {
        log.info("Canceling transaction with ID {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("Transaction not found with ID {}", transactionId);
                    return new EntityNotFoundException("Transaction not found with ID: " + transactionId);
                });

        if (transaction.getTransactionDate().isBefore(LocalDateTime.now().minusDays(1))) {
            log.warn("Transaction ID {} cannot be canceled after 24 hours", transactionId);
            throw new IllegalStateException("Transaction cannot be canceled after 24 hours.");
        }

        User sender = transaction.getUserSender();
        User receiver = transaction.getUserReceiver();

        appAccountService.updateBalanceByUserId(sender.getId(), transaction.getAmountWithFee());
        appAccountService.updateBalanceByUserId(receiver.getId(), -transaction.getAmount());
        log.info("Balances reverted for sender {} and receiver {}", sender.getId(), receiver.getId());

        try {
            transactionRepository.delete(transaction);
            log.info("Transaction with ID {} canceled successfully", transactionId);
        } catch (Exception e) {
            log.error("Failed to delete transaction ID {}: {}", transactionId, e.getMessage(), e);
            throw new EntityDeleteException("Failed to delete transaction with ID: " + transactionId, e);
        }

        return "Transaction canceled successfully";
    }

    /**
     * Calculates the total transaction fees across all transactions.
     *
     * @return The total fees from all transactions.
     */
    public long calculateTotalFees() {
        log.info("Calculating total transaction fees");
        List<Transaction> allTransactions = transactionRepository.findAll();

        long totalFees = allTransactions.stream()
                .mapToLong(transaction -> transaction.getAmountWithFee() - transaction.getAmount())
                .sum();
        log.info("Total fees calculated: {}", totalFees);
        return totalFees;
    }

    /**
     * Checks if a transaction amount exceeds the daily limit for a given user.
     *
     * @param userId           The user for whom the limit is checked.
     * @param transactionAmount The transaction amount to check (in cents).
     * @return True if the transaction is within the limit, false otherwise.
     */
    public boolean checkTransactionLimit(int userId, long transactionAmount) {
        log.info("Checking transaction limit for user {}", userId);

        long dailyLimit = roleService.getTransactionLimitForUser(userId);

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        long dailyTransactionBySender = getTotalSentByUser(userId, startOfDay, endOfDay);
        long remainingLimit = dailyLimit - dailyTransactionBySender;

        boolean isWithinLimit = (remainingLimit - transactionAmount) >= 0;
        log.info("Daily limit check: user {}, remaining limit {}, is within limit: {}", userId, remainingLimit, isWithinLimit);
        return isWithinLimit;
    }

    /**
     * Converts a transaction entity to a DTO.
     *
     * @param transaction The transaction to convert.
     * @return The converted DTO.
     */
    public TransactionDTO convertToDTO(Transaction transaction) {
        log.info("Converting transaction ID {} to DTO", transaction.getId());

        TransactionDTO dto = new TransactionDTO();
        dto.setSenderId(transaction.getUserSender().getId());
        dto.setSenderName(userService.findUsernameByUserId(transaction.getUserSender().getId()));
        dto.setReceiverId(transaction.getUserReceiver().getId());
        dto.setReceiverName(userService.findUsernameByUserId(transaction.getUserReceiver().getId()));
        dto.setAmount(transaction.getAmount());
        dto.setAmountWithFee(transaction.getAmountWithFee());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());

        log.info("Transaction ID {} converted to DTO", transaction.getId());
        return dto;
    }

    /**
     * Converts a list of transaction entities to a list of DTOs.
     *
     * @param transactions The list of transactions to convert.
     * @return The list of converted DTOs.
     */
    public List<TransactionDTO> convertToDTOList(List<Transaction> transactions) {
        log.info("Converting list of transactions to DTOs");
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total amount sent by a user within a date range.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return The total amount sent by the user.
     */
    public long getTotalSentByUser(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating total sent by user {} between {} and {}", userId, startDate, endDate);
        User user = userService.getUserById(userId);
        long totalSent = transactionRepository.calculateTotalSentByUserAndDateRange(user, startDate, endDate);
        log.info("Total sent by user {}: {}", userId, totalSent);
        return totalSent;
    }
}
