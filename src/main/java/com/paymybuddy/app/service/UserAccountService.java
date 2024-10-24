package com.paymybuddy.app.service;

import org.springframework.stereotype.Service;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@Service
public class UserAccountService {


    private final AppAccountRepository appAccountRepository;
    private final UserRepository userRepository;

    public UserAccountService(AppAccountRepository appAccountRepository, UserRepository userRepository) {
        this.appAccountRepository = appAccountRepository;
        this.userRepository = userRepository;
    }

    // Trouver le compte par utilisateur
    public ResponseEntity<?> getAccountByUserId(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }

        AppAccount account = appAccountRepository.findByUser(user);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found for user with ID: " + userId);
        }

        return ResponseEntity.ok(account);
    }

    // Créer un compte pour un utilisateur
    public ResponseEntity<?> createAccountForUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }

        if (appAccountRepository.findByUser(user) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User already has an account");
        }

        AppAccount newAccount = new AppAccount();
        newAccount.setUser(user);
        newAccount.setBalance(BigDecimal.ZERO); // Balance initiale

        appAccountRepository.save(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newAccount);
    }

    // Supprimer le compte d'un utilisateur
    public ResponseEntity<?> deleteAccountByUserId(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + userId);
        }

        AppAccount account = appAccountRepository.findByUser(user);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found for user with ID: " + userId);
        }

        appAccountRepository.delete(account);
        return ResponseEntity.ok("Account deleted successfully");
    }

}
