package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.UserRelationDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.UserRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param user  The user who wants to add a relation
     * @param email The email of the user to be added
     * @return A success message if the relation is added successfully
     * @throws EntityNotFoundException If the user with the given email is not found
     * @throws IllegalArgumentException If the relation already exists
     * @throws EntitySaveException If there is an error while saving the new relation
     */
    public String addRelation(User user, String email) {
        log.info("Adding a new relation for user with ID: {} and user with email: {}", user.getId(), email);

        User userToAdd = userService.findByEmail(email);
        if (userToAdd == null) {
            log.error("User with email {} not found", email);
            throw new EntityNotFoundException("User with email not found: " + email);
        }

        if (userRelationRepository.findByUserIdAndUserRelationId(user.getId(), userToAdd.getId()).isPresent()) {
            log.error("Relation already exists between users with ID: {} and user with email: {}", user.getId(), email);
            throw new IllegalArgumentException("Relation already exists between users with ID: " + user.getId() + " and user with email: " + email);
        }

        UserRelation newRelation = new UserRelation();
        newRelation.setUserId(user.getId());
        newRelation.setUserRelationId(userToAdd.getId());
        newRelation.setStatus(true);
        newRelation.setCreatedAt(LocalDateTime.now());

        UserRelation newRelation2 = new UserRelation();
        newRelation2.setUserId(userToAdd.getId());
        newRelation2.setUserRelationId(user.getId());
        newRelation2.setStatus(true);
        newRelation2.setCreatedAt(LocalDateTime.now());

      //

        try {
            userRelationRepository.save(newRelation);
            userRelationRepository.save(newRelation2);
            log.info("User relation successfully added between user with ID: {} and user with email: {}", user.getId(), email);
        } catch (Exception e) {
            log.error("Failed to save the new user relation", e);
            throw new EntitySaveException("Failed to save the new user relation", e);
        }
        user.addUserRelation(newRelation);
        userToAdd.addUserRelation(newRelation2);
        return "User relation successfully added between user with ID: " + user.getId() + " and user with email: " + email;
    }

    /**
     * Deletes a relation between the given user and user relation ID.
     *
     * @param userId         The ID of the user
     * @param userRelationId The ID of the user to remove from relations
     * @return A success message if the relation is deleted successfully
     * @throws EntityNotFoundException If the relation is not found
     * @throws EntityDeleteException If there is an error while deleting the relation
     */
    public String deleteRelation(int userId, int userRelationId) {
        log.info("Deleting relation between user with ID: {} and user relation ID: {}", userId, userRelationId);

        UserRelation userRelation = userRelationRepository.findByUserIdAndUserRelationId(userId, userRelationId)
                .orElseThrow(() -> {
                    log.error("Relation not found between user with ID: {} and user relation ID: {}", userId, userRelationId);
                    return new EntityNotFoundException("Relation not found");
                });

        User user = userService.getUserById(userId);

        try {
            userRelationRepository.delete(userRelation);
            log.info("User relation successfully deleted between user with ID: {} and user relation ID: {}", userId, userRelationId);
        } catch (Exception e) {
            log.error("Failed to delete user relation", e);
            throw new EntityDeleteException("Failed to delete user relation", e);
        }

        user.removeUserRelation(userRelation);
        return "User relation successfully deleted between user with ID: " + userId + " and user relation ID: " + userRelationId;
    }

    /**
     * Retrieves all relations for the given user.
     *
     * @param user The user whose relations are to be retrieved
     * @return An unmodifiable list of user relations
     */
    public List<UserRelation> getAllRelations(User user) {
        log.info("Retrieving all relations for user with ID: {}", user.getId());
        return Collections.unmodifiableList(user.getUserRelations());
    }

    /**
     * Checks if a relation exists between two users.
     *
     * @param userId         The ID of the user
     * @param userRelationId The ID of the user to check the relation with
     * @return True if the relation exists, otherwise false
     */

    //modifier pour faire un double traitemant
    public boolean checkRelation(int userId, int userRelationId) {
        log.info("Checking relation between user with ID: {} and user relation ID: {}", userId, userRelationId);
        return userRelationRepository.findByUserIdAndUserRelationId(userId, userRelationId).isPresent();
    }



    public UserRelationDTO convertToDTO(UserRelation userRelation) {
        UserRelationDTO dto = new UserRelationDTO();
        dto.setUserId(userRelation.getUserId());
        dto.setUserRelationId(userRelation.getUserRelationId());
        dto.setUserNameRelation(userService.findUsernameByUserId(userRelation.getUserRelationId()));
        dto.setStatus(userRelation.isStatus());
        dto.setCreatedAt(userRelation.getCreatedAt());
        return dto;
    }

    public List<UserRelationDTO> convertToDTOList(List<UserRelation> userRelations) {
        return userRelations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
