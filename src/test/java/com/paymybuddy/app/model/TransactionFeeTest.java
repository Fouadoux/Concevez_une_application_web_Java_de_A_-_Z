package com.paymybuddy.app.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFeeTest {

    @Test
    public void testCreateTransactionFee() {
        // Création d'une instance de TransactionFee
        TransactionFee fee = new TransactionFee();
        fee.setFee(1);
        fee.setPercentage(BigDecimal.valueOf(0.5));
        fee.setEffectiveDate(LocalDateTime.now());

        // Vérification que les valeurs sont correctement définies
        assertEquals(1, fee.getFee());
        assertEquals(BigDecimal.valueOf(0.5), fee.getPercentage());
        assertNotNull(fee.getEffectiveDate());
    }

    @Test
    public void testUpdatePercentage() {
        // Création d'une instance de TransactionFee
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(BigDecimal.valueOf(0.5));

        // Mise à jour du pourcentage
        fee.setPercentage(BigDecimal.valueOf(0.6));

        // Vérification que le pourcentage a été mis à jour
        assertEquals(BigDecimal.valueOf(0.6), fee.getPercentage());
    }

    @Test
    public void testUpdateEffectiveDate() {
        // Création d'une instance de TransactionFee
        TransactionFee fee = new TransactionFee();
        LocalDateTime oldDate = LocalDateTime.now().minusDays(1);
        fee.setEffectiveDate(oldDate);

        // Mise à jour de la date effective
        LocalDateTime newDate = LocalDateTime.now();
        fee.setEffectiveDate(newDate);

        // Vérification que la date effective a été mise à jour
        assertEquals(newDate, fee.getEffectiveDate());
    }

    @Test
    public void testSetFee() {
        // Création d'une instance de TransactionFee
        TransactionFee fee = new TransactionFee();

        // Mise à jour du champ fee
        fee.setFee(2);

        // Vérification que le fee a été mis à jour
        assertEquals(2, fee.getFee());
    }
}
