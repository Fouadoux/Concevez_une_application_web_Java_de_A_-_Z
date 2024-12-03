package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.UserRelationRepository;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        user.setUserName("User 1");

        userToAdd = new User();
        userToAdd.setId(2);
        userToAdd.setEmail("user2@example.com");
        userToAdd.setUserName("User 2");

        userRelation = new UserRelation();
        userRelation.setUserId(user.getId());
        userRelation.setUserRelationId(userToAdd.getId());
        userRelation.setStatus(true);
        userRelation.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testAddRelation_ShouldAddRelationSuccessfully() {
        log.info("Testing addRelation method for successful addition of relation");
        when(userService.getUserByEmail("user2@example.com")).thenReturn(userToAdd);
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.empty());

        String result = userRelationService.addRelation(user, "user2@example.com");

        assertEquals("Relation successfully added between user ID: 1 and user email: user2@example.com", result);
        verify(userRelationRepository, times(1)).save(any(UserRelation.class));
        log.info("Relation added successfully between user with ID: {} and user with email: {}", user.getId(), userToAdd.getEmail());
    }

    @Test
    void testAddRelation_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
        log.info("Testing addRelation method for user not found scenario");
        when(userService.getUserByEmail("user2@example.com")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userRelationService.addRelation(user, "user2@example.com"));
        log.warn("EntityNotFoundException thrown as expected for email: user2@example.com");
    }

    @Test
    void testAddRelation_ShouldThrowIllegalArgumentException_WhenRelationAlreadyExists() {
        log.info("Testing addRelation method for existing relation scenario");
        when(userService.getUserByEmail("user2@example.com")).thenReturn(userToAdd);
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.of(userRelation));

        assertThrows(IllegalArgumentException.class, () -> userRelationService.addRelation(user, "user2@example.com"));
        log.warn("IllegalArgumentException thrown as expected for existing relation between user with ID: {} and user with email: {}", user.getId(), userToAdd.getEmail());
    }

    @Test
    void testDeleteRelation_ShouldDeleteRelationSuccessfully() {
        log.info("Testing deleteRelation method for successful deletion of relation");
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.of(userRelation));
        when(userService.getUserById(user.getId())).thenReturn(user);

        String result = userRelationService.deleteRelation(user.getId(), userToAdd.getId());

        assertEquals("Relation successfully deleted between user ID: 1 and related user ID: 2", result);
        verify(userRelationRepository, times(1)).delete(userRelation);
        log.info("Relation deleted successfully between user with ID: {} and user relation ID: {}", user.getId(), userToAdd.getId());
    }

    @Test
    void testDeleteRelation_ShouldThrowEntityNotFoundException_WhenRelationNotFound() {
        log.info("Testing deleteRelation method for relation not found scenario");
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userRelationService.deleteRelation(user.getId(), userToAdd.getId()));
        log.warn("EntityNotFoundException thrown as expected for user with ID: {} and relation ID: {}", user.getId(), userToAdd.getId());
    }

    @Test
    void testCheckRelation_ShouldReturnCorrectStatus() {
        log.info("Testing checkRelation method for existing relation");
        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.of(userRelation));

        assertTrue(userRelationService.checkRelation(user.getId(), userToAdd.getId()));
        log.info("Relation exists between user with ID: {} and relation ID: {}", user.getId(), userToAdd.getId());

        when(userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()))
                .thenReturn(Optional.empty());

        assertFalse(userRelationService.checkRelation(user.getId(), userToAdd.getId()));
        log.info("No relation exists between user with ID: {} and relation ID: {}", user.getId(), userToAdd.getId());
    }

    @Test
    void getAllRelatedUsers_success() {
        log.info("Testing getAllRelatedUsers method for successful retrieval");
        user.setUserRelations(List.of(createRelation(user, userToAdd)));
        userToAdd.setUserRelations(List.of(createRelation(userToAdd, user)));



        List<RelatedUserDTO> relatedUsers = userRelationService.getAllRelatedUsers(user);

        assertEquals(1, relatedUsers.size());
        assertEquals(2, relatedUsers.get(0).getId());
        assertEquals("User 2", relatedUsers.get(0).getName());
        log.info("Successfully retrieved all related users for user with ID: {}", user.getId());
    }

    @Test
    void findRelatedUsers_success() {
        log.info("Testing findRelatedUsers method for successful retrieval");
        user.setUserRelations(List.of(createRelation(user, userToAdd)));
        when(userService.getUserById(user.getId())).thenReturn(user);

        List<RelatedUserDTO> relatedUsers = userRelationService.findRelatedUsers(user.getId());

        assertEquals(1, relatedUsers.size());
        assertEquals(2, relatedUsers.get(0).getId());
        assertEquals("User 2", relatedUsers.get(0).getName());
        log.info("Successfully retrieved related users for user with ID: {}", user.getId());
    }

    @Test
    void findRelatedUsers_userNotFound() {
        log.info("Testing findRelatedUsers method for user not found scenario");
        when(userService.getUserById(user.getId())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userRelationService.findRelatedUsers(user.getId()));
        log.warn("EntityNotFoundException thrown as expected for user with ID: {}", user.getId());
    }

    private UserRelation createRelation(User user, User relatedUser) {
        UserRelation relation = new UserRelation();
        relation.setUser(user);
        relation.setRelatedUser(relatedUser);
        return relation;
    }
}
