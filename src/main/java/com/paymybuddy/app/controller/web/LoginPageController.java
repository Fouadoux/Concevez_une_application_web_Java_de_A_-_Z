package com.paymybuddy.app.controller.web;

import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginPageController {

    private final UserService userService;

    public LoginPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginPage"; // Va chercher "loginPage.html" dans "templates"
    }

    @GetMapping("/connected")
    public String connectedPage() {
        return "connectedPage"; // Nom du fichier HTML sans l'extension
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "registerPage"; // Va chercher "loginPage.html" dans "templates"
    }
    @GetMapping("/transaction")
    public String showTransactionPage() {
        return "TransactionPage"; // Va chercher "loginPage.html" dans "templates"
    }
    @GetMapping("/profil")
    public String getUserProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Tentative d'accès à /profil sans utilisateur connecté.");
            return "redirect:/login";
        }

        log.info("Utilisateur connecté : {}", userDetails.getUsername());
        User currentUser = userService.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            log.error("Utilisateur non trouvé pour l'email : {}", userDetails.getUsername());
            return "redirect:/login";
        }

        model.addAttribute("userId", currentUser.getId());
        return "profilPage";
    }

    @GetMapping("/addRelation")
    public String getAddRelation(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Tentative d'accès à /profil sans utilisateur connecté.");
            return "redirect:/addRelation";
        }

        log.info("Utilisateur connecté : {}", userDetails.getUsername());
        User currentUser = userService.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            log.error("Utilisateur non trouvé pour l'email : {}", userDetails.getUsername());
            return "redirect:/addRelation";
        }

        model.addAttribute("userId", currentUser.getId());
        return "addRelationPage";
    }


}

