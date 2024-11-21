package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.RoleAlreadyExistsException;
import com.paymybuddy.app.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    UserService userService;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testCreateRole_Success() {
        Role role = new Role();
        role.setRoleName("TestRole");

        when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(Optional.empty());
        when(roleRepository.save(role)).thenReturn(role);

        Role createdRole = roleService.createRole(role);

        assertEquals("TestRole", createdRole.getRoleName());
        verify(roleRepository).save(role);
    }

    @Test
    void testCreateRole_ShouldThrowException_WhenRoleNameExists() {
        Role existingRole = new Role();
        existingRole.setRoleName("admin");

        when(roleRepository.findByRoleName("admin")).thenReturn(Optional.of(existingRole));

        Role newRole = new Role();
        newRole.setRoleName("admin");

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.createRole(newRole));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testGetAllRoles() {
        Role role1 = new Role();
        role1.setRoleName("admin");
        Role role2 = new Role();
        role2.setRoleName("User");
        Role role3 = new Role();
        role3.setRoleName("Moderator");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2, role3));

        List<Role> roleList = roleService.getAllRoles();

        assertEquals(3, roleList.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetRoleById_Success() {
        Role role = new Role();
        role.setId(1);
        role.setRoleName("Test");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(1);

        assertNotNull(foundRole);
        assertEquals("Test", foundRole.getRoleName());
    }

    @Test
    void testGetRoleById_ShouldThrowException_WhenRoleNotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.getRoleById(1));
    }

    @Test
    void testUpdateRole_Success() {
        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setRoleName("OldName");

        Role updatedRole = new Role();
        updatedRole.setRoleName("NewName");

        when(roleRepository.findById(1)).thenReturn(Optional.of(existingRole));

        String updateMessage = roleService.updateRole(1, updatedRole);

        assertEquals("Role updated successfully", updateMessage);
        assertEquals("NewName", existingRole.getRoleName());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void testUpdateRole_ShouldThrowException_WhenRoleNotFound() {
        Role updatedRole = new Role();
        updatedRole.setRoleName("NewName");

        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.updateRole(1, updatedRole));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDeleteRole_Success() {
        Role role = new Role();
        role.setId(1);
        role.setRoleName("user");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        roleService.deleteRole(1);

        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void testDeleteRole_ShouldThrowException_WhenRoleNotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.deleteRole(1));
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    void testGetTransactionLimitForUser() {
        Role role = new Role();
        role.setId(1);
        role.setRoleName("user");
        role.setDailyLimit(200000);

        User user = new User();
        user.setId(1);
        user.setUserName("test");
        user.setRole(role);

        when(userService.getUserById(1)).thenReturn(user);

        long limit = roleService.getTransactionLimitForUser(1);

        assertEquals(limit, role.getDailyLimit());
    }

    @Test
    void changeDailyLimit_success() {
        // Arrange
        String roleName = "user";
        long newDailyLimit = 300000;

        Role role = new Role();
        role.setId(1);
        role.setRoleName(roleName);
        role.setDailyLimit(200000);

        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(role));

        // Act
        roleService.changeDailyLimit(roleName, newDailyLimit);

        // Assert
        assertEquals(newDailyLimit, role.getDailyLimit());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void changeDailyLimit_roleNotFound() {
        // Arrange
        String roleName = "nonexistent";
        long newDailyLimit = 300000;

        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                roleService.changeDailyLimit(roleName, newDailyLimit));
    }

    @Test
    void changeDailyLimit_invalidRoleName() {
        // Arrange
        String roleName = " ";
        long newDailyLimit = 300000;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                roleService.changeDailyLimit(roleName, newDailyLimit));
    }

    @Test
    void changeDailyLimit_invalidDailyLimit() {
        // Arrange
        String roleName = "user";
        long newDailyLimit = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                roleService.changeDailyLimit(roleName, newDailyLimit));
    }

    @Test
    void changeDailyLimit_saveException() {
        // Arrange
        String roleName = "user";
        long newDailyLimit = 300000;

        Role role = new Role();
        role.setId(1);
        role.setRoleName(roleName);
        role.setDailyLimit(200000);

        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(role));
        doThrow(new RuntimeException()).when(roleRepository).save(role);

        // Act & Assert
        assertThrows(EntitySaveException.class, () ->
                roleService.changeDailyLimit(roleName, newDailyLimit));
    }


}
