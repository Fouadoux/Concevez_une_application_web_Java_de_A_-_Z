package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.InsufficientBalanceException;
import com.paymybuddy.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionFeeService transactionFeeService;
    private final AppAccountService appAccountService;
    private final UserService userService;
    private final UserRelationService userRelationService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionFeeService transactionFeeService,
                              AppAccountService appAccountService, UserService userService,UserRelationService userRelationService) {
        this.transactionRepository = transactionRepository;
        this.transactionFeeService = transactionFeeService;
        this.appAccountService = appAccountService;
        this.userService = userService;
        this.userRelationService=userRelationService;
    }

    /**
     * Creates a transaction between a sender and receiver with a specified amount and description.
     *
     * @param sender      The user sending the transaction
     * @param receiver    The user receiving the transaction
     * @param amount      The amount being transferred
     * @param description A description of the transaction
     * @return A success message if the transaction is created successfully
     */
    public String createTransaction(User sender, User receiver, BigDecimal amount, String description) {

        //vérifier la relation entre sender et receiver
        if (!userRelationService.checkRelation(sender.getId(), receiver.getId())) {
            throw new EntityNotFoundException("No relation exists between the sender and receiver.");
        }

        // Vérifier la limite de transaction quotidienne
        checkTransactionLimit(sender, amount);

        // Vérifier le solde de l'expéditeur
        BigDecimal senderBalance = appAccountService.getBalanceByIdInBigDecimal(sender.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sender account not found with ID: " + sender.getId()));

        // Calculer les frais et le montant total à déduire
        BigDecimal feeAmount = transactionFeeService.calculateFeeForTransaction(amount);
        BigDecimal amountWithFee = amount.add(feeAmount);

        if (senderBalance.compareTo(amountWithFee) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for user ID: " + sender.getId());
        }

        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setAmountWithFee(amountWithFee);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());

        // Ajouter la transaction aux listes de l'expéditeur et du destinataire
        sender.addSenderTransactions(transaction);
        receiver.addReceiverTransactions(transaction);

        // Sauvegarder la transaction
        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save transaction.", e);
        }

        // Mettre à jour les soldes des comptes
        appAccountService.updateBalanceByUserId(sender.getId(), amountWithFee.negate());
        appAccountService.updateBalanceByUserId(receiver.getId(), amount);

        return "Transaction successful";
    }

    /**
     * Retrieves the transaction history for a given user, including both sent and received transactions.
     *
     * @param user The user whose transaction history is to be retrieved
     * @return A list of all transactions where the user is either the sender or receiver
     */
    public List<Transaction> getTransactionHistory(User user) {
        List<Transaction> transactionHistory = new ArrayList<>();

        // Ajouter toutes les transactions envoyées et reçues en utilisant les getters
        transactionHistory.addAll(user.getSenderTransactions());
        transactionHistory.addAll(user.getReceiverTransactions());

        // Trier la liste par date de transaction (les plus récentes en premier)
        transactionHistory.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        return transactionHistory;
    }

    /**
     * Filters transactions within a specified date range for a given user.
     *
     * @param user      The user for whom the transactions are filtered
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A list of transactions within the specified date range
     */
    public List<Transaction> getTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserSenderOrUserReceiverAndTransactionDateBetween(user, user, startDate, endDate);
    }

    /**
     * Calculates the total amount of transactions for a given user.
     *
     * @param user The user for whom the total amount is calculated
     * @return The total amount of transactions
     */
    public BigDecimal calculateTotalTransactions(User user) {
        return transactionRepository.calculateTotalTransactionAmountByUser(user);
    }

    /**
     * Cancels a transaction by its ID, updating the balances of both the sender and receiver.
     *
     * @param transactionId The ID of the transaction to cancel
     * @return A success message if the transaction is canceled
     */
    public String cancelTransaction(int transactionId) {
        // Trouver la transaction, lever une exception si elle n'est pas trouvée
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + transactionId));

        // Vérifier si la transaction peut être annulée (dans les 24 heures)
        if (transaction.getTransactionDate().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new IllegalStateException("Transaction cannot be canceled after 24 hours.");
        }

        // Récupérer les utilisateurs expéditeur et destinataire de la transaction
        User sender = userService.getUserById(transaction.getUserSender().getId());
        User receiver = userService.getUserById(transaction.getUserReceiver().getId());

        // Mise à jour des soldes : ajouter le montant avec les frais pour l'expéditeur, soustraire pour le destinataire
        appAccountService.updateBalanceByUserId(sender.getId(), transaction.getAmountWithFee());
        appAccountService.updateBalanceByUserId(receiver.getId(), transaction.getAmount().negate());

        // Retirer la transaction des listes de l'expéditeur et du destinataire
        sender.removeSenderTransactions(transaction);
        receiver.removeReceiverTransactions(transaction);

        // Supprimer la transaction
        try {
            transactionRepository.delete(transaction);
        } catch (Exception e) {
            throw new EntityDeleteException("Failed to delete transaction with ID: " + transaction.getId(), e);
        }

        return "Transaction canceled successfully";
    }

    /**
     * Calculates the total transaction fees across all transactions.
     *
     * @return The total fees from all transactions
     */
    public BigDecimal calculateTotalFees() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        return allTransactions.stream()
                .map(transaction -> transaction.getAmountWithFee().subtract(transaction.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Checks if a transaction amount exceeds the daily limit for a given user.
     *
     * @param user             The user for whom the limit is checked
     * @param transactionAmount The transaction amount to check
     */
    public void checkTransactionLimit(User user, BigDecimal transactionAmount) {
        BigDecimal dailyLimit = BigDecimal.valueOf(5000);

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        BigDecimal dailyTotal = getTransactionsByDateRange(user, startOfDay, endOfDay).stream()
                .map(Transaction::getAmountWithFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dailyTotal.add(transactionAmount).compareTo(dailyLimit) > 0) {
            throw new IllegalStateException("Transaction limit exceeded for the day.");
        }
    }

    public TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderId(transaction.getUserSender().getId());
        dto.setSenderName(userService.findUsernameByUserId(transaction.getUserSender().getId()));
        dto.setReceiverId(transaction.getUserReceiver().getId());
        dto.setReceiverName(userService.findUsernameByUserId(transaction.getUserReceiver().getId()));
        dto.setAmount(transaction.getAmount());
        dto.setAmountWithFee(transaction.getAmountWithFee());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }

    public List<TransactionDTO> convertToDTOList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
