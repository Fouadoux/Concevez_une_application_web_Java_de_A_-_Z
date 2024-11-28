package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.UpdateUserRequestDTO;
import com.paymybuddy.app.dto.UserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testUpdateUser_success() throws Exception {
        // Arrange
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setUserName("NewUserName");
        request.setEmail("newemail@example.com");

        doNothing().when(userService).updateUser(eq(1), any(UpdateUserRequestDTO.class));

        // Act & Assert
        mockMvc.perform(put("/api/users/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userName": "NewUserName",
                                    "email": "newemail@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Vos informations ont été mises à jour avec succès !"));

        verify(userService, times(1)).updateUser(eq(1), any(UpdateUserRequestDTO.class));
    }

    @Test
    void testGetAllUsers_success() throws Exception {
        // Arrange
        UserDTO user1 = new UserDTO("Alice", "alice@example.com", "ADMIN", LocalDateTime.now());
        UserDTO user2 = new UserDTO("Bob", "bob@example.com", "USER", LocalDateTime.now());
        List<UserDTO> userDTOS = List.of(user1, user2);

        when(userService.convertToDTOList(anyList())).thenReturn(userDTOS);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById_success() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO("Alice", "alice@example.com", "ADMIN", LocalDateTime.now());
        when(userService.getUserById(1)).thenReturn(new User());
        when(userService.convertToDTO(any(User.class))).thenReturn(userDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).getUserById(1);
    }

    @Test
    void testUpdateUserRole_success() throws Exception {
        // Arrange
        when(userService.updateUserRole(1, "ADMIN")).thenReturn("User role updated successfully");

        // Act & Assert
        mockMvc.perform(put("/api/users/1/role/ADMIN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User role updated successfully"));

        verify(userService, times(1)).updateUserRole(1, "ADMIN");
    }

    @Test
    void testDeleteUser_success() throws Exception {
        // Arrange
        when(userService.deleteUser(1)).thenReturn("User deleted successfully");

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    void testGetFindByRole_success() throws Exception {
        // Arrange
        UserDTO user1 = new UserDTO("Alice", "alice@example.com", "ADMIN", LocalDateTime.now());
        List<UserDTO> userDTOS = List.of(user1);

        when(userService.getFindByRole(anyList(), eq("ADMIN"))).thenReturn(userDTOS);

        // Act & Assert
        mockMvc.perform(get("/api/users/role/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));

        verify(userService, times(1)).getFindByRole(anyList(), eq("ADMIN"));
    }

    @Test
    void testGetFindByRole_noUsersFound() throws Exception {
        // Arrange
        when(userService.getFindByRole(anyList(), eq("MODERATOR"))).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/users/role/MODERATOR"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No users found with role: MODERATOR"));

        verify(userService, times(1)).getFindByRole(anyList(), eq("MODERATOR"));
    }

    @Test
    void testUpdateUser_userNotFound() throws Exception {
        // Arrange
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setUserName("NewUserName");
        request.setEmail("newemail@example.com");

        doThrow(new EntityNotFoundException("User not found with ID: 1"))
                .when(userService).updateUser(eq(1), any(UpdateUserRequestDTO.class));

        // Act & Assert
        mockMvc.perform(put("/api/users/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userName": "NewUserName",
                                "email": "newemail@example.com"
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("User not found with ID: 1"));

        verify(userService, times(1)).updateUser(eq(1), any(UpdateUserRequestDTO.class));
    }

    @Test
    void testGetAllUsers_noUsersFound() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]")); // Liste vide

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById_userNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(1))
                .thenThrow(new EntityNotFoundException("User not found with ID: 1"));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("User not found with ID: 1"));

        verify(userService, times(1)).getUserById(1);
    }

    @Test
    void testUpdateUserRole_roleNotFound() throws Exception {
        // Arrange
        when(userService.updateUserRole(1, "MODERATOR"))
                .thenThrow(new EntityNotFoundException("Role not found with name: MODERATOR"));

        // Act & Assert
        mockMvc.perform(put("/api/users/1/role/MODERATOR")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("Role not found with name: MODERATOR"));

        verify(userService, times(1)).updateUserRole(1, "MODERATOR");
    }

    @Test
    void testDeleteUser_userNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("User not found with ID: 1"))
                .when(userService).deleteUser(1);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details").value("User not found with ID: 1"));

        verify(userService, times(1)).deleteUser(1);
    }

}