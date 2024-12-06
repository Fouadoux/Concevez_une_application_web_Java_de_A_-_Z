package com.paymybuddy.app.service;

import java.util.regex.Pattern;

public  class EmailValidationService {

    /**
     * Vérifie si une adresse email est valide en utilisant une expression régulière.
     *
     * @param email L'adresse email à valider
     * @return true si l'email est valide, false sinon
     */


    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String domainPartRegex = "[A-Za-z0-9.-]*[^.](\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";

        if (!email.matches(emailRegex)) {
            return false;
        }

        String domainPart = email.substring(email.indexOf('@') + 1);
        return domainPart.matches(domainPartRegex);
    }


}
