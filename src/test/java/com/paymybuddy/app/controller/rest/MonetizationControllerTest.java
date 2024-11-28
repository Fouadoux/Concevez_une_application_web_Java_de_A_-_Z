package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.MonetizationDTO;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.service.MonetizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonetizationController.class)
class MonetizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonetizationService monetizationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindMonetizationByTransactionIdWithDTO_success() throws Exception {
        // Arrange
        MonetizationDTO dto = new MonetizationDTO();
        dto.setTransactionId(1);
        dto.setResult(250);

        when(monetizationService.findMonetizationByTransactionIdWithDTO(1)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/monetization/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.result").value(250));

        verify(monetizationService, times(1)).findMonetizationByTransactionIdWithDTO(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindMonetizationByTransactionIdWithDTO_notFound() throws Exception {
        // Arrange
        when(monetizationService.findMonetizationByTransactionIdWithDTO(1))
                .thenThrow(new EntityNotFoundException("Monetization not found for transaction ID: 1"));

        // Act & Assert
        mockMvc.perform(get("/api/monetization/transaction/1"))
                .andExpect(status().isNotFound());

        verify(monetizationService, times(1)).findMonetizationByTransactionIdWithDTO(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTotalMonetization_success() throws Exception {
        // Arrange
        when(monetizationService.getTotalResult()).thenReturn(12345L); // 123.45 après division

        // Act & Assert
        mockMvc.perform(get("/api/monetization/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("123,45"));

        verify(monetizationService, times(1)).getTotalResult();
    }

}
