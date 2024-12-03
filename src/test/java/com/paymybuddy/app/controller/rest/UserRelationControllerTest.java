package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.service.UserRelationService;
import com.paymybuddy.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRelationController.class)
class UserRelationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRelationService userRelationService;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testAddRelation_success() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userService.getUserById(1)).thenReturn(user);
        when(userRelationService.addRelation(any(User.class), eq("test@example.com")))
                .thenReturn("Relation successfully added");

        // Act & Assert
        mockMvc.perform(post("/api/relation/add")
                        .with(csrf())
                        .param("userId", "1")
                        .param("email", "test@example.com"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Relation successfully added"));

        verify(userService, times(1)).getUserById(1);
        verify(userRelationService, times(1)).addRelation(any(User.class), eq("test@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteRelation_success() throws Exception {
        // Arrange
        when(userRelationService.deleteRelation(1, 2)).thenReturn("Relation successfully deleted");

        // Act & Assert
        mockMvc.perform(delete("/api/relation/delete")
                        .with(csrf())
                        .param("userId", "1")
                        .param("userRelationId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Relation successfully deleted"));

        verify(userRelationService, times(1)).deleteRelation(1, 2);
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testGetAllRelations_success() throws Exception {
        // Arrange
        RelatedUserDTO relation1 = new RelatedUserDTO(2, "User2");
        RelatedUserDTO relation2 = new RelatedUserDTO(3, "User3");
        List<RelatedUserDTO> relations = List.of(relation1, relation2);

        when(userRelationService.findRelatedUsers(1)).thenReturn(relations);

        // Act & Assert
        mockMvc.perform(get("/api/relation/all/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("User2"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("User3"));

        verify(userRelationService, times(1)).findRelatedUsers(1);
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testCheckRelation_success() throws Exception {
        // Arrange
        when(userRelationService.checkRelation(1, 2)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/relation/check")
                        .param("userId", "1")
                        .param("userRelationId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userRelationService, times(1)).checkRelation(1, 2);
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testAddRelation_userNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(1)).thenThrow(new EntityNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/relation/add")
                        .with(csrf())
                        .param("userId", "1")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("User not found"));

        verify(userService, times(1)).getUserById(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteRelation_relationNotFound() throws Exception {
        // Arrange
        when(userRelationService.deleteRelation(1, 2))
                .thenThrow(new EntityNotFoundException("Relation not found"));

        // Act & Assert
        mockMvc.perform(delete("/api/relation/delete")
                        .with(csrf())
                        .param("userId", "1")
                        .param("userRelationId", "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("Relation not found"));

        verify(userRelationService, times(1)).deleteRelation(1, 2);
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testCheckRelation_notExists() throws Exception {
        // Arrange
        when(userRelationService.checkRelation(1, 2)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/relation/check")
                        .param("userId", "1")
                        .param("userRelationId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(userRelationService, times(1)).checkRelation(1, 2);
    }
}