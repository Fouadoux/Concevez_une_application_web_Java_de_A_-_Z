package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks((this));
    }

    @Test
    public void testCreateRole(){
        Role role=new Role();
        role.setRoleName("TestRole");

        when(roleRepository.findByRoleName(role.getRoleName())).thenReturn(null);

        ResponseEntity<?> response = roleService.createRole(role);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(roleRepository).save(role);
    }

    @Test
    public void testCreateRoleShouldReturnBadRequest_WhenUsernameExists(){
        Role existingRole = new Role();
        existingRole.setRoleName("admin");

        when(roleRepository.findByRoleName("admin")).thenReturn(existingRole);

        Role newRole = new Role();
        newRole.setRoleName("admin");

        ResponseEntity<?> response = roleService.createRole(newRole);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(roleRepository, never()).save(any(Role.class));

    }

    @Test
    public void testAllRole(){
        Role role1 = new Role();
        role1.setRoleName("admin");
        Role role2 = new Role();
        role2.setRoleName("User");
        Role role3 = new Role();
        role1.setRoleName("Moderator");
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1,role2,role3));

        List<Role> roleList = roleService.getAllRole();

        assertEquals(3,roleList.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void testGetRoleByIdSuccess(){
        Role role = new Role();
        role.setId(1);
        role.setRoleName("Test");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        ResponseEntity<Role> response = roleService.getRoleById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testGetRoleById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Role> response = roleService.getRoleById(1);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }



    @Test
    public void TestDeleteRoleSuccess(){
        // Arrange
        Role role= new Role();
        role.setRoleName("user");
        role.setId(1);

        // Simuler la présence d'un role dans la base de données
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        // Act
        ResponseEntity<?> response=roleService.deleteRole(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Role deleted successfully", response.getBody());

        // Vérifier que la méthode delete a bien été appelée une fois
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    public void testDeleteUser_ShouldReturnNotFound_WhenUserDoesNotExist(){
        // Arrange
        // Simuler l'absence d'un role dans la base de données
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response=roleService.deleteRole(1);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(roleRepository, never()).delete(any(Role.class)); // S'assurer que la méthode delete n'est jamais appelée

    }

}