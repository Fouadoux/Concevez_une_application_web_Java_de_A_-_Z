package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.exception.EntitySaveException;
import com.paymybuddy.app.repository.BankAccountRepository;
import com.paymybuddy.app.repository.RoleRepository;
import com.paymybuddy.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BankAccountService bankAccountService;

    private User user;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role= new Role();
        role.setRoleName("user");

        user = new User();
        user.setId(1);
        user.setUserName("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole(role);

        bankAccount = new BankAccount();
        bankAccount.setTransferId(1);
        bankAccount.setUser(user);
        bankAccount.setAmount(BigDecimal.valueOf(1000.0));
        bankAccount.setExternalBankAccountNumber("1234567890");
        bankAccount.setTransferDate(LocalDateTime.now());
        bankAccount.setStatus(true);
    }

    @Test
    void createBankAccount_Success() {
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        BankAccount createdBankAccount = bankAccountService.createBankAccount(bankAccount);

        assertNotNull(createdBankAccount);
        assertEquals(bankAccount.getTransferId(), createdBankAccount.getTransferId());
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void createBankAccount_ThrowsEntitySaveException() {
        when(bankAccountRepository.save(bankAccount)).thenThrow(new RuntimeException());

        assertThrows(EntitySaveException.class, () -> bankAccountService.createBankAccount(bankAccount));
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void getBankAccountById_Success() {
        when(bankAccountRepository.findById(1)).thenReturn(Optional.of(bankAccount));

        BankAccount foundBankAccount = bankAccountService.getBankAccountById(1);

        assertNotNull(foundBankAccount);
        assertEquals(bankAccount.getTransferId(), foundBankAccount.getTransferId());
        verify(bankAccountRepository, times(1)).findById(1);
    }

    @Test
    void getBankAccountById_ThrowsEntityNotFoundException() {
        when(bankAccountRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.getBankAccountById(1));
        verify(bankAccountRepository, times(1)).findById(1);
    }

    @Test
    void getBankAccountsByUser_Success() {
        when(bankAccountRepository.findAllBankAccountByUser(user)).thenReturn(Arrays.asList(bankAccount));

        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByUser(user);

        assertNotNull(bankAccounts);
        assertEquals(1, bankAccounts.size());
        verify(bankAccountRepository, times(1)).findAllBankAccountByUser(user);
    }

    @Test
    void updateBankAccountStatus_Success() {
        when(bankAccountRepository.findById(1)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        BankAccount updatedBankAccount = bankAccountService.updateBankAccountStatus(1, false);

        assertNotNull(updatedBankAccount);
        assertFalse(updatedBankAccount.isStatus());
        verify(bankAccountRepository, times(1)).findById(1);
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void updateBankAccountStatus_ThrowsEntityNotFoundException() {
        when(bankAccountRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.updateBankAccountStatus(1, false));
        verify(bankAccountRepository, times(1)).findById(1);
    }

    @Test
    void deleteBankAccount_Success() {
        when(bankAccountRepository.findById(1)).thenReturn(Optional.of(bankAccount));
        doNothing().when(bankAccountRepository).delete(bankAccount);

        assertDoesNotThrow(() -> bankAccountService.deleteBankAccount(1));
        verify(bankAccountRepository, times(1)).findById(1);
        verify(bankAccountRepository, times(1)).delete(bankAccount);
    }

    @Test
    void deleteBankAccount_ThrowsEntityNotFoundException() {
        when(bankAccountRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.deleteBankAccount(1));
        verify(bankAccountRepository, times(1)).findById(1);
        verify(bankAccountRepository, never()).delete(any(BankAccount.class));
    }
}
