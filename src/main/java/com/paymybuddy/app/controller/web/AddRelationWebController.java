package com.paymybuddy.app.controller.web;

import com.paymybuddy.app.exception.EmailAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.security.CustomUserDetails;
import com.paymybuddy.app.service.UserRelationService;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class AddRelationWebController {

    private final UserRelationService userRelationService;
    private final UserService userService;

    public AddRelationWebController(UserRelationService userRelationService, UserService userService) {
        this.userRelationService=userRelationService;
        this.userService = userService;
    }

    @GetMapping("/addRelation")
    public String getAddRelation(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Tentative d'accès à sans utilisateur connecté.");
            return "redirect:/login";
        }

        int userId=userDetails.getId();

        model.addAttribute("userId", userId);
        return "addRelationPage";
    }

    @PostMapping("/add")
    @PreAuthorize("#userId == principal.id")
    public String addRelation(@RequestParam int userId, @RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            String result = userRelationService.addRelation(userService.getUserById(userId), email);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (IllegalArgumentException | EmailAlreadyExistsException | EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/addRelation";
    }

}
