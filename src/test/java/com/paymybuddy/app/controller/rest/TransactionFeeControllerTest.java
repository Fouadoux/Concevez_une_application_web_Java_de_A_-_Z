package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.entity.TransactionFee;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.service.TransactionFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionFeeController.class)
@WithMockUser(username = "testUser", roles = {"ADMIN"})
class TransactionFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionFeeService transactionFeeService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetActiveTransactionFee_success() throws Exception {
        // Arrange
        TransactionFee fee = new TransactionFee();
        fee.setId(1);
        fee.setPercentage(2500); // 2.5% exprimé en millièmes
        fee.setEffectiveDate(LocalDateTime.now());

        when(transactionFeeService.getActiveTransactionFee()).thenReturn(fee);

        // Act & Assert
        mockMvc.perform(get("/api/transactionfee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.percentage").value(2500)) // En millièmes
                .andExpect(jsonPath("$.effectiveDate").exists());

        verify(transactionFeeService, times(1)).getActiveTransactionFee();
    }

    @Test
    void testCreateTransactionFee_success() throws Exception {
        // Arrange
        TransactionFee fee = new TransactionFee();
        fee.setId(1);
        fee.setPercentage(2500); // 2.5% exprimé en millièmes
        fee.setEffectiveDate(LocalDateTime.now());

        when(transactionFeeService.createTransactionFee(any(TransactionFee.class))).thenReturn(fee);

        // Act & Assert
        mockMvc.perform(post("/api/transactionfee")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "percentage": 2500
                                }
                                """)) // Envoyer 2500 en millièmes
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.percentage").value(2500)); // Vérifier en millièmes

        verify(transactionFeeService, times(1)).createTransactionFee(any(TransactionFee.class));
    }

    @Test
    void testUpdateTransactionFeePercentage_success() throws Exception {
        // Arrange
        TransactionFee updatedFee = new TransactionFee();
        updatedFee.setId(1);
        updatedFee.setPercentage(3000); // 3.0% exprimé en millièmes
        updatedFee.setEffectiveDate(LocalDateTime.now());

        when(transactionFeeService.updateTransactionFeePercentage(1, 3000)).thenReturn(updatedFee);

        // Act & Assert
        mockMvc.perform(put("/api/transactionfee/update/id/1/percent/3000")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.percentage").value(3000)); // Vérifier en millièmes

        verify(transactionFeeService, times(1)).updateTransactionFeePercentage(1, 3000);
    }

    @Test
    void testDeleteTransactionFee_success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/transactionfee/id/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction fee deleted successfully"));

        verify(transactionFeeService, times(1)).deleteTransactionFee(1);
    }

    @Test
    void testCreateTransactionFee_invalidPercentage() throws Exception {
        // Arrange
        when(transactionFeeService.createTransactionFee(any(TransactionFee.class)))
                .thenThrow(new IllegalArgumentException("The transaction fee percentage must be greater than zero."));

        // Act & Assert
        mockMvc.perform(post("/api/transactionfee")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "percentage": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("The transaction fee percentage must be greater than zero."));

        verify(transactionFeeService, times(1)).createTransactionFee(any(TransactionFee.class));
    }

    @Test
    void testGetActiveTransactionFee_notFound() throws Exception {
        // Arrange
        when(transactionFeeService.getActiveTransactionFee())
                .thenThrow(new EntityNotFoundException("No active transaction fee found."));

        // Act & Assert
        mockMvc.perform(get("/api/transactionfee"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("No active transaction fee found."));

        verify(transactionFeeService, times(1)).getActiveTransactionFee();
    }

    @Test
    void testUpdateTransactionFeePercentage_notFound() throws Exception {
        // Arrange
        when(transactionFeeService.updateTransactionFeePercentage(999, 3000))
                .thenThrow(new EntityNotFoundException("Transaction fee not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/transactionfee/update/id/999/percent/3000")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("Transaction fee not found with ID: 999"));

        verify(transactionFeeService, times(1)).updateTransactionFeePercentage(999, 3000);
    }

    @Test
    void testDeleteTransactionFee_notFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Transaction fee not found with ID: 999"))
                .when(transactionFeeService).deleteTransactionFee(999);

        // Act & Assert
        mockMvc.perform(delete("/api/transactionfee/id/999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("Transaction fee not found with ID: 999"));

        verify(transactionFeeService, times(1)).deleteTransactionFee(999);
    }

}
