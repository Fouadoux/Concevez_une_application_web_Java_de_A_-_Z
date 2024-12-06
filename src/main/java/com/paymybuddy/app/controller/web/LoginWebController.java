package com.paymybuddy.app.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Slf4j
@Controller
public class LoginWebController {


    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Incorrect email address or password.");
        }
        return "loginPage";
    }

}

