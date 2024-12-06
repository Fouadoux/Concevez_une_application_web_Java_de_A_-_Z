package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.AppAccountDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.*;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for managing application accounts linked to users.
 */
@Slf4j
@Service
public class AppAccountService {

    private final AppAccountRepository appAccountRepository;
    private final UserRepository userRepository;

    public AppAccountService(AppAccountRepository appAccountRepository, UserRepository userRepository) {
        this.appAccountRepository = appAccountRepository;
        this.userRepository = userRepository;
    }


    /**
     * Finds an account by the user ID.
     *
     * @param userId the ID of the user whose account is to be retrieved.
     * @return the associated AppAccount.
     * @throws EntityNotFoundException if no account is found for the given user ID.
     */
    public AppAccount findAccountByUserId(int userId) {
        log.info("Searching for account by user ID: {}", userId);
        return appAccountRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Account not found for user ID: {}", userId);
                    return new EntityNotFoundException("Account not found for user with ID: " + userId);
                });
    }

    /**
     * Gets the balance of the account associated with the user ID.
     *
     * @param userId the ID of the user.
     * @return the account balance in cents.
     */
    public long getBalanceByUserId(int userId) {
        log.info("Fetching balance for user ID: {}", userId);
        return findAccountByUserId(userId).getBalance();
    }

    /**
     * Updates the balance of the account associated with the user ID.
     *
     * @param userId the ID of the user.
     * @param newBalance the amount to update (positive or negative).
     * @return the updated balance in cents.
     * @throws InvalidBalanceException if the resulting balance is negative.
     */
    public long updateBalanceByUserId(int userId, long newBalance) {
        log.info("Updating balance for user ID: {}, with change: {}", userId, newBalance);
        AppAccount account = findAccountByUserId(userId);

        long updatedBalance = account.getBalance() + newBalance;
        if (updatedBalance < 0) {
            log.error("Balance update failed. Negative balance for user ID: {}", userId);
            throw new InvalidBalanceException("Balance can't be negative. Current balance: " + account.getBalance());
        }

        account.setBalance(updatedBalance);
        try {
            appAccountRepository.save(account);
            log.info("Balance updated successfully for user ID: {}, new balance: {}", userId, updatedBalance);
        } catch (Exception e) {
            log.error("Failed to save updated balance for user ID: {}", userId, e);
            throw new EntitySaveException("Failed to save updated balance.", e);
        }

        return updatedBalance;
    }

    /**
     * Gets the balance of the account as an Optional.
     *
     * @param userId the ID of the user.
     * @return an Optional containing the balance in cents, if found.
     */
    public Optional<Long> getBalanceById(int userId) {
        log.info("Fetching balance as Optional for user ID: {}", userId);
        return Optional.of(getBalanceByUserId(userId));
    }

    /**
     * Creates a new account for a user.
     *
     * @param userId the ID of the user.
     * @return the newly created AppAccount.
     * @throws AccountAlreadyExistsException if the user already has an account.
     */
    @Transactional
    public AppAccount createAccountForUser(int userId) {
        log.info("Creating account for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });


        if (user.getAppAccount() != null) {
            log.error("Account creation failed. User ID: {} already has an account.", userId);
            throw new AccountAlreadyExistsException("User already has an account");
        }

        AppAccount newAccount = new AppAccount();
        newAccount.setUser(user);
        newAccount.setBalance(0L);

        user.setAppAccount(newAccount);

        try {
            AppAccount savedAccount = appAccountRepository.save(newAccount);
            log.info("Account created successfully for user ID: {}", userId);
            return savedAccount;
        } catch (Exception e) {
            log.error("Failed to create account for user ID: {}", userId, e);
            throw new EntitySaveException("Failed to save new account.", e);
        }
    }

    /**
     * Deletes the account associated with the given user ID.
     *
     * @param userId the ID of the user.
     * @throws EntityNotFoundException if no user or account is found for the given user ID.
     */
    @Transactional
    public void deleteAccountByUserId(int userId) {
        log.info("Deleting account for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });

        AppAccount account = findAccountByUserId(userId);


        user.setAppAccount(null); // Maintain bidirectional consistency

        try {
            appAccountRepository.delete(account);
            log.info("Account deleted successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to delete account for user ID: {}", userId, e);
            throw new EntityDeleteException("Failed to delete account with ID: " + account.getId(), e);
        }
    }

    /**
     * Retrieves account information as a DTO for the user ID.
     *
     * @param userId the ID of the user.
     * @return an AppAccountDTO containing account details.
     */
    public AppAccountDTO getInfoAppAccountByUserId(int userId) {
        log.info("Fetching account info as DTO for user ID: {}", userId);

        AppAccount account = findAccountByUserId(userId);
        AppAccountDTO accountDTO = new AppAccountDTO();

        accountDTO.setBalance(account.getBalance());
        accountDTO.setLastUpdate(account.getLastUpdate());
        accountDTO.setCreatedAt(account.getCreatedAt());

        log.info("Account info retrieved successfully for user ID: {}", userId);
        return accountDTO;
    }

    /**
     * Changes the daily transaction limit for a role.
     *
     * @param userId   the name of the role.
     * @param dailyLimit the new daily transaction limit.
     * @throws IllegalArgumentException if the role name is null/blank or the daily limit is invalid.
     * @throws EntityNotFoundException if the role is not found.
     * @throws EntitySaveException if updating the role fails.
     */
    @Transactional
    public void changeDailyLimit(int userId, long dailyLimit) {
        log.info("Changing daily limit for role: {} to {}", userId, dailyLimit);

        AppAccount account=findAccountByUserId(userId);

        if (dailyLimit <= 0) {
            log.error("Invalid daily limit provided: {}", dailyLimit);
            throw new IllegalArgumentException("Daily limit must be a positive value.");
        }

        account.setDailyLimit(dailyLimit);

        try {
            appAccountRepository.save(account);
            log.info("Daily limit for User '{}' updated to {}", userId, dailyLimit);
        } catch (Exception e) {
            log.error("Failed to update daily limit for user : {}", userId, e);
            throw new EntitySaveException("Error while updating the daily limit for user: " + userId, e);
        }
    }

    /**
     * Gets the daily transaction limit for a user based on their role.
     *
     * @param userId the ID of the user.
     * @return the daily transaction limit for the user's role.
     * @throws EntityNotFoundException if the user is not found.
     */
    public long getTransactionLimitForUser(int userId) {
        log.info("Fetching transaction limit for user ID: {}", userId);
        AppAccount account=findAccountByUserId(userId);
        long dailyLimit = account.getDailyLimit();
        log.info("Transaction limit for user ID {} is {}", userId, dailyLimit);
        return dailyLimit;
    }


}
