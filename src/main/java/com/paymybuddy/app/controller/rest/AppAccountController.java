package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.AppAccountDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.service.AppAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


/**
 * Controller for managing application accounts.
 * Provides endpoints to get account information, update balances, create and delete accounts.
 */

@Slf4j
@RestController
@RequestMapping("/api/appAccounts")
public class AppAccountController {

    private final AppAccountService appAccountService;


    /**
     * Constructs an instance of the AppAccountController.
     *
     * @param appAccountService Service to manage AppAccount operations
     */
    public AppAccountController(AppAccountService appAccountService) {
        this.appAccountService = appAccountService;
    }

    /**
     * Endpoint to get the balance of an account by its user ID.
     * This method is secured to ensure that only the user can view their own balance.
     *
     * @param userId The ID of the account
     * @return The balance of the account or a 404 (NOT FOUND) if the account does not exist
     */
    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Long> getBalanceByUserId(@PathVariable int userId) {
        log.info("Fetching balance for user with ID: {}", userId);
        long balance = appAccountService.getBalanceByUserId(userId);
        log.info("Balance for user {} is {}", userId, balance);
        return ResponseEntity.ok(balance);
    }

    /**
     * Endpoint to get the balance of an account by its user ID for an admin user.
     * This method is only accessible by users with the "ROLE_ADMIN" authority.
     *
     * @param userId The ID of the account
     * @return The balance of the account or a 404 (NOT FOUND) if the account does not exist
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}/balance/admin")
    public ResponseEntity<Long> getBalanceByUserIdAdmin(@PathVariable int userId) {
        log.info("Admin fetching balance for user with ID: {}", userId);
        long balance = appAccountService.getBalanceByUserId(userId);
        log.info("Admin found balance for user {}: {}", userId, balance);
        return ResponseEntity.ok(balance);
    }


    /**
     * Endpoint to get full information of an account by its user ID.
     * This method is secured to ensure that only the user can view their own account details.
     *
     * @param userId The ID of the account
     * @return The account details or a 404 (NOT FOUND) if the account does not exist
     */
    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}")
    public ResponseEntity<AppAccountDTO> getAccountInfo(@PathVariable int userId) {
        log.info("Fetching full account information for user with ID: {}", userId);
        AppAccountDTO accountInfo = appAccountService.getInfoAppAccountByUserId(userId);
        log.info("Retrieved account info for user {}: {}", userId, accountInfo);
        return ResponseEntity.ok(accountInfo);
    }

    /**
     * Endpoint to update the balance of an account by its account ID.
     * This method allows adding or subtracting from the account balance.
     *
     * @param accountId  The ID of the account to be updated
     * @param newBalance The new balance to add or subtract
     * @return The updated balance of the account or a 404 (NOT FOUND) if the account does not exist
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{accountId}/balance/{newBalance}")
    public ResponseEntity<Long> updateBalanceById(@PathVariable int accountId,
                                                        @PathVariable long newBalance) {
        log.info("Updating balance for account with ID: {}. New balance: {}", accountId, newBalance);
        long updatedBalance = appAccountService.updateBalanceByUserId(accountId, newBalance);
        log.info("Balance for account {} updated successfully. New balance: {}", accountId, updatedBalance);
        return ResponseEntity.ok(updatedBalance);
    }


    /**
     * Creates an account for a specific user identified by their user ID.
     * This operation fails if the user already has an existing account.
     *
     * @param userId The ID of the user for whom the account should be created
     * @return A ResponseEntity containing the created account with a 201 (CREATED) status,
     * or a 400 (BAD REQUEST) error if the user already has an account,
     * or 404 (NOT FOUND) if the user is not found
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<AppAccount> createAccountForUser(@PathVariable int userId) {
        log.info("Attempting to create account for user with ID: {}", userId);
        AppAccount account = appAccountService.createAccountForUser(userId);
        log.info("Account created for user {}: {}", userId, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    /**
     * Deletes the account associated with a specific user by their user ID.
     * This operation will fail if no account is found for the user.
     *
     * @param userId The ID of the user whose account should be deleted
     * @return A ResponseEntity with a confirmation message and a 200 (OK) status,
     * or a 404 (NOT FOUND) error if the user or account is not found
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteAccountByUserId(@PathVariable int userId) {
        log.info("Attempting to delete account for user with ID: {}", userId);
        appAccountService.deleteAccountByUserId(userId);
        log.info("Account for user {} deleted successfully", userId);
        return ResponseEntity.ok("Account deleted successfully");
    }

}