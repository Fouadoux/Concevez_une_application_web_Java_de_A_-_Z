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
     * Create a new bank account for a user.
     *
     * @param bankAccount The bank account details
     * @return The saved bank account
     */
    @Transactional
    public BankAccount createBankAccount(BankAccount bankAccount) {
        bankAccount.setTransferDate(LocalDateTime.now());
        try {
            log.info("Creating new bank account for user with ID: {}", bankAccount.getUser().getId());
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
            log.error("Failed to save bank account for user with ID: {}", bankAccount.getUser().getId(), e);
            throw new EntitySaveException("Failed to save bank account.", e);
        }
    }

    /**
     * Retrieve a bank account by its ID.
     *
     * @param transferId The ID of the bank account
     * @return The bank account details
     */
    public BankAccount getBankAccountById(int transferId) {
        log.info("Retrieving bank account with ID: {}", transferId);
        return bankAccountRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + transferId));
    }

    /**
     * Retrieve all bank accounts for a specific user.
     *
     * @param user The user for whom the bank accounts are retrieved
     * @return A list of bank accounts for the user
     */
    public List<BankAccount> getBankAccountsByUser(User user) {
        log.info("Retrieving all bank accounts for user with ID: {}", user.getId());
        return bankAccountRepository.findAllBankAccountByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + user.getId()));
    }

    /**
     * Update the status of a bank account.
     *
     * @param transferId The ID of the bank account to be updated
     * @param status The new status of the bank account
     * @return The updated bank account
     */
    @Transactional
    public BankAccount updateBankAccountStatus(int transferId, boolean status) {
        BankAccount bankAccount = getBankAccountById(transferId);
        log.info("Updating status of bank account with ID: {} to: {}", transferId, status);
        bankAccount.setStatus(status);
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Update the external bank account number of a bank account.
     *
     * @param transferId The ID of the bank account to be updated
     * @param newExternalBankAccountNumber The new external bank account number
     * @return The updated bank account
     */
    @Transactional
    public BankAccount updateExternalBankAccountNumber(int transferId,String newExternalBankAccountNumber){
        BankAccount bankAccount = getBankAccountById(transferId);
        log.info("Updating external bank account number of bank account with ID: {} to: {}", transferId, newExternalBankAccountNumber);

        bankAccount.setExternalBankAccountNumber(newExternalBankAccountNumber);
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Delete a bank account by its ID.
     *
     * @param transferId The ID of the bank account to be deleted
     */
    @Transactional
    public void deleteBankAccount(int transferId) {
        BankAccount bankAccount = getBankAccountById(transferId);
        try {
            log.info("Deleting bank account with ID: {}", transferId);
            bankAccountRepository.delete(bankAccount);
        } catch (Exception e) {
            log.error("Failed to delete bank account with ID: {}", transferId, e);
            throw new EntityDeleteException("Failed to delete bank account with ID: " + transferId, e);
        }
    }

    /**
     * Transfer funds from an AppAccount to a BankAccount.
     *
     * @param appAccountId The ID of the AppAccount
     * @param bankAccountId The ID of the BankAccount
     * @param amount The amount to transfer
     * @return The updated BankAccount
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
     * Transfer funds from a BankAccount to an AppAccount.
     *
     * @param appAccountId The ID of the AppAccount
     * @param bankAccountId The ID of the BankAccount
     * @param amount The amount to transfer
     * @return The updated AppAccount
     */
    @Transactional
    public AppAccount transferFromBankAccount(int appAccountId, int bankAccountId, long amount) {
        log.info("Transferring {} (in cents) from BankAccount ID: {} to AppAccount ID: {}", amount * 100, bankAccountId, appAccountId);
        AppAccount appAccount = appAccountRepository.findById(appAccountId)
                .orElseThrow(() -> new EntityNotFoundException("App account not found with ID: " + appAccountId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + bankAccountId));

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        if (bankAccount.getAmount() < (amount * 100)) {
            throw new IllegalArgumentException("Insufficient balance in BankAccount with ID: " + bankAccountId);
        }

        appAccount.setBalance(appAccount.getBalance() + (amount * 100));
        bankAccount.setAmount(bankAccount.getAmount() - (amount * 100));

        try {
            bankAccountRepository.save(bankAccount);
            return appAccountRepository.save(appAccount);
        } catch (Exception e) {
            log.error("Failed to transfer funds from BankAccount ID: {} to AppAccount ID: {}", bankAccountId, appAccountId, e);
            throw new EntitySaveException("Failed to transfer funds.", e);
        }
    }
}
