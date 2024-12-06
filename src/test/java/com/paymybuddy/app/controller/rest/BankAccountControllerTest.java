package com.paymybuddy.app.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.service.BankAccountService;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
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
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateBankAccount_Success() throws Exception {
        User user=new User();
        user.setUserName("test");
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("test@example.fr");
        user.setPassword("password");


        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAmount(100);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);
        bankAccount.setUser(user);


        when(bankAccountService.createBankAccount(any(BankAccount.class))).thenReturn(bankAccount);

        String jsonContent = objectMapper.writeValueAsString(bankAccount);

        mockMvc.perform(post("/api/bankAccounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf())) // Ajout du jeton CSRF
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bankAccount.getId()));
    }

    @Test
    void testCreateBankAccount_fail()throws Exception{

        User user=new User();
        user.setUserName("test");
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("test@example.fr");
        user.setPassword("password");


        BankAccount bankAccount = new BankAccount();
        bankAccount.setAmount(0);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setUser(user);

        String jsonContent= objectMapper.writeValueAsString(bankAccount);

        when(bankAccountService.createBankAccount(any(BankAccount.class)))
                .thenThrow(new EntitySaveException("Failed to save bank account."));

        mockMvc.perform(post("/api/bankAccounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Failed to save bank account."));

        verify(bankAccountService,times(1)).createBankAccount(any(BankAccount.class));


    }

    @Test
    void testGetBankAccountById_Success() throws Exception {
        int transferId = 1;
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(transferId);
        bankAccount.setAmount(100);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(bankAccountService.getBankAccountById(transferId)).thenReturn(bankAccount);

        mockMvc.perform(get("/api/bankAccounts/{transferId}", transferId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(transferId));
    }

    @Test
    void testGetBankAccountById_NotFound()throws Exception{
        int transferId=1;

        when(bankAccountService.getBankAccountById(transferId))
                .thenThrow(new EntityNotFoundException("Bank account not found with ID: " + transferId));;


        mockMvc.perform(get("/api/bankAccounts/{transferId}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Bank account not found with ID: " + transferId));
    }

    @Test
    void testGetBankAccountsByUser_Success() throws Exception {
        int userId = 1;
        User user = new User();
        user.setId(userId);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAmount(100);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(userService.getUserById(userId)).thenReturn(user);
        when(bankAccountService.getBankAccountsByUser(user)).thenReturn(List.of(bankAccount));

        mockMvc.perform(get("/api/bankAccounts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(bankAccount.getId()));
    }

    @Test
    void testGetBankAccountsByUser_UserNotFound() throws Exception {
        // Arrange
        int userId = 1;

        when(userService.getUserById(userId))
                .thenThrow(new EntityNotFoundException("User not found with ID: " + userId));

        // Act & Assert
        mockMvc.perform(get("/api/bankAccounts/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("User not found with ID: " + userId));
    }

    @Test
    void testUpdateBankAccountStatus_Success() throws Exception {
        int transferId = 1;
        boolean newStatus = true;

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(transferId);
        bankAccount.setStatus(newStatus);

        when(bankAccountService.updateBankAccountStatus(transferId, newStatus)).thenReturn(bankAccount);

        mockMvc.perform(put("/api/bankAccounts/updateStatus/{transferId}", transferId)
                        .param("status", String.valueOf(newStatus))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(transferId))
                .andExpect(jsonPath("$.status").value(newStatus));
    }

    @Test
    void testUpdateBankAccountStatus_NotFound() throws Exception {
        // Arrange
        int transferId = 1;
        boolean newStatus = true;

        when(bankAccountService.updateBankAccountStatus(transferId, newStatus))
                .thenThrow(new EntityNotFoundException("Bank account not found with ID: " + transferId));

        // Act & Assert
        mockMvc.perform(put("/api/bankAccounts/updateStatus/{transferId}", transferId)
                        .param("status", String.valueOf(newStatus))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Bank account not found with ID: " + transferId)); // Vérifie le message d'erreur
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
    void testDeleteBankAccount_NotFound() throws Exception {
        // Arrange
        int transferId = 1; // Identifiant long

        doThrow(new EntityNotFoundException("Bank account not found with ID: " + transferId))
                .when(bankAccountService).deleteBankAccount(transferId);

        // Act & Assert
        mockMvc.perform(delete("/api/bankAccounts/delete/{transferId}", transferId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Bank account not found with ID: " + transferId));
    }


    @Test
    void testTransferToBankAccount_Success() throws Exception {
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = 100;
        boolean toBankAccount = true;

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(bankAccountId);
        bankAccount.setAmount(amount);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        when(bankAccountService.transferToBankAccount(appAccountId, bankAccountId, amount)).thenReturn(bankAccount);

        mockMvc.perform(post("/api/bankAccounts/transferToBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .param("toBankAccount", String.valueOf(toBankAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bankAccountId));
    }

    @Test
    void testTransferToBankAccount_AppAccountNotFound() throws Exception {
        // Arrange
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = 100;

        when(bankAccountService.transferToBankAccount(appAccountId, bankAccountId, amount))
                .thenThrow(new EntityNotFoundException("App account not found with ID: " + appAccountId));

        // Act & Assert
        mockMvc.perform(post("/api/bankAccounts/transferToBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("App account not found with ID: " + appAccountId));
    }

    @Test
    void testTransferToBankAccount_BankAccountNotFound() throws Exception {
        // Arrange
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = 100;

        when(bankAccountService.transferToBankAccount(appAccountId, bankAccountId, amount))
                .thenThrow(new EntityNotFoundException("Bank account not found with ID: " + bankAccountId));

        // Act & Assert
        mockMvc.perform(post("/api/bankAccounts/transferToBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Bank account not found with ID: " + bankAccountId));
    }


    @Test
    void testTransferToBankAccount_NegativeAmount() throws Exception {
        // Arrange
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = -100;

        when(bankAccountService.transferToBankAccount(appAccountId, bankAccountId, amount))
                .thenThrow(new IllegalArgumentException("Transfer amount must be greater than zero."));

        // Act & Assert
        mockMvc.perform(post("/api/bankAccounts/transferToBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Transfer amount must be greater than zero."));
    }

    @Test
    void testTransferFromBankAccount() throws Exception {
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = 100;
        boolean toBankAccount = true;

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(bankAccountId);
        bankAccount.setAmount(0);
        bankAccount.setExternalBankAccountNumber("123456789");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);

        AppAccount account=new AppAccount();
        account.setId(1);
        account.setBalance(amount);


        when(bankAccountService.transferFromBankAccount(appAccountId, bankAccountId, amount)).thenReturn(account);

        mockMvc.perform(post("/api/bankAccounts/transferFromBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .param("toBankAccount", String.valueOf(toBankAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testTransferFromBankAccount_AppAccountNotFound() throws Exception {
        // Arrange
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = 100;

        when(bankAccountService.transferFromBankAccount(appAccountId, bankAccountId, amount))
                .thenThrow(new EntityNotFoundException("App account not found with ID: " + appAccountId));

        // Act & Assert
        mockMvc.perform(post("/api/bankAccounts/transferFromBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("App account not found with ID: " + appAccountId));
    }


    @Test
    void testTransferFromBankAccount_BankAccountNotFound() throws Exception {
        // Arrange
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = 100;

        when(bankAccountService.transferFromBankAccount(appAccountId, bankAccountId, amount))
                .thenThrow(new EntityNotFoundException("Bank account not found with ID: " + bankAccountId));

        // Act & Assert
        mockMvc.perform(post("/api/bankAccounts/transferFromBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Bank account not found with ID: " + bankAccountId));
    }


    @Test
    void testTransferFromBankAccount_NegativeAmount() throws Exception {
        // Arrange
        int appAccountId = 1;
        int bankAccountId = 2;
        long amount = -100;

        when(bankAccountService.transferFromBankAccount(appAccountId, bankAccountId, amount))
                .thenThrow(new IllegalArgumentException("Transfer amount must be greater than zero."));

        // Act & Assert
        mockMvc.perform(post("/api/bankAccounts/transferFromBankAccount")
                        .param("appAccountId", String.valueOf(appAccountId))
                        .param("bankAccountId", String.valueOf(bankAccountId))
                        .param("amount", String.valueOf(amount))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Transfer amount must be greater than zero."));
    }

}
