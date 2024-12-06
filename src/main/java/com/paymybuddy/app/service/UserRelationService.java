package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.exception.*;
import com.paymybuddy.app.repository.UserRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service for managing user relations, including adding, deleting, and retrieving related users.
 */
@Slf4j
@Service
public class UserRelationService {

    private final UserRelationRepository userRelationRepository;
    private final UserService userService;

    public UserRelationService(UserRelationRepository userRelationRepository, UserService userService) {
        this.userRelationRepository = userRelationRepository;
        this.userService = userService;
    }

    /**
     * Adds a new relation between the given user and the user identified by the email.
     *
     * @param user  The user who wants to add a relation.
     * @param email The email of the user to be added.
     * @return A success message if the relation is added successfully.
     * @throws EntityNotFoundException    If the user with the given email is not found.
     * @throws IllegalArgumentException   If the relation already exists or the email is invalid.
     * @throws EntitySaveException        If there is an error while saving the new relation.
     */
    public String addRelation(User user, String email) {
        log.info("Adding a new relation for user ID: {} with user email: {}", user.getId(), email);

        if (!EmailValidationService.isValidEmail(email)) {
            log.error("Invalid email format: {}", email);
            throw new InvalidEmailException("Invalid email format: " + email);
        }

        User userToAdd = userService.getUserByEmail(email);
        if (userToAdd == null) {
            log.error("User with email {} not found", email);
            throw new EntityNotFoundException("User with email not found: " + email);
        }

        // Check if the user is marked as deleted in the system
        if (userToAdd.isDeleted()) {
            log.warn("The user with ID {} does not exist or has been marked as deleted.", userToAdd.getId());
            throw new EntityNotFoundException("The specified user does not exist or has been deleted.");
        }

        // Check if the user is trying to add himself
        if (user.getEmail().equals(userToAdd.getEmail())) {
            log.warn("User with ID {} tried to add their own email", userToAdd.getId());
            throw new EmailAlreadyExistsException("You can't add your own email");
        }

        if (userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()).isPresent()) {
            log.error("Relation already exists between user ID: {} and user email: {}", user.getId(), email);
            throw new IllegalArgumentException("Relation already exists between user ID: " + user.getId() + " and user email: " + email);
        }

        UserRelation newRelation = new UserRelation();
        newRelation.setUserId(user.getId());
        newRelation.setUserRelationId(userToAdd.getId());
        newRelation.setStatus(true);
        newRelation.setCreatedAt(LocalDateTime.now());

        try {
            userRelationRepository.save(newRelation);
            log.info("Relation successfully added between user ID: {} and user email: {}", user.getId(), email);
        } catch (Exception e) {
            log.error("Failed to save the new relation", e);
            throw new EntitySaveException("Failed to save the new relation", e);
        }

        user.addUserRelation(newRelation);
        return "Relation successfully added between user ID: " + user.getId() + " and user email: " + email;
    }

    /**
     * Deletes a relation between a user and a related user by their IDs.
     *
     * @param userId         The ID of the user.
     * @param userRelationId The ID of the related user.
     * @return A success message if the relation is deleted successfully.
     * @throws EntityNotFoundException If the relation is not found.
     * @throws EntityDeleteException   If there is an error while deleting the relation.
     */
    public String deleteRelation(int userId, int userRelationId) {
        log.info("Deleting relation between user ID: {} and related user ID: {}", userId, userRelationId);

        UserRelation userRelation = userRelationRepository.findByUserIdAndUserRelationId(userId, userRelationId)
                .orElseThrow(() -> {
                    log.error("Relation not found between user ID: {} and related user ID: {}", userId, userRelationId);
                    return new EntityNotFoundException("Relation not found");
                });

        User user = userService.getUserById(userId);

        try {
            userRelationRepository.delete(userRelation);
            log.info("Relation successfully deleted between user ID: {} and related user ID: {}", userId, userRelationId);
        } catch (Exception e) {
            log.error("Failed to delete the relation", e);
            throw new EntityDeleteException("Failed to delete the relation", e);
        }

        user.removeUserRelation(userRelation);
        return "Relation successfully deleted between user ID: " + userId + " and related user ID: " + userRelationId;
    }

    /**
     * Retrieves all related users for a given user.
     *
     * @param user The user whose related users are to be retrieved.
     * @return A list of related users as DTOs.
     */
    public List<RelatedUserDTO> getAllRelatedUsers(User user) {
        log.info("Retrieving all related users for user ID: {}", user.getId());

        List<UserRelation> relationsAsUser = user.getUserRelations();
        List<UserRelation> relationsAsRelatedUser = user.getRelatedUserRelations();

        List<RelatedUserDTO> relatedUsers = new ArrayList<>();
        for (UserRelation relation : relationsAsUser) {
            relatedUsers.add(new RelatedUserDTO(
                    relation.getRelatedUser().getId(),
                    relation.getRelatedUser().getUserName()
            ));
        }

        for (UserRelation relation : relationsAsRelatedUser) {
            relatedUsers.add(new RelatedUserDTO(
                    relation.getUser().getId(),
                    relation.getUser().getUserName()
            ));
        }

        Iterator<RelatedUserDTO> iterator = relatedUsers.iterator();
        while (iterator.hasNext()) {
            RelatedUserDTO dto = iterator.next();
            User related = userService.getUserById(dto.getId());

            if (related.isDeleted()) {
                iterator.remove();
            }
        }

        log.info("Total related users found for user ID {}: {}", user.getId(), relatedUsers.size());
        return relatedUsers;
    }

    /**
     * Finds related users for a given user ID.
     *
     * @param userId The ID of the user.
     * @return A list of related users as DTOs.
     * @throws EntityNotFoundException If the user is not found.
     */
    public List<RelatedUserDTO> findRelatedUsers(int userId) {
        log.info("Finding related users for user ID: {}", userId);

        User user = userService.getUserById(userId);
        if (user == null) {
            log.error("User with ID {} not found", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        return getAllRelatedUsers(user);
    }

    /**
     * Checks if a relation exists between two users.
     *
     * @param userId         The ID of the user.
     * @param userRelationId The ID of the related user.
     * @return True if the relation exists, false otherwise.
     */
    public boolean checkRelation(int userId, int userRelationId) {
        log.info("Checking relation between user ID: {} and related user ID: {}", userId, userRelationId);

        boolean relationExists = userRelationRepository.findByUserIdAndUserRelationId(userId, userRelationId).isPresent() ||
                userRelationRepository.findByUserIdAndUserRelationId(userRelationId, userId).isPresent();

        log.info("Relation exists between user ID: {} and related user ID: {}: {}", userId, userRelationId, relationExists);
        return relationExists;
    }
}
