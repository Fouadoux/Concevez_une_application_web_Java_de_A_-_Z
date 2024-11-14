package com.paymybuddy.app.controller.rest;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.UserRelationService;
import com.paymybuddy.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public ResponseEntity<String> addRelation(@AuthenticationPrincipal UserDetails userDetails, @RequestParam int userId, @RequestParam String email) {
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
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRelation(@AuthenticationPrincipal UserDetails userDetails,@RequestParam int userId, @RequestParam int userRelationId) {
        String result = userRelationService.deleteRelation(userId, userRelationId);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves all relations for the given user.
     *
     * @param userId The ID of the user whose relations are to be retrieved
     * @return A list of user relations
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<RelatedUserDTO>> getAllRelations(@AuthenticationPrincipal UserDetails userDetails,@PathVariable int userId) {
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
    @PreAuthorize("isAuthenticated()")    @GetMapping("/check")
    public ResponseEntity<Boolean> checkRelation(@AuthenticationPrincipal UserDetails userDetails,@RequestParam int userId, @RequestParam int userRelationId) {
        boolean relationExists = userRelationService.checkRelation(userId, userRelationId);
        return ResponseEntity.ok(relationExists);
    }
}
