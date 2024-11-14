package com.paymybuddy.app.service;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.UserRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        try {
            userRelationRepository.save(newRelation);

            log.info("User relation successfully added between user with ID: {} and user with email: {}", user.getId(), email);
        } catch (Exception e) {
            log.error("Failed to save the new user relation", e);
            throw new EntitySaveException("Failed to save the new user relation", e);
        }
        user.addUserRelation(newRelation);
        //userToAdd.addUserRelation(newRelation);
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
    public List<RelatedUserDTO> getAllRelatedUsers(User user) {
        log.info("Retrieving all related users for user with ID: {}", user.getId());

        // Relations où l'utilisateur est user_id
        List<UserRelation> relationsAsUser = user.getUserRelations();

        // Relations où l'utilisateur est user_relation_id
        List<UserRelation> relationsAsRelatedUser = user.getRelatedUserRelations();

        // Liste des utilisateurs liés
        List<RelatedUserDTO> relatedUsers = new ArrayList<>();

        // Ajouter les relations où l'utilisateur est user_id
        for (UserRelation relation : relationsAsUser) {
            relatedUsers.add(new RelatedUserDTO(
                    relation.getRelatedUser().getId(),
                    relation.getRelatedUser().getUserName()
            ));
        }

        // Ajouter les relations où l'utilisateur est user_relation_id
        for (UserRelation relation : relationsAsRelatedUser) {
            relatedUsers.add(new RelatedUserDTO(
                    relation.getUser().getId(),
                    relation.getUser().getUserName()
            ));
        }

        log.info("Total related users found: {}", relatedUsers.size());
        return relatedUsers;
    }

    public List<RelatedUserDTO> findRelatedUsers(int userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        return getAllRelatedUsers(user);
    }

    /**
     * Checks if a relation exists between two users.
     *
     * @param userId         The ID of the user
     * @param userRelationId The ID of the user to check the relation with
     * @return True if the relation exists, otherwise false
     */
    public boolean checkRelation(int userId, int userRelationId) {
        log.info("Checking relation between user with ID: {} and user relation ID: {}", userId, userRelationId);

        boolean relationExists =userRelationRepository.findByUserIdAndUserRelationId(userId, userRelationId).isPresent() ||
                userRelationRepository.findByUserIdAndUserRelationId(userRelationId,userId).isPresent();

        log.info("Relation exists: {}", relationExists);
        return relationExists;
    }



}
