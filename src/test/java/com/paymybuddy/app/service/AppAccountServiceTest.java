package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.paymybuddy.app.dto.AppAccountDTO;
import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.AccountAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.exception.InvalidBalanceException;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AppAccountServiceTest {

    @Mock
    private AppAccountRepository appAccountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AppAccountService appAccountService;

    private AppAccount account;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = new Role();
        role.setRoleName("USER");

        user = new User();
        user.setId(1);
        user.setUserName("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole(role);

        account = new AppAccount();
        account.setId(1);
        account.setUser(user);
        account.setBalance(100);
        account.setCreatedAt(LocalDateTime.now());
        account.setLastUpdate(LocalDateTime.now());
        account.setDailyLimit(50000L);


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));
    }

    @Test
    void testGetBalanceByUserId_Success() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));


        long balance = appAccountService.getBalanceByUserId(user.getId());

        assertEquals(100, balance);
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
        verify(userRepository, times(0)).findById(anyInt()); // Aucun appel attendu à userRepository
    }

    @Test
    void testGetBalanceByUserId_AccountNotFound() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.getBalanceByUserId(user.getId()));
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testUpdateBalanceByUserId_Success() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        long updatedBalance = appAccountService.updateBalanceByUserId(user.getId(), 50);

        assertEquals(150, updatedBalance);
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
        verify(appAccountRepository, times(1)).save(account);
    }

    @Test
    void testUpdateBalanceByUserId_AccountNotFound() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.updateBalanceByUserId(user.getId(), 50));
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testUpdateBalanceByUserId_NegativeBalance() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        assertThrows(InvalidBalanceException.class, () -> appAccountService.updateBalanceByUserId(user.getId(), -200));
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testGetInfoAppAccountByUserId_Success() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        AppAccountDTO accountDTO = appAccountService.getInfoAppAccountByUserId(user.getId());

        assertNotNull(accountDTO);
        assertEquals(100, accountDTO.getBalance());
        assertNotNull(accountDTO.getLastUpdate());
        assertNotNull(accountDTO.getLastUpdate());
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testGetInfoAppAccountByUserId_AccountNotFound() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.getInfoAppAccountByUserId(user.getId()));
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testCreateAccountForUser_Success() {


        when(userService.getUserById(user.getId())).thenReturn(user);
        when(appAccountRepository.save(any(AppAccount.class))).thenReturn(account);

        AppAccount createdAccount = appAccountService.createAccountForUser(user.getId());

        assertNotNull(createdAccount);
        assertEquals(account.getUser(), createdAccount.getUser());
        verify(appAccountRepository, times(1)).save(any(AppAccount.class));
    }

    @Test
    void testCreateAccountForUser_UserNotFound() {
        doThrow(new EntityNotFoundException("User not found with ID: " + user.getId()))
                .when(userRepository).findById(user.getId());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.createAccountForUser(user.getId()));

        verify(appAccountRepository, never()).save(any());
    }


    @Test
    void testCreateAccountForUser_AccountAlreadyExists() {

        user.setAppAccount(account);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        assertThrows(AccountAlreadyExistsException.class, () -> appAccountService.createAccountForUser(user.getId()));
        verify(appAccountRepository, times(0)).save(any(AppAccount.class));
    }

    @Test
    void testDeleteAccountByUserId_Success() {

        when(userService.getUserById(user.getId())).thenReturn(user);

        appAccountService.deleteAccountByUserId(user.getId());

        verify(appAccountRepository, times(1)).delete(account);
    }

    @Test
    void testDeleteAccountByUserId_UserNotFound() {
        int userId = 123;

        doThrow(new EntityNotFoundException("User not found with ID: " + userId))
                .when(userService).getUserById(userId);

        assertThrows(EntityNotFoundException.class, () -> appAccountService.deleteAccountByUserId(userId));

        verify(appAccountRepository, never()).findByUserId(anyInt());
        verify(appAccountRepository, never()).delete(any());
    }



    @Test
    void testDeleteAccountByUserId_AccountNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.deleteAccountByUserId(user.getId()));
        verify(appAccountRepository, times(0)).delete(any(AppAccount.class));
    }

    @Test
    void testGetTransactionLimitForUser() {
        long limit = appAccountService.getTransactionLimitForUser(1);

        assertEquals(limit, account.getDailyLimit());
    }

    @Test
    void testChangeDailyLimit_success() {
        // Arrange
        int userId = 1;
        long newDailyLimit = 300000;

        // Act
        appAccountService.changeDailyLimit(userId, newDailyLimit);

        // Assert
        assertEquals(newDailyLimit, account.getDailyLimit());
        verify(appAccountRepository, times(1)).save(account);
    }

    @Test
    void testChangeDailyLimit_roleNotFound() {
        // Arrange
        int userId = 2;
        long newDailyLimit = 300000;

        when(appAccountRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                appAccountService.changeDailyLimit(userId, newDailyLimit));
    }

    @Test
    void testChangeDailyLimit_invalidRoleName() {
        // Arrange
        int userId = 2;
        long newDailyLimit = 300000;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                appAccountService.changeDailyLimit(userId, newDailyLimit));
    }

    @Test
    void testChangeDailyLimit_invalidDailyLimit() {
        // Arrange
        int userId = 2;
        long newDailyLimit = 0;

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                appAccountService.changeDailyLimit(userId, newDailyLimit));
    }

}
