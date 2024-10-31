package com.paymybuddy.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.AccountAlreadyExistsException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.InvalidBalanceException;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AppAccountServiceTest {

    @Mock
    private AppAccountRepository appAccountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

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
        account.setAccountId(1);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(100));
        account.setCreatedAt(LocalDateTime.now());
        account.setLastUpdate(LocalDateTime.now());

        // Mock pour les méthodes findById et findByUserId
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));
    }

    @Test
    void testGetBalanceByUserId_Success() {
        // Mock du retour de appAccountRepository.findByUserId
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        // Appel de la méthode à tester
        BigDecimal balance = appAccountService.getBalanceByUserId(user.getId());

        // Vérifications
        assertEquals(BigDecimal.valueOf(100), balance);
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

        BigDecimal updatedBalance = appAccountService.updateBalanceByUserId(user.getId(), BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), updatedBalance);
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
        verify(appAccountRepository, times(1)).save(account);
    }

    @Test
    void testUpdateBalanceByUserId_AccountNotFound() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.updateBalanceByUserId(user.getId(), BigDecimal.valueOf(50)));
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testUpdateBalanceByUserId_NegativeBalance() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        assertThrows(InvalidBalanceException.class, () -> appAccountService.updateBalanceByUserId(user.getId(), BigDecimal.valueOf(-200)));
        verify(appAccountRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testGetInfoAppAccountByUserId_Success() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        AppAccountService.AppAccountInfo accountInfo = appAccountService.getInfoAppAccountByUserId(user.getId());

        assertNotNull(accountInfo);
        assertEquals(BigDecimal.valueOf(100), accountInfo.balance());
        assertNotNull(accountInfo.lastUpdate());
        assertNotNull(accountInfo.createdAt());
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
        // Configurer les mocks pour le retour de userRepository et vérifier qu'aucun compte n'existe
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // Mock pour le retour de save afin de renvoyer le nouvel account
        when(appAccountRepository.save(any(AppAccount.class))).thenReturn(account);

        // Appel de la méthode à tester
        AppAccount createdAccount = appAccountService.createAccountForUser(user.getId());

        // Vérifications
        assertNotNull(createdAccount); // S'assure que le compte créé n'est pas nul
        assertEquals(account.getUser(), createdAccount.getUser()); // Vérifie que le compte est associé à l'utilisateur correct
        verify(appAccountRepository, times(1)).save(any(AppAccount.class));
    }

    @Test
    void testCreateAccountForUser_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.createAccountForUser(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testCreateAccountForUser_AccountAlreadyExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        assertThrows(AccountAlreadyExistsException.class, () -> appAccountService.createAccountForUser(user.getId()));
        verify(appAccountRepository, times(0)).save(any(AppAccount.class));
    }

    @Test
    void testDeleteAccountByUserId_Success() {
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

        appAccountService.deleteAccountByUserId(user.getId());

        verify(appAccountRepository, times(1)).delete(account);
    }

    @Test
    void testDeleteAccountByUserId_UserNotFound() {
        // Configurer le mock pour simuler l'absence de l'utilisateur
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Vérifier que l'exception est levée
        assertThrows(EntityNotFoundException.class, () -> appAccountService.deleteAccountByUserId(user.getId()));

        // Vérifie que findById est bien appelé une fois
        verify(userRepository, times(1)).findById(user.getId());
        // Vérifie que findByUserId n'est jamais appelé car l'utilisateur n'existe pas
        verify(appAccountRepository, times(0)).findByUserId(anyInt());
    }


    @Test
    void testDeleteAccountByUserId_AccountNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(appAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appAccountService.deleteAccountByUserId(user.getId()));
        verify(appAccountRepository, times(0)).delete(any(AppAccount.class));
    }
}
