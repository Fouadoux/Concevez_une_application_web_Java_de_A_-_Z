package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class  TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Endpoint pour créer une nouvelle transaction entre deux utilisateurs.
     *
     * @param senderId     L'ID de l'utilisateur expéditeur
     * @param receiverId   le nom de l'utilisateur destinataire
     * @param amount       Le montant de la transaction
     * @param description  La description de la transaction
     * @return Un message de succès ou une erreur
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestParam int senderId,
                                                                 @RequestParam int receiverId,
                                                                 @RequestParam BigDecimal amount,
                                                                 @RequestParam String description) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);

        String transactionResult = transactionService.createTransaction(sender, receiver, amount, description);

        // Créer une réponse structurée
        Map<String, Object> response = new HashMap<>();
        response.put("message", transactionResult);
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

      //  return ResponseEntity.status(HttpStatus.CREATED).body(transactionResult);
    }

    /**
     * Endpoint pour obtenir l'historique des transactions d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @return La liste des transactions envoyées et reçues par l'utilisateur
     */
    @GetMapping("/allByUser/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        List<Transaction> transactionList = transactionService.getTransactionHistory(user);
        List<TransactionDTO> transactionDTOs = transactionService.convertToDTOList(transactionList);
        return ResponseEntity.ok(transactionDTOs);
    }

    /**
     * Endpoint pour obtenir les transactions d'un utilisateur sur une plage de dates.
     *
     * @param userId    L'ID de l'utilisateur
     * @param startDate La date de début (format ISO)
     * @param endDate   La date de fin (format ISO)
     * @return La liste des transactions dans la plage spécifiée
     */
    @GetMapping("/byDateRange")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @RequestParam int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        User user = userService.getUserById(userId);
        List<Transaction> transactionList = transactionService.getTransactionsByDateRange(user, startDate, endDate);
        return ResponseEntity.ok(transactionList);
    }

    /**
     * Endpoint pour calculer le montant total des transactions d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur
     * @return Le montant total des transactions de l'utilisateur
     */
    @GetMapping("/total/{userId}")
    public ResponseEntity<BigDecimal> calculateTotalTransactions(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        BigDecimal totalTransactions = transactionService.calculateTotalTransactions(user);
        return ResponseEntity.ok(totalTransactions);
    }

    /**
     * Endpoint pour annuler une transaction existante.
     *
     * @param transactionId L'ID de la transaction à annuler
     * @return Un message de succès ou une erreur
     */
    @DeleteMapping("/cancel/{transactionId}")
    public ResponseEntity<String> cancelTransaction(@PathVariable int transactionId) {
        String cancelMessage = transactionService.cancelTransaction(transactionId);
        return ResponseEntity.ok(cancelMessage);
    }

    /**
     * Endpoint pour calculer le montant total des frais de transaction.
     *
     * @return Le montant total des frais
     */
    @GetMapping("/fee")
    public ResponseEntity<BigDecimal> calculateTotalFees() {
        BigDecimal totalFees = transactionService.calculateTotalFees();
        return ResponseEntity.ok(totalFees);
    }
}
