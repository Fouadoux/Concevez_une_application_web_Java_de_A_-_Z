package com.paymybuddy.app.service;

import org.junit.jupiter.api.Test;

import static com.paymybuddy.app.service.EmailValidationService.isValidEmail;
import static org.junit.jupiter.api.Assertions.*;

class EmailValidationServiceTest {

    @Test
    void testIsValidEmail_ValidEmails_ReturnTrue() {
        // Arrange & Act & Assert
        assertTrue(isValidEmail("test@example.com"), "Valid email should return true");
        assertTrue(isValidEmail("user.name+tag+sorting@example.com"), "Valid email with tags should return true");
        assertTrue(isValidEmail("user@sub.domain.com"), "Valid email with subdomain should return true");
        assertTrue(isValidEmail("user123@example.co.uk"), "Valid email with multiple TLDs should return true");
        assertTrue(isValidEmail("user_name@example.com"), "Valid email with underscores should return true");
        assertTrue(isValidEmail("user-name@example.com"), "Valid email with hyphens should return true");
    }

    @Test
    void testIsValidEmail_InvalidEmails_ReturnFalse() {
        // Arrange & Act & Assert
        assertFalse(isValidEmail("plainaddress"), "Email without @ should return false");
        assertFalse(isValidEmail("@missingusername.com"), "Email without username should return false");
        assertFalse(isValidEmail("username@.com"), "Email with missing domain name should return false");
        assertFalse(isValidEmail("username@domain..com"), "Email with double dots in domain should return false");
        assertFalse(isValidEmail("username@domain.c"), "Email with TLD less than 2 characters should return false");
        assertFalse(isValidEmail("username@domain,com"), "Email with invalid comma should return false");
    }

}