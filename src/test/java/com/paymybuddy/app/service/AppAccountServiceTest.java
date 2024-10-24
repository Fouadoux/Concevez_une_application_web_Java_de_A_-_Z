package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.repository.AppAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;


import static org.mockito.Mockito.*;

class AppAccountServiceTest {

    @Mock
    private AppAccountRepository appAccountRepository;

    @InjectMocks
    private AppAccountService appAccountService;

    private AppAccount account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new AppAccount();
        account.setAccountId(1);
        account.setBalance(100.0f);
        account.setCreatedAt(LocalDateTime.now());
        account.setLastUpdate(LocalDateTime.now());
    }

    @Test
    void testGetBalanceById_Success() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.of(account));

        ResponseEntity<?> response = appAccountService.getBalanceById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100.0f, response.getBody());
        verify(appAccountRepository, times(1)).findById(1);
    }

    @Test
    void testGetBalanceById_AccountNotFound() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = appAccountService.getBalanceById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found with ID: 1", response.getBody());
        verify(appAccountRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateBalanceById_Success() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.of(account));

        ResponseEntity<?> response = appAccountService.updateBalanceById(1, 50.0f);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150.0f, response.getBody());
        verify(appAccountRepository, times(1)).findById(1);
        verify(appAccountRepository, times(1)).save(account);
    }

    @Test
    void testUpdateBalanceById_AccountNotFound() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = appAccountService.updateBalanceById(1, 50.0f);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found with ID: 1", response.getBody());
        verify(appAccountRepository, times(1)).findById(1);
        verify(appAccountRepository, times(0)).save(any(AppAccount.class));
    }

    @Test
    void testUpdateBalanceById_NegativeBalance() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.of(account));

        ResponseEntity<?> response = appAccountService.updateBalanceById(1, -200.0f);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Balance can't be negative. Current balance: 100.0", response.getBody());
        verify(appAccountRepository, times(1)).findById(1);
        verify(appAccountRepository, times(0)).save(any(AppAccount.class));
    }

    @Test
    void testGetInfoAppAccountById_Success() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.of(account));

        ResponseEntity<?> response = appAccountService.getInfoAppAccountById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        AppAccountService.AppAccountInfo accountInfo = (AppAccountService.AppAccountInfo) response.getBody();
        assertNotNull(accountInfo);
        assertEquals(100.0f, accountInfo.balance());
        assertNotNull(accountInfo.lastUpdate());
        assertNotNull(accountInfo.createdAt());

        verify(appAccountRepository, times(1)).findById(1);
    }

    @Test
    void testGetInfoAppAccountById_AccountNotFound() {
        when(appAccountRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = appAccountService.getInfoAppAccountById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found with ID: 1", response.getBody());
        verify(appAccountRepository, times(1)).findById(1);
    }
}
