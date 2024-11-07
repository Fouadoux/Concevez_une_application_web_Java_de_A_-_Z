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

@Slf4j
@RestController
@RequestMapping("/api/appAccounts")
public class AppAccountController {

    private final AppAccountService appAccountService;

    public AppAccountController(AppAccountService appAccountService) {
        this.appAccountService = appAccountService;
    }

    /**
     * Endpoint to get the balance of an account by its ID.
     *
     * @param userId The ID of the account
     * @return The account balance or an error response if not found
     */
    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalanceByUserId(@PathVariable int userId) {
        BigDecimal balance = appAccountService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}/balance/admin")
    public ResponseEntity<BigDecimal> getBalanceByUserIdAdmin(@PathVariable int userId) {
                BigDecimal balance = appAccountService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }


    /**
     * Endpoint to get full information of an account by its ID.
     *
     * @param userId The ID of the account
     * @return Account information or an error response if not found
     */
    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}")
    public ResponseEntity<AppAccountDTO> getAccountInfo(@PathVariable int userId) {
        AppAccountDTO accountInfo = appAccountService.getInfoAppAccountByUserId(userId);
        return ResponseEntity.ok(accountInfo);
    }

    /**
     * Endpoint to update the balance of an account by its ID.
     *
     * @param accountId  The ID of the account
     * @param newBalance The new balance to add or subtract
     * @return The updated balance or an error response if the account does not exist or if the balance is negative
     */
    @PutMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> updateBalanceById(@PathVariable int accountId,
                                                        @RequestBody BigDecimal newBalance) {
        BigDecimal updatedBalance = appAccountService.updateBalanceByUserId(accountId, newBalance);
        return ResponseEntity.ok(updatedBalance);
    }


    /**
     * Creates an account for a specific user using their ID.
     * This operation fails if the user already has an account.
     *
     * @param userId The ID of the user for whom the account should be created
     * @return A ResponseEntity containing the created account with a 201 (CREATED) status,
     * or a 400 (BAD REQUEST) error if the user already has an account,
     * or 404 (NOT FOUND) if the user is not found
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<AppAccount> createAccountForUser(@PathVariable int userId) {
        AppAccount account = appAccountService.createAccountForUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    /**
     * Deletes the account associated with a specific user by their ID.
     *
     * @param userId The ID of the user whose account should be deleted
     * @return A ResponseEntity with a confirmation message and a 200 (OK) status,
     * or a 404 (NOT FOUND) error if the user or account is not found
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteAccountByUserId(@PathVariable int userId) {
        appAccountService.deleteAccountByUserId(userId);
        return ResponseEntity.ok("Account deleted successfully");
    }

}