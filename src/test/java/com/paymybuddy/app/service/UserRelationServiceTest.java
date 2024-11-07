package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.UserRelationDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.UserRelationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRelationServiceTest {

    @Mock
    private UserRelationRepository userRelationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRelationService userRelationService;

    private User user;
    private User userToAdd;
    private UserRelation userRelation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("user1@example.com");

        userToAdd = new User();
        userToAdd.setId(2);
        userToAdd.setEmail("user2@example.com");

        userRelation = new UserRelation();
        userRelation.setUserId(user.getId());
        userRelation.setUserRelationId(userToAdd.getId());
        userRelation.setStatus(true);
        userRelation.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testAddRelation_ShouldAddRelationSuccessfully() {
        when(userService.findByEmail("user2@example.com")).thenReturn(userToAdd);
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.empty());

        String result = userRelationService.addRelation(user, "user2@example.com");

        assertEquals("User relation successfully added between user with ID: 1 and user with email: user2@example.com", result);
        verify(userRelationRepository, times(2)).save(any(UserRelation.class));
    }

    @Test
    void testAddRelation_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
        when(userService.findByEmail("user2@example.com")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            userRelationService.addRelation(user, "user2@example.com");
        });
    }

    @Test
    void testAddRelation_ShouldThrowIllegalArgumentException_WhenRelationAlreadyExists() {
        when(userService.findByEmail("user2@example.com")).thenReturn(userToAdd);
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.of(userRelation));

        assertThrows(IllegalArgumentException.class, () -> {
            userRelationService.addRelation(user, "user2@example.com");
        });
    }

    @Test
    void testDeleteRelation_ShouldDeleteRelationSuccessfully() {
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.of(userRelation));
        when(userService.getUserById(user.getId())).thenReturn(user);

        String result = userRelationService.deleteRelation(user.getId(), userToAdd.getId());

        assertEquals("User relation successfully deleted between user with ID: 1 and user relation ID: 2", result);
        verify(userRelationRepository, times(1)).delete(userRelation);
    }

    @Test
    void testDeleteRelation_ShouldThrowEntityNotFoundException_WhenRelationNotFound() {
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userRelationService.deleteRelation(user.getId(), userToAdd.getId());
        });
    }

    @Test
    void testGetAllRelations_ShouldReturnAllRelations() {
        user.addUserRelation(userRelation);

        List<UserRelation> relations = userRelationService.getAllRelations(user);

        assertEquals(1, relations.size());
        assertEquals(userRelation, relations.get(0));
    }

    @Test
    void testCheckRelation_ShouldReturnTrue_WhenRelationExists() {
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.of(userRelation));

        assertTrue(userRelationService.checkRelation(user.getId(), userToAdd.getId()));
    }

    @Test
    void testCheckRelation_ShouldReturnFalse_WhenRelationDoesNotExist() {
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.empty());

        assertFalse(userRelationService.checkRelation(user.getId(), userToAdd.getId()));
    }

    @Test
    void testConvertToDTO_ShouldConvertUserRelationToDTO() {
        UserRelationDTO dto = userRelationService.convertToDTO(userRelation);

        assertEquals(userRelation.getUserId(), dto.getUserId());
        assertEquals(userRelation.getUserRelationId(), dto.getUserRelationId());
        assertEquals(userRelation.isStatus(), dto.isStatus());
        assertEquals(userRelation.getCreatedAt(), dto.getCreatedAt());
    }
}
