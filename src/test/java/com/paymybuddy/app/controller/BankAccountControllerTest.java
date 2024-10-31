package com.paymybuddy.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.BankAccountService;
import com.paymybuddy.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
@WithMockUser(username = "testUser", roles = {"ADMIN"})
@ActiveProfiles("test")
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule()); // Enregistre le module JSR310
    }

    @Test
    void testCreateBankAccount_Success() throws Exception {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setTransferId(1);
        bankAccount.setAmount(new BigDecimal("100.00"));
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(bankAccountService.createBankAccount(any(BankAccount.class))).thenReturn(bankAccount);

        String jsonContent = objectMapper.writeValueAsString(bankAccount);

        mockMvc.perform(post("/api/bankAccounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf())) // Ajout du jeton CSRF
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferId").value(bankAccount.getTransferId()));
    }


    @Test
    void testGetBankAccountById_Success() throws Exception {
        int transferId = 1;
        BankAccount bankAccount = new BankAccount();
        bankAccount.setTransferId(transferId);
        bankAccount.setAmount(new BigDecimal("100.00"));
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(bankAccountService.getBankAccountById(transferId)).thenReturn(bankAccount);

        mockMvc.perform(get("/api/bankAccounts/{transferId}", transferId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferId").value(transferId));
    }

    @Test
    void testGetBankAccountsByUser_Success() throws Exception {
        int userId = 1;
        User user = new User();
        user.setId(userId);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setTransferId(1);
        bankAccount.setAmount(new BigDecimal("100.00"));
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(userService.getUserById(userId)).thenReturn(user);
        when(bankAccountService.getBankAccountsByUser(user)).thenReturn(List.of(bankAccount));

        mockMvc.perform(get("/api/bankAccounts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].transferId").value(bankAccount.getTransferId()));
    }

    @Test
    void testUpdateBankAccountStatus_Success() throws Exception {
        int transferId = 1;
        boolean newStatus = true;

        BankAccount bankAccount = new BankAccount();
        bankAccount.setTransferId(transferId);
        bankAccount.setStatus(newStatus);

        when(bankAccountService.updateBankAccountStatus(transferId, newStatus)).thenReturn(bankAccount);

        mockMvc.perform(put("/api/bankAccounts/updateStatus/{transferId}", transferId)
                        .param("status", String.valueOf(newStatus))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferId").value(transferId))
                .andExpect(jsonPath("$.status").value(newStatus));
    }

    @Test
    void testDeleteBankAccount_Success() throws Exception {
        int transferId = 1;

        doNothing().when(bankAccountService).deleteBankAccount(transferId);

        mockMvc.perform(delete("/api/bankAccounts/delete/{transferId}", transferId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Bank account deleted successfully"));
    }

    @Test
    void testTransferFunds_Success() throws Exception {
        int appAccountId = 1;
        int bankAccountId = 2;
        BigDecimal amount = new BigDecimal("100.00");
        boolean toBankAccount = true;

        BankAccount bankAccount = new BankAccount();
        bankAccount.setTransferId(bankAccountId);
        bankAccount.setAmount(amount);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(bankAccountService.transferFunds(appAccountId, bankAccountId, amount, toBankAccount)).thenReturn(bankAccount);

        mockMvc.perform(post("/api/bankAccounts/transferFunds")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", amount.toString())
                        .param("toBankAccount", String.valueOf(toBankAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferId").value(bankAccountId));
    }
}
