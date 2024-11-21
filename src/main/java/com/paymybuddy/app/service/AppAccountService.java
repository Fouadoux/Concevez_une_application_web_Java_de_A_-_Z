package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.AppAccountDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.*;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AppAccountService {

    private final AppAccountRepository appAccountRepository;
    private final UserRepository userRepository;

    public AppAccountService(AppAccountRepository appAccountRepository, UserRepository userRepository) {
        this.appAccountRepository = appAccountRepository;
        this.userRepository = userRepository;
    }

    // Méthode privée pour récupérer le compte d'un utilisateur via userId
    public AppAccount findAccountByUserId(int userId) {
        return appAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for user with ID: " + userId));
    }

    // Obtenir le solde par ID de l'utilisateur
    public long getBalanceByUserId(int userId) {
        return findAccountByUserId(userId).getBalance();
    }

    // Mettre à jour le solde par ID de l'utilisateur
    public long updateBalanceByUserId(int userId, long newBalance) {
        AppAccount account = findAccountByUserId(userId);

        long updatedBalance = account.getBalance()+ newBalance;
        if (updatedBalance < 0) {
            throw new InvalidBalanceException("Balance can't be negative. Current balance: " + account.getBalance());
        }

        account.setBalance(updatedBalance);
        try {
            appAccountRepository.save(account);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save updated balance.", e);
        }

        return updatedBalance;
    }

    // Obtenir le solde sous forme de BigDecimal
    public Optional<Long> getBalanceById(int userId) {
        return Optional.of(getBalanceByUserId(userId));
    }

    // Créer un compte pour un utilisateur
    @Transactional
    public AppAccount createAccountForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (appAccountRepository.findByUserId(userId).isPresent()) {
            throw new AccountAlreadyExistsException("User already has an account");
        }

        AppAccount newAccount = new AppAccount();
        newAccount.setUser(user); // Associez l'utilisateur au compte
        newAccount.setBalance(100); // Pour les tests

        try {
            return appAccountRepository.save(newAccount);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save new account.", e);
        }
    }


    public void deleteAccountByUserId(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        AppAccount account = appAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for user with ID: " + userId));

        try {
            appAccountRepository.delete(account);
        } catch (Exception e) {
            throw new EntityDeleteException("Failed to delete account with ID: " + account.getAccountId(), e);
        }
    }

    public AppAccountDTO getInfoAppAccountByUserId(int userId) {
        AppAccount account = findAccountByUserId(userId);
        AppAccountDTO accountDTO = new AppAccountDTO();

        // Mapper les informations de l'entité vers le DTO
        accountDTO.setBalance(account.getBalance());
        accountDTO.setLastUpdate(account.getLastUpdate());
        accountDTO.setCreatedAt(account.getCreatedAt());

        return accountDTO;
    }
}
