package com.paymybuddy.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginPageController {


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
}

