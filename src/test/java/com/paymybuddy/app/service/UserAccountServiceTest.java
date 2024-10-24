package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test class for the {@link UserAccountService}.
 * This class contains unit tests for the various functionalities such as
 * creating, retrieving, and deleting accounts linked to users.
 */
class UserAccountServiceTest {

    @Mock
    private AppAccountRepository appAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAccountService userAccountService;

    private User user;
    private AppAccount account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialiser un utilisateur et un compte
        user = new User();
        user.setId(1);
        user.setUserName("testUser");

        account = new AppAccount();
        account.setAccountId(1);
        account.setUser(user);
        account.setBalance(100.0f);
    }

    /**
     * Test to ensure that the account linked to a user is correctly retrieved.
     * Verifies that the correct account is returned when the user and account both exist.
     */
    @Test
    void testGetAccountByUserId_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUser(user)).thenReturn(account);

        ResponseEntity<?> response = userAccountService.getAccountByUserId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
        verify(userRepository, times(1)).findById(1);
        verify(appAccountRepository, times(1)).findByUser(user);
    }

    /**
     * Test to verify that the service returns a 404 response when the user is not found.
     */
    @Test
    void testGetAccountByUserId_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userAccountService.getAccountByUserId(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: 1", response.getBody());
        verify(userRepository, times(1)).findById(1);
        verify(appAccountRepository, times(0)).findByUser(any(User.class));
    }

    /**
     * Test to ensure that a 404 response is returned when no account is found for the user.
     */
    @Test
    void testGetAccountByUserId_AccountNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUser(user)).thenReturn(null);

        ResponseEntity<?> response = userAccountService.getAccountByUserId(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found for user with ID: 1", response.getBody());
        verify(userRepository, times(1)).findById(1);
        verify(appAccountRepository, times(1)).findByUser(user);
    }

    /**
     * Test to verify the creation of an account for a user who does not already have an account.
     * Ensures that the account is created and saved successfully.
     */
    @Test
    void testCreateAccountForUser_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUser(user)).thenReturn(null); // L'utilisateur n'a pas de compte

        ResponseEntity<?> response = userAccountService.createAccountForUser(1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(appAccountRepository, times(1)).save(any(AppAccount.class));
    }

    /**
     * Test to verify that the service returns a 404 response when attempting to create an account
     * for a user that does not exist.
     */
    @Test
    void testCreateAccountForUser_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userAccountService.createAccountForUser(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: 1", response.getBody());
        verify(userRepository, times(1)).findById(1);
        verify(appAccountRepository, times(0)).findByUser(any(User.class));
        verify(appAccountRepository, times(0)).save(any(AppAccount.class));
    }

    /**
     * Test to ensure that the service returns a 400 response when trying to create an account
     * for a user who already has an account.
     */
    @Test
    void testCreateAccountForUser_AccountAlreadyExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUser(user)).thenReturn(account); // L'utilisateur a déjà un compte

        ResponseEntity<?> response = userAccountService.createAccountForUser(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already has an account", response.getBody());
        verify(appAccountRepository, times(0)).save(any(AppAccount.class));
    }

    /**
     * Test to verify that the service successfully deletes an account for a user.
     */
    @Test
    void testDeleteAccountByUserId_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUser(user)).thenReturn(account);

        ResponseEntity<?> response = userAccountService.deleteAccountByUserId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account deleted successfully", response.getBody());
        verify(appAccountRepository, times(1)).delete(account);
    }

    /**
     * Test to verify that the service returns a 404 response when trying to delete an account
     * for a user that does not exist.
     */
    @Test
    void testDeleteAccountByUserId_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userAccountService.deleteAccountByUserId(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with ID: 1", response.getBody());
        verify(userRepository, times(1)).findById(1);
        verify(appAccountRepository, times(0)).delete(any(AppAccount.class));
    }

    /**
     * Test to verify that the service returns a 404 response when attempting to delete an account
     * for a user who does not have an account.
     */
    @Test
    void testDeleteAccountByUserId_AccountNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUser(user)).thenReturn(null); // Pas de compte pour cet utilisateur

        ResponseEntity<?> response = userAccountService.deleteAccountByUserId(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found for user with ID: 1", response.getBody());
        verify(appAccountRepository, times(0)).delete(any(AppAccount.class));
    }
}
