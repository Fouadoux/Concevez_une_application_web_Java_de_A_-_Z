package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionFeeService transactionFeeService;
    private final AppAccountService appAccountService;

    public TransactionService(TransactionRepository transactionRepository, TransactionFeeService transactionFeeService,
                              AppAccountService appAccountService) {
        this.transactionRepository = transactionRepository;
        this.transactionFeeService = transactionFeeService;
        this.appAccountService=appAccountService;
    }

    public ResponseEntity<?> createTransaction(User sender, User receiver, BigDecimal amount, String description) {

        Optional<BigDecimal> optionalBalance = appAccountService.getBalanceByIdInBigDecimal(sender.getId());

        // Vérifier si le solde de l'expéditeur est trouvé
        if (optionalBalance.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender account not found");
        }

        BigDecimal senderBalance = optionalBalance.get();

        // Calculer le montant des frais et le montant total
        BigDecimal feeAmount = transactionFeeService.calculateFeeForTransaction(amount);
        BigDecimal amountWithFee = amount.add(feeAmount);

        // Vérifier si l'expéditeur a suffisamment de solde pour couvrir le montant avec les frais
        if (senderBalance.compareTo(amountWithFee) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
        }

        // Créer et sauvegarder la transaction
        Transaction transaction = new Transaction();
        transaction.setUserSender(sender);
        transaction.setUserReceiver(receiver);
        transaction.setAmount(amount);  // Montant que le destinataire reçoit (sans frais)
        transaction.setAmountWithFee(amountWithFee);  // Montant total payé par l'expéditeur (avec frais)
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Mettre à jour le solde de l'expéditeur (réduire le montant avec les frais)
        appAccountService.updateBalanceById(sender.getId(), amountWithFee.negate());

        // Mettre à jour le solde du destinataire (ajouter le montant sans frais)
        appAccountService.updateBalanceById(receiver.getId(), amount);

        return ResponseEntity.ok("Transaction successful");
    }





}
