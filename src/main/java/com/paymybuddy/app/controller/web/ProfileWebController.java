package com.paymybuddy.app.controller.web;

import com.paymybuddy.app.dto.UpdateUserRequestDTO;
import com.paymybuddy.app.exception.EntityAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidEmailException;
import com.paymybuddy.app.security.CustomUserDetails;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class ProfileWebController {


    private final UserService userService;

    public ProfileWebController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/profile")
    public String getUserProfile(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Attempt to access without a logged-in user..");
            return "redirect:/login";
        }

        int userId=userDetails.getId();

        model.addAttribute("userId", userId);
        return "profilePage";
    }

    @PostMapping("/update")
    @PreAuthorize("#userId == principal.id")
    public String updateUser(
            @RequestParam int userId,
            @ModelAttribute UpdateUserRequestDTO request,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Updating user with ID: {} with new information: {}", userId, request);
        try {
            userService.updateUser(userId, request);
            log.info("User with ID: {} updated successfully", userId);
            redirectAttributes.addFlashAttribute("successMessage", "Vos informations ont été mises à jour avec succès !");
        } catch (InvalidEmailException | EntityAlreadyExistsException | EntityNotFoundException ex) {
            log.error("Error updating user with ID: {}: {}", userId, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/profile";
    }


}
