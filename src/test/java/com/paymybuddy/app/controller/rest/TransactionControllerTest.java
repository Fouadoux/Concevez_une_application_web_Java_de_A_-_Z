package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.InsufficientBalanceException;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@WithMockUser(username = "testUser", roles = {"ADMIN"})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserService userService;


    @Test
    void testCreateTransaction_success() throws Exception {
        // Arrange
        when(transactionService.createTransaction(1, 2, 100, "Test transaction"))
                .thenReturn("Transaction successful");

        // Act & Assert
        mockMvc.perform(post("/api/transactions/create")
                        .with(csrf())
                        .param("senderId", "1")
                        .param("receiverId", "2")
                        .param("amount", "100")
                        .param("description", "Test transaction")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Transaction successful"))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(transactionService, times(1)).createTransaction(1, 2, 100, "Test transaction");
    }

    @Test
    void testCreateTransaction_noRelationExists() throws Exception {
        // Arrange
        when(transactionService.createTransaction(1, 2, 100, "Test transaction"))
                .thenThrow(new EntityNotFoundException("No relation exists between the sender and receiver."));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/create")
                        .with(csrf()) // Protection CSRF
                        .param("senderId", "1")
                        .param("receiverId", "2")
                        .param("amount", "100")
                        .param("description", "Test transaction")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound()) // Vérifie le statut HTTP 404
                .andExpect(jsonPath("$.details").value("No relation exists between the sender and receiver.")); // Vérifie le message d'erreur

        verify(transactionService, times(1)).createTransaction(1, 2, 100, "Test transaction");
    }

    @Test
    void testCreateTransaction_transactionLimitExceeded() throws Exception {
        // Arrange
        when(transactionService.createTransaction(1, 2, 100, "Test transaction"))
                .thenThrow(new IllegalStateException("Transaction limit exceeded for the day."));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/create")
                        .with(csrf()) // Protection CSRF
                        .param("senderId", "1")
                        .param("receiverId", "2")
                        .param("amount", "100")
                        .param("description", "Test transaction")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()) // Vérifie le statut HTTP 400
                .andExpect(jsonPath("$.details").value("Transaction limit exceeded for the day.")); // Vérifie le message d'erreur

        verify(transactionService, times(1)).createTransaction(1, 2, 100, "Test transaction");
    }

    @Test
    void testCreateTransaction_insufficientBalance() throws Exception {
        // Arrange
        when(transactionService.createTransaction(1, 2, 100, "Test transaction"))
                .thenThrow(new InsufficientBalanceException("Insufficient balance for user ID: 1"));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/create")
                        .with(csrf()) // Protection CSRF
                        .param("senderId", "1")
                        .param("receiverId", "2")
                        .param("amount", "100")
                        .param("description", "Test transaction")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()) // Vérifie le statut HTTP 400
                .andExpect(jsonPath("$.details").value("Insufficient balance for user ID: 1")); // Vérifie le message d'erreur

        verify(transactionService, times(1)).createTransaction(1, 2, 100, "Test transaction");
    }

    @Test
    void testCreateTransaction_saveError() throws Exception {
        // Arrange
        when(transactionService.createTransaction(1, 2, 100, "Test transaction"))
                .thenThrow(new EntitySaveException("Failed to save transaction."));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/create")
                        .with(csrf()) // Protection CSRF
                        .param("senderId", "1")
                        .param("receiverId", "2")
                        .param("amount", "100")
                        .param("description", "Test transaction")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError()) // Vérifie le statut HTTP 500
                .andExpect(jsonPath("$.details").value("Failed to save transaction.")); // Vérifie le message d'erreur

        verify(transactionService, times(1)).createTransaction(1, 2, 100, "Test transaction");
    }

    @Test
    void testCreateTransaction_failure() throws Exception {
        // Arrange
        when(transactionService.createTransaction(1, 2, 100, "Test transaction"))
                .thenThrow(new IllegalStateException("Insufficient funds for transaction"));

        // Act & Assert
        mockMvc.perform(post("/api/transactions/create")
                        .with(csrf()) // Protection CSRF
                        .param("senderId", "1")
                        .param("receiverId", "2")
                        .param("amount", "100")
                        .param("description", "Test transaction")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()) // Vérifie que le statut HTTP est 400
                .andExpect(jsonPath("$.details").value("Insufficient funds for transaction")); // Vérifie le message d'erreur


        verify(transactionService, times(1)).createTransaction(1, 2, 100, "Test transaction");
    }

    @Test
    void testGetTransactionHistory_success() throws Exception {
        // Arrange
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderId(1);
        transactionDTO.setReceiverId(2);
        transactionDTO.setAmount(100);
        transactionDTO.setDescription("Test transaction");

        when(transactionService.getTransactionHistoryByUserId(1))
                .thenReturn(Collections.emptyList()); // Exemple de liste vide
        when(transactionService.convertToDTOList(anyList()))
                .thenReturn(List.of(transactionDTO));

        // Act & Assert
        mockMvc.perform(get("/api/transactions/allByUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].senderId").value(1))
                .andExpect(jsonPath("$[0].receiverId").value(2))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].description").value("Test transaction"));

        verify(transactionService, times(1)).getTransactionHistoryByUserId(1);
        verify(transactionService, times(1)).convertToDTOList(anyList());
    }

    @Test
    void testCancelTransaction_success() throws Exception {
        // Arrange
        when(transactionService.cancelTransaction(1))
                .thenReturn("Transaction canceled successfully");

        // Act & Assert
        mockMvc.perform(delete("/api/transactions/cancel/1")
                        .with(csrf())) // Ajout de la protection CSRF
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction canceled successfully"));

        verify(transactionService, times(1)).cancelTransaction(1);
    }

    @Test
    void testCalculateTotalFees_success() throws Exception {
        // Arrange
        when(transactionService.calculateTotalFees())
                .thenReturn(250L);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/fee"))
                .andExpect(status().isOk())
                .andExpect(content().string("250"));

        verify(transactionService, times(1)).calculateTotalFees();
    }

    @Test
    void testCancelTransaction_notFound() throws Exception {
        // Arrange
        when(transactionService.cancelTransaction(1))
                .thenThrow(new EntityNotFoundException("Transaction not found with ID: 1"));

        // Act & Assert
        mockMvc.perform(delete("/api/transactions/cancel/1")
                        .with(csrf())) // Protection CSRF
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("Transaction not found with ID: 1"));

        verify(transactionService, times(1)).cancelTransaction(1);
    }

    @Test
    void testCancelTransaction_illegalState() throws Exception {
        // Arrange
        when(transactionService.cancelTransaction(1))
                .thenThrow(new IllegalStateException("Transaction cannot be canceled"));

        // Act & Assert
        mockMvc.perform(delete("/api/transactions/cancel/1")
                        .with(csrf())) // Protection CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("Transaction cannot be canceled"));

        verify(transactionService, times(1)).cancelTransaction(1);
    }

}
