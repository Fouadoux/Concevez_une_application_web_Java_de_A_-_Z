package com.paymybuddy.app.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.RoleAlreadyExistsException;
import com.paymybuddy.app.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RoleController.class)
@WithMockUser(username = "testUser", roles = {"ADMIN"})
@ActiveProfiles("test")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules(); // Support pour les formats JSON
    }

    @Test
    void testCreateRole_Success() throws Exception {
        Role role = new Role();
        role.setId(1);
        role.setRoleName("ADMIN");
        role.setDailyLimit(1000L);

        when(roleService.createRole(any(Role.class))).thenReturn(role);

        String jsonContent = objectMapper.writeValueAsString(role);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.roleName").value(role.getRoleName()))
                .andExpect(jsonPath("$.dailyLimit").value(role.getDailyLimit()));
    }

    @Test
    void testCreateRole_RoleAlreadyExists() throws Exception {
        Role role = new Role();
        role.setRoleName("ADMIN");

        when(roleService.createRole(any(Role.class)))
                .thenThrow(new RoleAlreadyExistsException("Role already exists"));

        String jsonContent = objectMapper.writeValueAsString(role);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Role already exists"));
    }

    @Test
    void testGetAllRoles_Success() throws Exception {
        Role role1 = new Role();
        role1.setId(1);
        role1.setRoleName("ADMIN");
        role1.setDailyLimit(1000L);

        Role role2 = new Role();
        role2.setId(2);
        role2.setRoleName("USER");
        role2.setDailyLimit(500L);

        when(roleService.getAllRoles()).thenReturn(List.of(role1, role2));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roleName").value("ADMIN"))
                .andExpect(jsonPath("$[1].roleName").value("USER"));
    }

    @Test
    void testGetRoleById_Success() throws Exception {
        int roleId = 1;
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName("ADMIN");
        role.setDailyLimit(1000L);

        when(roleService.getRoleById(roleId)).thenReturn(role);

        mockMvc.perform(get("/api/roles/{roleId}", roleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.roleName").value(role.getRoleName()))
                .andExpect(jsonPath("$.dailyLimit").value(role.getDailyLimit()));
    }

    @Test
    void testGetRoleById_NotFound() throws Exception {
        int roleId = 1;

        when(roleService.getRoleById(roleId))
                .thenThrow(new EntityNotFoundException("Role not found with ID: " + roleId));

        mockMvc.perform(get("/api/roles/{roleId}", roleId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Role not found with ID: " + roleId));
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        int roleId = 1;
        Role role = new Role();
        role.setRoleName("UPDATED_ROLE");

        when(roleService.updateRole(eq(roleId), any(Role.class))).thenReturn("Role updated successfully");

        String jsonContent = objectMapper.writeValueAsString(role);

        mockMvc.perform(put("/api/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Role updated successfully"));
    }

    @Test
    void testUpdateRole_NotFound() throws Exception {
        int roleId = 1;
        Role role = new Role();
        role.setRoleName("UPDATED_ROLE");

        when(roleService.updateRole(eq(roleId), any(Role.class)))
                .thenThrow(new EntityNotFoundException("Role not found with ID: " + roleId));

        String jsonContent = objectMapper.writeValueAsString(role);

        mockMvc.perform(put("/api/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Role not found with ID: " + roleId));
    }

    @Test
    void testDeleteRole_Success() throws Exception {
        int roleId = 1;

        doNothing().when(roleService).deleteRole(roleId);

        mockMvc.perform(delete("/api/roles/{roleId}", roleId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Role deleted successfully"));
    }

    @Test
    void testDeleteRole_NotFound() throws Exception {
        int roleId = 1;

        doThrow(new EntityNotFoundException("Role not found with ID: " + roleId))
                .when(roleService).deleteRole(roleId);

        mockMvc.perform(delete("/api/roles/{roleId}", roleId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Role not found with ID: " + roleId));
    }

    @Test
    void testUpdateDailyLimit_Success() throws Exception {
        String roleName = "ADMIN";
        long dailyLimit = 2000L;

        doNothing().when(roleService).changeDailyLimit(roleName, dailyLimit);

        mockMvc.perform(put("/api/roles/dailyLimit/role/{roleName}/limit/{dailyLimit}", roleName, dailyLimit)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Daily limit updated successfully"));
    }

    @Test
    void testUpdateDailyLimit_RoleNotFound() throws Exception {
        String roleName = "ADMIN";
        long dailyLimit = 2000L;

        doThrow(new EntityNotFoundException("Role not found: " + roleName))
                .when(roleService).changeDailyLimit(roleName, dailyLimit);

        mockMvc.perform(put("/api/roles/dailyLimit/role/{roleName}/limit/{dailyLimit}", roleName, dailyLimit)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details").value("Role not found: " + roleName));
    }
}
