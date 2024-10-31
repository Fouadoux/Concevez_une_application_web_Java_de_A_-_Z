package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
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
    public BankAccount createBankAccount(BankAccount bankAccount) {
        bankAccount.setTransferDate(LocalDateTime.now());
        try {
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
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
        return bankAccountRepository.findAllBankAccountByUser(user);
    }

    /**
     * Update the status of a bank account.
     *
     * @param transferId The ID of the bank account to be updated
     * @param status The new status of the bank account
     * @return The updated bank account
     */
    public BankAccount updateBankAccountStatus(int transferId, boolean status) {
        BankAccount bankAccount = getBankAccountById(transferId);
        bankAccount.setStatus(status);
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Delete a bank account by its ID.
     *
     * @param transferId The ID of the bank account to be deleted
     */
    public void deleteBankAccount(int transferId) {
        BankAccount bankAccount = getBankAccountById(transferId);
        try {
            bankAccountRepository.delete(bankAccount);
        } catch (Exception e) {
            throw new EntityDeleteException("Failed to delete bank account with ID: " + transferId, e);
        }
    }

    /**
     * Transfer funds between an AppAccount and a BankAccount.
     *
     * @param appAccountId The ID of the AppAccount
     * @param bankAccountId The ID of the BankAccount
     * @param amount The amount to transfer
     * @param toBankAccount True if transferring from AppAccount to BankAccount, false otherwise
     * @return The updated BankAccount or AppAccount
     */
    public Object transferFunds(int appAccountId, int bankAccountId, BigDecimal amount, boolean toBankAccount) {
        AppAccount appAccount = appAccountRepository.findById(appAccountId)
                .orElseThrow(() -> new EntityNotFoundException("App account not found with ID: " + appAccountId));
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + bankAccountId));

        if (toBankAccount) {
            if (appAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in AppAccount with ID: " + appAccountId);
            }
            appAccount.setBalance(appAccount.getBalance().subtract(amount));
            bankAccount.setAmount(bankAccount.getAmount().add(amount));
        } else {
            if (bankAccount.getAmount().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in BankAccount with ID: " + bankAccountId);
            }
            appAccount.setBalance(appAccount.getBalance().add(amount));
            bankAccount.setAmount(bankAccount.getAmount().subtract(amount));
        }

        try {
            appAccountRepository.save(appAccount);
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to transfer funds.", e);
        }
    }
}
