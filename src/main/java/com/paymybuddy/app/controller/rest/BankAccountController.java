package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.service.BankAccountService;
import com.paymybuddy.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bankAccounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    public BankAccountController(BankAccountService bankAccountService,UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService=userService;
    }

    /**
     * Create a new bank account for a user.
     *
     * @param bankAccount The bank account details
     * @return The saved bank account
     */
    @PostMapping("/create")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount bankAccount) {
        BankAccount createdBankAccount = bankAccountService.createBankAccount(bankAccount);
        return new ResponseEntity<>(createdBankAccount, HttpStatus.CREATED);
    }

    /**
     * Retrieve a bank account by its ID.
     *
     * @param transferId The ID of the bank account
     * @return The bank account details
     */
    @GetMapping("/{transferId}")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable int transferId) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(transferId);
        return ResponseEntity.ok(bankAccount);
    }

    /**
     * Retrieve all bank accounts for a specific user.
     *
     * @param userId The ID of the user
     * @return A list of bank accounts for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BankAccount>> getBankAccountsByUser(@PathVariable int userId) {
        User user=userService.getUserById(userId);
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByUser(user);
        return ResponseEntity.ok(bankAccounts);
    }

    /**
     * Update the status of a bank account.
     *
     * @param transferId The ID of the bank account to be updated
     * @param status The new status of the bank account
     * @return The updated bank account
     */
    @PutMapping("/updateStatus/{transferId}")
    public ResponseEntity<BankAccount> updateBankAccountStatus(@PathVariable int transferId, @RequestParam boolean status) {
        BankAccount updatedBankAccount = bankAccountService.updateBankAccountStatus(transferId, status);
        return ResponseEntity.ok(updatedBankAccount);
    }

    /**
     * Delete a bank account by its ID.
     *
     * @param transferId The ID of the bank account to be deleted
     * @return A response message indicating success
     */
    @DeleteMapping("/delete/{transferId}")
    public ResponseEntity<String> deleteBankAccount(@PathVariable int transferId) {
        bankAccountService.deleteBankAccount(transferId);
        return ResponseEntity.ok("Bank account deleted successfully");
    }

    /**
     * Transfer funds between an AppAccount and a BankAccount.
     *
     * @param appAccountId The ID of the AppAccount
     * @param bankAccountId The ID of the BankAccount
     * @param amount The amount to transfer
     * @return The updated BankAccount or AppAccount
     */
    @PostMapping("/transferToBankAccount")
    public ResponseEntity<Object> transferToBankAccount(@RequestParam int appAccountId, @RequestParam int bankAccountId,
                                                @RequestParam long amount) {
        Object result = bankAccountService.transferToBankAccount(appAccountId, bankAccountId, amount);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transferFromBankAccount")
    public ResponseEntity<Object> transferFromBankAccount(@RequestParam int appAccountId, @RequestParam int bankAccountId,
                                                        @RequestParam long amount) {
        Object result = bankAccountService.transferFromBankAccount(appAccountId, bankAccountId, amount);
        return ResponseEntity.ok(result);
    }




}
