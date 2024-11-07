package com.paymybuddy.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.app.controller.rest.AppAccountController;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.service.AppAccountService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;

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

    @Test
    void testGetBalanceByUserId_Success() throws Exception {
        int userId = 1;
        BigDecimal balance = BigDecimal.valueOf(100);

        when(appAccountService.getBalanceByUserId(userId)).thenReturn(balance);

        mockMvc.perform(get("/api/appAccounts/{userId}/balance", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(balance.toString()));
    }

  /*  @Test
    void testGetInfoAppAccountById_Success() throws Exception {
        int accountId = 1;
        AppAccountService.AppAccountInfo accountInfo = new AppAccountService.AppAccountInfo(BigDecimal.valueOf(100), null, null);

        when(appAccountService.getInfoAppAccountByUserId(accountId)).thenReturn(accountInfo);

        mockMvc.perform(get("/api/appAccounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(accountInfo.balance()));
    }*/

    @Test
    void testUpdateBalanceById_Success() throws Exception {
        int accountId = 1;
        BigDecimal newBalance = BigDecimal.valueOf(50);
        BigDecimal updatedBalance = BigDecimal.valueOf(150);

        when(appAccountService.updateBalanceByUserId(accountId, newBalance)).thenReturn(updatedBalance);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(newBalance); // Convertit en JSON

        mockMvc.perform(put("/api/appAccounts/{accountId}/balance", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent) // Envoie newBalance dans le corps de la requête
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(updatedBalance.toString()));

        verify(appAccountService, times(1)).updateBalanceByUserId(accountId, newBalance);
    }

    @Test
    void testCreateAccountForUser_Success() throws Exception {
        int userId = 1;
        AppAccount account = new AppAccount();
        account.setAccountId(1);
        account.setBalance(BigDecimal.ZERO);

        when(appAccountService.createAccountForUser(userId)).thenReturn(account);

        mockMvc.perform(post("/api/appAccounts/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())) // Ajoute le jeton CSRF
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(account.getAccountId()))
                .andExpect(jsonPath("$.balance").value(account.getBalance()));
    }

    @Test
    void testDeleteAccountByUserId_Success() throws Exception {
        int userId = 1;

        doNothing().when(appAccountService).deleteAccountByUserId(userId);

        mockMvc.perform(delete("/api/appAccounts/user/{userId}", userId)
                        .with(csrf())) // Ajoute le jeton CSRF
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted successfully"));
    }

}
