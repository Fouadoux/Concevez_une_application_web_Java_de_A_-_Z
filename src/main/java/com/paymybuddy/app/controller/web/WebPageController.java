package com.paymybuddy.app.controller.web;

import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;

import com.paymybuddy.app.security.CustomUserDetails;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserRelationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class WebPageController {

    private final UserRelationService userRelationService;
    private final TransactionService transactionService;

    public WebPageController(UserRelationService userRelationService, TransactionService transactionService) {
        this.userRelationService=userRelationService;
        this.transactionService = transactionService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginPage"; // Va chercher "loginPage.html" dans "templates"
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "registerPage"; // Va chercher "loginPage.html" dans "templates"
    }

    @GetMapping("/transaction")
    public String showTransactionPage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Tentative d'accès à sans utilisateur connecté.");
            return "redirect:/login";
        }

        int userId=userDetails.getId();
        String userName=userDetails.getNameUser();

        List<RelatedUserDTO> relatedUsers = userRelationService.findRelatedUsers(userId);
        List<Transaction> transactionList=transactionService.getTransactionHistoryByUserId(userId);
        List<TransactionDTO> transactionDTOs = transactionService.convertToDTOList(transactionList);
        model.addAttribute("relationUserList",relatedUsers);
        model.addAttribute("transactionList",transactionDTOs);
        model.addAttribute("userId", userId);
        model.addAttribute("currentUsername",userName);
        return "TransactionPage"; // Va chercher "loginPage.html" dans "templates"
    }
    @GetMapping("/profil")
    public String getUserProfile(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Tentative d'accès à sans utilisateur connecté.");
            return "redirect:/login";
        }

        int userId=userDetails.getId();

        model.addAttribute("userId", userId);
        return "profilPage";
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


}

