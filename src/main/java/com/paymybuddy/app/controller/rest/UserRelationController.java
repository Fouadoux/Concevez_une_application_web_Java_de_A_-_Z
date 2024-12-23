package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidEmailException;
import com.paymybuddy.app.service.UserRelationService;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relation")
@RequiredArgsConstructor
public class UserRelationController {

    private final UserRelationService userRelationService;
    private final UserService userService;

    /**
     * Adds a relation between the given user and the user identified by the provided email.
     *
     * @param userId The ID of the user who wants to add a relation
     * @param email  The email of the user to add as a relation
     * @return A success message if the relation is added successfully
     */

    @PostMapping("/add")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<String> addRelation( @RequestParam int userId, @RequestParam String email) {
        User user = userService.getUserById(userId);
        String result = userRelationService.addRelation(user, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Deletes a relation between the given user and user relation ID.
     *
     * @param userId         The ID of the user
     * @param userRelationId The ID of the user to remove from relations
     * @return A success message if the relation is deleted successfully
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRelation(@RequestParam int userId, @RequestParam int userRelationId) {
        String result = userRelationService.deleteRelation(userId, userRelationId);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all relations for the given user.
     *
     * @param userId The ID of the user whose relations are to be retrieved
     * @return A list of user relations
     */


    @GetMapping("/all/{userId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<List<RelatedUserDTO>> getAllRelations(@PathVariable int userId) {
        List<RelatedUserDTO> relatedUsers = userRelationService.findRelatedUsers(userId);
        return ResponseEntity.ok(relatedUsers);
    }


    /**
     * Checks if a relation exists between two users.
     *
     * @param userId         The ID of the user
     * @param userRelationId The ID of the user to check the relation with
     * @return A boolean indicating whether the relation exists or not
     */
    @PreAuthorize("#userId == principal.id")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkRelation(@RequestParam int userId, @RequestParam int userRelationId) {
        boolean relationExists = userRelationService.checkRelation(userId, userRelationId);
        return ResponseEntity.ok(relationExists);
    }
}
