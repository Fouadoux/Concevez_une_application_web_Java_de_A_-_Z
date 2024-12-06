package com.paymybuddy.app.controller.web;


import com.paymybuddy.app.dto.RelatedUserDTO;
import com.paymybuddy.app.dto.TransactionDTO;
import com.paymybuddy.app.entity.Transaction;
import com.paymybuddy.app.security.CustomUserDetails;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserRelationService;
import com.paymybuddy.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class TransactionWebController {

    private final UserRelationService userRelationService;
    private final TransactionService transactionService;

    public TransactionWebController(UserRelationService userRelationService, TransactionService transactionService) {
        this.userRelationService=userRelationService;
        this.transactionService = transactionService;
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
        return "transactionPage"; // Va chercher "loginPage.html" dans "templates"
    }

    @PostMapping("/create")
    public String createTransaction(@RequestParam int senderId,
                                    @RequestParam int receiverId,
                                    @RequestParam long amount,
                                    @RequestParam String description,
                                    RedirectAttributes redirectAttributes) {

        log.info("Creating transaction from user {} to user {} for amount {} with description: {}",
                senderId, receiverId, amount, description);

        try {
            String transactionResult = transactionService.createTransaction(senderId, receiverId, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", transactionResult);
            log.info("Transaction created successfully: {}", transactionResult);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error("Error creating transaction: {}", e.getMessage(), e);
        }

        return "redirect:/transaction";
    }

}
