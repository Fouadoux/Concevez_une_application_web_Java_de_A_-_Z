package com.paymybuddy.app.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationLoginControllerTest {

    @MockBean
    private RegistrationService registrationService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testRegisterUser_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("userName", "TestUser")
                        .param("email", "") // Email vide
                        .param("password", "password123")
                        .with(csrf())) // Ajout de CSRF pour la requête POST
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email is required"));
    }

    @Test
    void testRegisterUser_ShouldReturnBadRequest_WhenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("userName", "TestUser")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password is required"));
    }

    @Test
    void testRegisterUser_ShouldReturnBadRequest_WhenUserNameIsMissing() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username is required"));
    }

    @Test
    void testRegisterUser_ShouldRedirectToLogin_WhenUserIsValid() throws Exception {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setUserName("TestUser");

        doNothing().when(registrationService).registerUser(any(RegisterDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", registerDTO.getEmail())
                        .param("password", registerDTO.getPassword())
                        .param("userName", registerDTO.getUserName())
                        .with(csrf()))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/login")); // Vérifie la redirection

        verify(registrationService, times(1)).registerUser(any(RegisterDTO.class));
    }

    @Test
    void testRegisterUser_ShouldReturnBadRequest_WhenIllegalArgumentExceptionIsThrown() throws Exception {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setUserName("TestUser");

        doThrow(new IllegalArgumentException("Email already exists"))
                .when(registrationService).registerUser(any(RegisterDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", registerDTO.getEmail())
                        .param("password", registerDTO.getPassword())
                        .param("userName", registerDTO.getUserName())
                        .with(csrf()))
                .andExpect(status().isBadRequest()) // Vérifie le statut 400
                .andExpect(jsonPath("$.error").value("Email already exists"));


        verify(registrationService, times(1)).registerUser(any(RegisterDTO.class));
    }

    @Test
    void testRegisterUser_ShouldReturnInternalServerError_WhenGenericExceptionIsThrown() throws Exception {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setUserName("TestUser");

        // Simuler une exception générique levée par le service
        doThrow(new RuntimeException("Unexpected error"))
                .when(registrationService).registerUser(any(RegisterDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", registerDTO.getEmail())
                        .param("password", registerDTO.getPassword())
                        .param("userName", registerDTO.getUserName())
                        .with(csrf()))
                .andExpect(status().isInternalServerError()) // Vérifie le statut 500
                .andExpect(jsonPath("$.error").value("Failed to register user")); // Vérifie le message d'erreur générique

        // Vérifie que le service a été appelé une fois
        verify(registrationService, times(1)).registerUser(any(RegisterDTO.class));
    }

}