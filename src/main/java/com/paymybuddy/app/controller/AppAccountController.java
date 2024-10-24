package com.paymybuddy.app.controller;

import com.paymybuddy.app.service.AppAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/appAccounts")
public class AppAccountController {

    private final AppAccountService appAccountService;

    public AppAccountController(AppAccountService appAccountService) {
        this.appAccountService = appAccountService;
    }

    /**
     * Endpoint pour obtenir le solde d'un compte en fonction de son ID.
     *
     * @param accountId L'ID du compte
     * @return Le solde du compte ou une réponse d'erreur si non trouvé
     */
    @PreAuthorize("hasRole('ADMIN') or #accountId == principal.id")
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getBalanceById(@PathVariable int accountId) {
        return appAccountService.getBalanceById(accountId);
    }

    /**
     * Endpoint pour mettre à jour le solde d'un compte en fonction de son ID.
     *
     * @param accountId L'ID du compte
     * @param newBalance Le nouveau solde à ajouter ou soustraire
     * @return Le solde mis à jour ou une réponse d'erreur si le compte n'existe pas ou si le solde est négatif
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{accountId}/balance")
    public ResponseEntity<?> updateBalanceById(@PathVariable int accountId, @RequestParam BigDecimal newBalance) {  // Utiliser BigDecimal pour la précision
        return appAccountService.updateBalanceById(accountId, newBalance);
    }

    /**
     * Endpoint pour obtenir les informations complètes d'un compte en fonction de son ID.
     *
     * @param accountId L'ID du compte
     * @return Les informations du compte ou une réponse d'erreur si non trouvé
     */
    @PreAuthorize("hasRole('ADMIN') or #accountId == principal.id")
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getInfoAppAccountById(@PathVariable int accountId) {
        return appAccountService.getInfoAppAccountById(accountId);
    }
}
