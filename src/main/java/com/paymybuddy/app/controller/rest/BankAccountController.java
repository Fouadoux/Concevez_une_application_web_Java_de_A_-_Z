package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.BankAccountService;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing bank accounts.
 * Provides endpoints for creating, retrieving, updating, and deleting bank accounts, as well as transferring funds between AppAccount and BankAccount.
 */
@Slf4j
@RestController
@RequestMapping("/api/bankAccounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    /**
     * Constructs an instance of the BankAccountController.
     *
     * @param bankAccountService Service to manage BankAccount operations
     * @param userService        Service to manage User operations
     */
    public BankAccountController(BankAccountService bankAccountService, UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    /**
     * Create a new bank account for a user.
     *
     * @param bankAccount The bank account details
     * @return The saved bank account with a 201 (CREATED) status
     */
    @PostMapping("/create")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount bankAccount) {
        log.info("Creating new bank account for user: {}", bankAccount.getUser().getId());
        BankAccount createdBankAccount = bankAccountService.createBankAccount(bankAccount);
        log.info("Bank account created successfully for user {}: {}", bankAccount.getUser().getId(), createdBankAccount.getId());
        return new ResponseEntity<>(createdBankAccount, HttpStatus.CREATED);
    }

    /**
     * Retrieve a bank account by its ID.
     *
     * @param transferId The ID of the bank account
     * @return The bank account details with a 200 (OK) status
     */
    @GetMapping("/{transferId}")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable int transferId) {
        log.info("Fetching bank account with ID: {}", transferId);
        BankAccount bankAccount = bankAccountService.getBankAccountById(transferId);
        log.info("Fetched bank account details: {}", bankAccount);
        return ResponseEntity.ok(bankAccount);
    }

    /**
     * Retrieve all bank accounts for a specific user.
     *
     * @param userId The ID of the user
     * @return A list of bank accounts for the user with a 200 (OK) status
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BankAccount>> getBankAccountsByUser(@PathVariable int userId) {
        log.info("Fetching bank accounts for user with ID: {}", userId);
        User user = userService.getUserById(userId);
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByUser(user);
        log.info("Found {} bank accounts for user {}", bankAccounts.size(), userId);
        return ResponseEntity.ok(bankAccounts);
    }

    /**
     * Update the status of a bank account.
     *
     * @param transferId The ID of the bank account to be updated
     * @param status     The new status of the bank account
     * @return The updated bank account with a 200 (OK) status
     */
    @PutMapping("/updateStatus/{transferId}")
    public ResponseEntity<BankAccount> updateBankAccountStatus(@PathVariable int transferId, @RequestParam boolean status) {
        log.info("Updating status for bank account with ID: {} to status: {}", transferId, status);
        BankAccount updatedBankAccount = bankAccountService.updateBankAccountStatus(transferId, status);
        log.info("Bank account status updated for account ID: {}", transferId);
        return ResponseEntity.ok(updatedBankAccount);
    }

    /**
     * Delete a bank account by its ID.
     *
     * @param transferId The ID of the bank account to be deleted
     * @return A response message indicating success with a 200 (OK) status
     */
    @DeleteMapping("/delete/{transferId}")
    public ResponseEntity<String> deleteBankAccount(@PathVariable int transferId) {
        log.info("Deleting bank account with ID: {}", transferId);
        bankAccountService.deleteBankAccount(transferId);
        log.info("Bank account with ID: {} deleted successfully", transferId);
        return ResponseEntity.ok("Bank account deleted successfully");
    }

    /**
     * Transfer funds between an AppAccount and a BankAccount.
     *
     * @param appAccountId The ID of the AppAccount
     * @param bankAccountId The ID of the BankAccount
     * @param amount The amount to transfer
     * @return The updated BankAccount or AppAccount with a 200 (OK) status
     */
    @PostMapping("/transferToBankAccount")
    public ResponseEntity<Object> transferToBankAccount(@RequestParam int appAccountId, @RequestParam int bankAccountId,
                                                        @RequestParam long amount) {
        log.info("Transferring {} from AppAccount ID: {} to BankAccount ID: {}", amount, appAccountId, bankAccountId);
        Object result = bankAccountService.transferToBankAccount(appAccountId, bankAccountId, amount);
        log.info("Transfer successful. New balance for BankAccount ID: {} is {}", bankAccountId, result);
        return ResponseEntity.ok(result);
    }

    /**
     * Transfer funds from a BankAccount to an AppAccount.
     *
     * @param appAccountId The ID of the AppAccount
     * @param bankAccountId The ID of the BankAccount
     * @param amount The amount to transfer
     * @return The updated BankAccount or AppAccount with a 200 (OK) status
     */
    @PostMapping("/transferFromBankAccount")
    public ResponseEntity<Object> transferFromBankAccount(@RequestParam int appAccountId, @RequestParam int bankAccountId,
                                                          @RequestParam long amount) {
        log.info("Transferring {} from BankAccount ID: {} to AppAccount ID: {}", amount, bankAccountId, appAccountId);
        Object result = bankAccountService.transferFromBankAccount(appAccountId, bankAccountId, amount);
        log.info("Transfer successful. New balance for AppAccount ID: {} is {}", appAccountId, result);
        return ResponseEntity.ok(result);
    }
}