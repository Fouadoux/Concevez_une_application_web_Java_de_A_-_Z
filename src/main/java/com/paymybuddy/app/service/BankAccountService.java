package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing bank accounts and handling transfers between bank accounts and application accounts.
 */
@Service
@Slf4j
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final AppAccountRepository appAccountRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, AppAccountRepository appAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.appAccountRepository = appAccountRepository;
    }

    /**
     * Creates a new bank account for a user.
     *
     * @param bankAccount the bank account details.
     * @return the saved bank account.
     */
    @Transactional
    public BankAccount createBankAccount(BankAccount bankAccount) {
        log.info("Creating new bank account for user ID: {}", bankAccount.getUser().getId());
        bankAccount.setTransferDate(LocalDateTime.now());
        try {
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
            log.error("Failed to save bank account for user ID: {}", bankAccount.getUser().getId(), e);
            throw new EntitySaveException("Failed to save bank account.", e);
        }
    }

    /**
     * Retrieves a bank account by its ID.
     *
     * @param transferId the ID of the bank account.
     * @return the bank account details.
     * @throws EntityNotFoundException if no bank account is found with the given ID.
     */
    public BankAccount getBankAccountById(int transferId) {
        log.info("Retrieving bank account with ID: {}", transferId);
        return bankAccountRepository.findById(transferId)
                .orElseThrow(() -> {
                    log.error("Bank account not found with ID: {}", transferId);
                    return new EntityNotFoundException("Bank account not found with ID: " + transferId);
                });
    }

    /**
     * Retrieves all bank accounts for a specific user.
     *
     * @param user the user whose bank accounts are to be retrieved.
     * @return a list of bank accounts for the user.
     * @throws EntityNotFoundException if no bank accounts are found for the user.
     */
    public List<BankAccount> getBankAccountsByUser(User user) {
        log.info("Retrieving all bank accounts for user ID: {}", user.getId());
        return bankAccountRepository.findAllBankAccountByUser(user)
                .orElseThrow(() -> {
                    log.error("No bank accounts found for user ID: {}", user.getId());
                    return new EntityNotFoundException("Bank accounts not found for user ID: " + user.getId());
                });
    }

    /**
     * Updates the status of a bank account.
     *
     * @param transferId the ID of the bank account to update.
     * @param status the new status.
     * @return the updated bank account.
     */
    @Transactional
    public BankAccount updateBankAccountStatus(int transferId, boolean status) {
        log.info("Updating status of bank account ID: {} to {}", transferId, status);
        BankAccount bankAccount = getBankAccountById(transferId);
        bankAccount.setStatus(status);
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Updates the external bank account number of a bank account.
     *
     * @param transferId the ID of the bank account to update.
     * @param newExternalBankAccountNumber the new external bank account number.
     * @return the updated bank account.
     */
    @Transactional
    public BankAccount updateExternalBankAccountNumber(int transferId, String newExternalBankAccountNumber) {
        log.info("Updating external bank account number for bank account ID: {} to {}", transferId, newExternalBankAccountNumber);
        BankAccount bankAccount = getBankAccountById(transferId);
        bankAccount.setExternalBankAccountNumber(newExternalBankAccountNumber);
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Deletes a bank account by its ID.
     *
     * @param transferId the ID of the bank account to delete.
     * @throws EntityDeleteException if the deletion fails.
     */
    @Transactional
    public void deleteBankAccount(int transferId) {
        log.info("Deleting bank account with ID: {}", transferId);
        BankAccount bankAccount = getBankAccountById(transferId);
        try {
            bankAccountRepository.delete(bankAccount);
            log.info("Successfully deleted bank account with ID: {}", transferId);
        } catch (Exception e) {
            log.error("Failed to delete bank account with ID: {}", transferId, e);
            throw new EntityDeleteException("Failed to delete bank account with ID: " + transferId, e);
        }
    }

    /**
     * Transfers funds from an application account to a bank account.
     *
     * @param appAccountId the ID of the application account.
     * @param bankAccountId the ID of the bank account.
     * @param amount the amount to transfer (in cents).
     * @return the updated bank account.
     * @throws IllegalArgumentException if the amount is invalid or the app account balance is insufficient.
     */
    @Transactional
    public BankAccount transferToBankAccount(int appAccountId, int bankAccountId, long amount) {
        log.info("Transferring {} (in cents) from AppAccount ID: {} to BankAccount ID: {}", amount * 100, appAccountId, bankAccountId);
        AppAccount appAccount = appAccountRepository.findById(appAccountId)
                .orElseThrow(() -> new EntityNotFoundException("App account not found with ID: " + appAccountId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + bankAccountId));

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        if (appAccount.getBalance() < (amount * 100)) {
            throw new IllegalArgumentException("Insufficient balance in AppAccount with ID: " + appAccountId);
        }

        appAccount.setBalance(appAccount.getBalance() - (amount * 100));
        bankAccount.setAmount(bankAccount.getAmount() + (amount * 100));

        try {
            appAccountRepository.save(appAccount);
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
            log.error("Failed to transfer funds from AppAccount ID: {} to BankAccount ID: {}", appAccountId, bankAccountId, e);
            throw new EntitySaveException("Failed to transfer funds.", e);
        }
    }

    /**
     * Transfers funds from a bank account to an application account.
     *
     * @param appAccountId the ID of the application account.
     * @param bankAccountId the ID of the bank account.
     * @param amount the amount to transfer (in cents).
     * @return the updated application account.
     * @throws IllegalArgumentException if the amount is invalid or the bank account balance is insufficient.
     */
    @Transactional
    public AppAccount transferFromBankAccount(int appAccountId, int bankAccountId, long amount) {
        log.info("Transferring {} cents from BankAccount ID: {} to AppAccount ID: {}", amount*100, bankAccountId, appAccountId);
        AppAccount appAccount = appAccountRepository.findById(appAccountId)
                .orElseThrow(() -> new EntityNotFoundException("App account not found with ID: " + appAccountId));
        BankAccount bankAccount = getBankAccountById(bankAccountId);

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        if (bankAccount.getAmount() < amount*100) {
            throw new IllegalArgumentException("Insufficient balance in BankAccount ID: " + bankAccountId);
        }

        bankAccount.setAmount(bankAccount.getAmount() - amount*100);
        appAccount.setBalance(appAccount.getBalance() + amount*100);

        try {
            bankAccountRepository.save(bankAccount);
            AppAccount updatedAppAccount = appAccountRepository.save(appAccount);
            log.info("Successfully transferred {} cents to AppAccount ID: {}", amount, appAccountId);
            return updatedAppAccount;
        } catch (Exception e) {
            log.error("Failed to transfer funds to AppAccount ID: {}", appAccountId, e);
            throw new EntitySaveException("Failed to transfer funds.", e);
        }
    }
}
