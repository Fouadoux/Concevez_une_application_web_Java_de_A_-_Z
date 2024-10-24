package com.paymybuddy.app.controller;

import com.paymybuddy.app.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * Endpoint pour obtenir le compte associé à un utilisateur donné par son ID.
     *
     * @param userId L'ID de l'utilisateur
     * @return Les informations du compte ou une réponse d'erreur si non trouvé
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountByUserId(@PathVariable int userId) {
        return userAccountService.getAccountByUserId(userId);
    }

    /**
     * Endpoint pour créer un compte pour un utilisateur donné.
     *
     * @param userId L'ID de l'utilisateur pour lequel créer un compte
     * @return Le compte nouvellement créé ou une réponse d'erreur si l'utilisateur a déjà un compte
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAccountForUser(@PathVariable int userId) {
        return userAccountService.createAccountForUser(userId);
    }

    /**
     * Endpoint pour supprimer le compte d'un utilisateur donné par son ID.
     *
     * @param userId L'ID de l'utilisateur pour lequel supprimer le compte
     * @return Une réponse indiquant le succès ou une erreur si le compte n'existe pas
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteAccountByUserId(@PathVariable int userId) {
        return userAccountService.deleteAccountByUserId(userId);
    }
}
