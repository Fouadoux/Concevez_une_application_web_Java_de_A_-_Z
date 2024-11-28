package com.paymybuddy.app.model;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class AppAccountTest {

    @Test
    public void testCreateAppAccount() {
        User user = new User();
        user.setUserName("testUser");

        AppAccount account = new AppAccount();
        account.setUser(user);
        account.setBalance(100);
        account.setLastUpdate(LocalDateTime.now());

        assertNotNull(account, "Account should not be null");
        assertEquals(100, account.getBalance(), "Balance should be 100.0");
        assertEquals(user, account.getUser(), "User should match the one assigned to the account");
        assertNotNull(account.getLastUpdate(), "Last update should not be null");
    }

    @Test
    public void testUpdateBalance() {
        AppAccount account = new AppAccount();
        account.setBalance(100);

        account.setBalance(150);  // Update the balance to 150.0

        assertEquals(150.0, account.getBalance(), "Balance should be updated to 150.0");
    }

    @Test
    public void testUpdateLastUpdate() {
        AppAccount account = new AppAccount();
        LocalDateTime now = LocalDateTime.now();

        account.setLastUpdate(now);

        assertEquals(now, account.getLastUpdate(), "Last update should match the provided value");
    }

    @Test
    public void testAppAccountUserRelation() {
        User user = new User();
        user.setUserName("userTest");

        AppAccount account = new AppAccount();
        account.setUser(user);

        assertEquals(user, account.getUser(), "The user associated with the account should be the one assigned");
    }

    @Test
    void testBalanceValidation() {
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        Validator validator = factory.getValidator();

        AppAccount account = new AppAccount();
        account.setBalance(-5);

        Set<ConstraintViolation<AppAccount>> violations = validator.validate(account);

        assertFalse(violations.isEmpty(), "Validation should fail for negative balance");
    }
}
