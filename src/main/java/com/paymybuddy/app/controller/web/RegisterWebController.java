package com.paymybuddy.app.controller.web;

import com.paymybuddy.app.dto.RegisterDTO;
import com.paymybuddy.app.exception.EmailAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.InvalidEmailException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.paymybuddy.app.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;



@Slf4j
@Controller
public class RegisterWebController {




    private final RegistrationService registrationService;

    public RegisterWebController( RegistrationService registrationService) {
        this.registrationService = registrationService;
    }



    @GetMapping("/register")
    public String showRegisterPage() {
        return "registerPage";
    }
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterDTO registerDTO,
                               RedirectAttributes redirectAttributes) {
        try {
            log.info("Registering user with email: {}", registerDTO.getEmail());
            registrationService.registerUser(registerDTO);
            log.info("User registered successfully with email: {}", registerDTO.getEmail());

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
            return "redirect:/register";

        } catch (IllegalArgumentException | InvalidEmailException | EmailAlreadyExistsException | EntitySaveException |
                 EntityNotFoundException e) {
            log.warn("Error during registration for email {}: {}", registerDTO.getEmail(), e.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";

        }
    }
}
