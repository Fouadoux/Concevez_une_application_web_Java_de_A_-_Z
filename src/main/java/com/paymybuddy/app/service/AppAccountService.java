package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.repository.AppAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AppAccountService {

    private final AppAccountRepository appAccountRepository;

    public AppAccountService(AppAccountRepository appAccountRepository) {
        this.appAccountRepository = appAccountRepository;
    }

    // Obtenir le solde par ID du compte
    public ResponseEntity<BigDecimal> getBalanceById(int id) {
        Optional<AppAccount> accountOptional = appAccountRepository.findById(id);

        if (accountOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);  // Retourne une réponse 404 avec un corps null
        }

        AppAccount account = accountOptional.get();
        return ResponseEntity.ok(account.getBalance());
    }

    // Mettre à jour le solde par ID
    public ResponseEntity<?> updateBalanceById(int id, BigDecimal newBalance) {
        // Trouver le compte AppAccount par son ID
        AppAccount account = appAccountRepository.findById(id)
                .orElse(null);

        // Si le compte n'existe pas, retourner un statut 404
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found with ID: " + id);
        }

        // Calculer le nouveau solde en ajoutant la nouvelle valeur
        BigDecimal updatedBalance = account.getBalance().add(newBalance);

        // Vérifier que le solde ne devient pas négatif
        if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Balance can't be negative. Current balance: " + account.getBalance());
        }

        // Mettre à jour le solde et sauvegarder les modifications
        account.setBalance(updatedBalance);
        appAccountRepository.save(account);

        // Retourner une réponse OK avec le nouveau solde
        return ResponseEntity.ok(updatedBalance);
    }

    // Record pour renvoyer les informations du compte
    public record AppAccountInfo(@jakarta.validation.constraints.Min(0) BigDecimal balance, LocalDateTime lastUpdate, LocalDateTime createdAt) {}

    // Obtenir les informations d'un compte par ID
    public ResponseEntity<?> getInfoAppAccountById(int id) {
        AppAccount account = appAccountRepository.findById(id)
                .orElse(null);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found with ID: " + id);
        }

        AppAccountInfo accountInfo = new AppAccountInfo(
                account.getBalance(),
                account.getLastUpdate(),
                account.getCreatedAt()
        );

        return ResponseEntity.ok(accountInfo);
    }

    public Optional<BigDecimal> getBalanceByIdInBigDecimal(int id) {
        return appAccountRepository.findById(id).map(AppAccount::getBalance);
    }


}
