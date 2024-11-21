package com.paymybuddy.app.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.app.dto.AppAccountDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.exception.AccountAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidBalanceException;
import com.paymybuddy.app.service.AppAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppAccountController.class)
@WithMockUser(username = "testUser", roles = {"ADMIN"})
class AppAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppAccountService appAccountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules(); // Supporte JavaTimeModule pour les formats de date
    }

    // Test 1: GET /balance (Succès)
    @Test
    void testGetBalanceByUserId_Success() throws Exception {
        int userId = 1;
        long balance = 100;

        when(appAccountService.getBalanceByUserId(userId)).thenReturn(balance);

        mockMvc.perform(get("/api/appAccounts/{userId}/balance", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(balance)));
    }

    // Test 2: GET /balance (User introuvable)
    @Test
    void testGetBalanceByUserId_UserNotFound() throws Exception {
        int userId = 1;

        when(appAccountService.getBalanceByUserId(userId))
                .thenThrow(new EntityNotFoundException("Account not found for user with ID: " + userId));

        mockMvc.perform(get("/api/appAccounts/{userId}/balance", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found for user with ID: " + userId));
    }

    // Test 3: GET /balance/admin (Succès avec ADMIN)
    @Test
    void testGetBalanceByUserIdAdmin_Success() throws Exception {
        int userId = 1;
        long balance = 100;

        when(appAccountService.getBalanceByUserId(userId)).thenReturn(balance);

        mockMvc.perform(get("/api/appAccounts/{userId}/balance/admin", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(balance)));
    }

    // Test 4: GET /api/appAccounts/{userId} (Succès)
    @Test
    void testGetAccountInfo_Success() throws Exception {
        int userId = 1;
        AppAccountDTO accountInfo = new AppAccountDTO();
        accountInfo.setBalance(1000);
        accountInfo.setLastUpdate(LocalDateTime.now());
        accountInfo.setCreatedAt(LocalDateTime.now());

        when(appAccountService.getInfoAppAccountByUserId(userId)).thenReturn(accountInfo);

        mockMvc.perform(get("/api/appAccounts/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(accountInfo.getBalance()))
                .andExpect(jsonPath("$.lastUpdate").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    // Test 5: PUT /balance (Succès)
    @Test
    void testUpdateBalanceById_Success() throws Exception {
        int userId = 1;
        long newBalance = 50;
        long updatedBalance = 150;

        when(appAccountService.updateBalanceByUserId(userId, newBalance)).thenReturn(updatedBalance);

        String jsonContent = objectMapper.writeValueAsString(newBalance);

        mockMvc.perform(put("/api/appAccounts/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(updatedBalance)));
    }

    // Test 6: PUT /balance (Solde négatif)
    @Test
    void testUpdateBalanceById_NegativeBalance() throws Exception {
        int userId = 1;
        long newBalance = -200;

        when(appAccountService.updateBalanceByUserId(userId, newBalance))
                .thenThrow(new InvalidBalanceException("Balance can't be negative."));

        String jsonContent = objectMapper.writeValueAsString(newBalance);

        mockMvc.perform(put("/api/appAccounts/{userId}/balance", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Balance can't be negative."));
    }

    // Test 7: POST /user/{userId} (Succès)
    @Test
    void testCreateAccountForUser_Success() throws Exception {
        int userId = 1;
        AppAccount account = new AppAccount();
        account.setAccountId(1);
        account.setBalance(100);

        when(appAccountService.createAccountForUser(userId)).thenReturn(account);

        mockMvc.perform(post("/api/appAccounts/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(account.getAccountId()))
                .andExpect(jsonPath("$.balance").value(account.getBalance()));
    }

    // Test 8: POST /user/{userId} (Compte existant)
    @Test
    void testCreateAccountForUser_AccountAlreadyExists() throws Exception {
        int userId = 1;

        when(appAccountService.createAccountForUser(userId))
                .thenThrow(new AccountAlreadyExistsException("User already has an account."));

        mockMvc.perform(post("/api/appAccounts/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User already has an account."));
    }

    // Test 9: DELETE /user/{userId} (Succès)
    @Test
    void testDeleteAccountByUserId_Success() throws Exception {
        int userId = 1;

        doNothing().when(appAccountService).deleteAccountByUserId(userId);

        mockMvc.perform(delete("/api/appAccounts/user/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted successfully"));
    }

    // Test 10: DELETE /user/{userId} (Compte introuvable)
    @Test
    void testDeleteAccountByUserId_AccountNotFound() throws Exception {
        int userId = 1;

        doThrow(new EntityNotFoundException("Account not found for user with ID: " + userId))
                .when(appAccountService).deleteAccountByUserId(userId);

        mockMvc.perform(delete("/api/appAccounts/user/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found for user with ID: " + userId));
    }
}

