package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.TransactionFee;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFeeTest {

    @Test
    public void testCreateTransactionFee() {
        TransactionFee fee = new TransactionFee();
        fee.setId(1);
        fee.setPercentage(5);
        fee.setEffectiveDate(LocalDateTime.now());

        assertEquals(1, fee.getId());
        assertEquals(5, fee.getPercentage());
        assertNotNull(fee.getEffectiveDate());
    }

    @Test
    public void testUpdatePercentage() {
        TransactionFee fee = new TransactionFee();
        fee.setPercentage(5);

        fee.setPercentage(6);

        assertEquals(6, fee.getPercentage());
    }

    @Test
    public void testUpdateEffectiveDate() {
        TransactionFee fee = new TransactionFee();
        LocalDateTime oldDate = LocalDateTime.now().minusDays(1);
        fee.setEffectiveDate(oldDate);

        LocalDateTime newDate = LocalDateTime.now();
        fee.setEffectiveDate(newDate);

        assertEquals(newDate, fee.getEffectiveDate());
    }

    @Test
    public void testSetFee() {
        TransactionFee fee = new TransactionFee();

        fee.setId(2);

        assertEquals(2, fee.getId());
    }
}
