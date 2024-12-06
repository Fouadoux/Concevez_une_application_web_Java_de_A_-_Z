package com.paymybuddy.app.service;

import com.paymybuddy.app.entity.AppAccount;
import com.paymybuddy.app.entity.BankAccount;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.exception.EntityDeleteException;
import com.paymybuddy.app.exception.EntityNotFoundException;
import com.paymybuddy.app.repository.AppAccountRepository;
import com.paymybuddy.app.repository.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private AppAccountRepository appAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private AppAccount appAccount;
    private BankAccount bankAccount;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);

        appAccount = new AppAccount();
        appAccount.setId(1);
        appAccount.setBalance(100000L);

        bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAmount(50000L);
        bankAccount.setUser(user);
        bankAccount.setExternalBankAccountNumber("1234-5678");
        bankAccount.setStatus(false);
    }

    @Test
    void testCreateBankAccount_ShouldCreateSuccessfully() {
        log.info("Testing bank account creation for user with ID: {}", user.getId());
        bankAccount.setUser(user);
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        BankAccount createdBankAccount = bankAccountService.createBankAccount(bankAccount);

        assertNotNull(createdBankAccount);
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testGetBankAccountById_ShouldReturnBankAccount() {
        log.info("Testing retrieval of bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        BankAccount retrievedBankAccount = bankAccountService.getBankAccountById(bankAccount.getId());

        assertNotNull(retrievedBankAccount);
        assertEquals(bankAccount.getId(), retrievedBankAccount.getId());
    }

    @Test
    void testGetBankAccountById_ShouldThrowEntityNotFoundException() {
        log.info("Testing retrieval failure for non-existent bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.getBankAccountById(bankAccount.getId()));
    }

    @Test
    void testTransferToBankAccount_ShouldTransferSuccessfully() {
        log.info("Testing successful transfer from AppAccount ID: {} to BankAccount ID: {}", appAccount.getId(), bankAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        bankAccountService.transferToBankAccount(appAccount.getId(), bankAccount.getId(), 100L); // 1.00 in cents

        assertEquals(90000, appAccount.getBalance());
        assertEquals(60000, bankAccount.getAmount());
        verify(appAccountRepository, times(1)).save(appAccount);
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testTransferToBankAccount_ShouldThrowException_WhenInsufficientBalance() {
        log.info("Testing transfer failure due to insufficient balance from AppAccount ID: {} to BankAccount ID: {}", appAccount.getId(), bankAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transferToBankAccount(appAccount.getId(), bankAccount.getId(), 2000L));
    }

    @Test
    void testTransferToBankAccount_ShouldThrowException_WhenAmountIsNegative() {
        log.info("Testing transfer failure due to negative amount from AppAccount ID: {} to BankAccount ID: {}", appAccount.getId(), bankAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transferToBankAccount(appAccount.getId(), bankAccount.getId(), -100L));
    }

    @Test
    void testTransferFromBankAccount_ShouldTransferSuccessfully() {
        log.info("Testing successful transfer from BankAccount ID: {} to AppAccount ID: {}", bankAccount.getId(), appAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        bankAccountService.transferFromBankAccount(appAccount.getId(), bankAccount.getId(), 100L);

        assertEquals(110000, appAccount.getBalance());
        assertEquals(40000L, bankAccount.getAmount());
        verify(appAccountRepository, times(1)).save(appAccount);
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testTransferFromBankAccount_ShouldThrowException_WhenInsufficientBalance() {
        log.info("Testing transfer failure due to insufficient balance from BankAccount ID: {} to AppAccount ID: {}", bankAccount.getId(), appAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transferFromBankAccount(appAccount.getId(), bankAccount.getId(), 60000L));
    }

    @Test
    void testTransferFromBankAccount_ShouldThrowException_WhenAmountIsNegative() {
        log.info("Testing transfer failure due to negative amount from BankAccount ID: {} to AppAccount ID: {}", bankAccount.getId(), appAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transferFromBankAccount(appAccount.getId(), bankAccount.getId(), -100L));
    }

    @Test
    void testTransferToBankAccount_ShouldThrowException_WhenAmountIsZero() {
        log.info("Testing transfer failure due to zero amount from AppAccount ID: {} to BankAccount ID: {}", appAccount.getId(), bankAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transferToBankAccount(appAccount.getId(), bankAccount.getId(), 0L));
    }

    @Test
    void testTransferFromBankAccount_ShouldThrowException_WhenAmountIsZero() {
        log.info("Testing transfer failure due to zero amount from BankAccount ID: {} to AppAccount ID: {}", bankAccount.getId(), appAccount.getId());
        when(appAccountRepository.findById(appAccount.getId())).thenReturn(Optional.of(appAccount));
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transferFromBankAccount(appAccount.getId(), bankAccount.getId(), 0L));
    }

    @Test
    void testUpdateBankAccountStatus_ShouldUpdateSuccessfully() {
        log.info("Testing updating status of bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        BankAccount updatedBankAccount = bankAccountService.updateBankAccountStatus(bankAccount.getId(), true);
        assertNotNull(updatedBankAccount);
        assertTrue(updatedBankAccount.isStatus());
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testUpdateBankAccountStatus_ShouldThrowEntityNotFoundException() {
        log.info("Testing updating status failure for non-existent bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.updateBankAccountStatus(bankAccount.getId(), true));
    }

    @Test
    void testUpdateExternalBankAccountNumber_ShouldUpdateSuccessfully() {
        String newExternalBankAccountNumber = "NEW123456789";
        log.info("Testing updating external bank account number for bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
        BankAccount updatedBankAccount = bankAccountService.updateExternalBankAccountNumber(bankAccount.getId(), newExternalBankAccountNumber);

        assertNotNull(updatedBankAccount);
        assertEquals(newExternalBankAccountNumber, updatedBankAccount.getExternalBankAccountNumber());
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testUpdateExternalBankAccountNumber_ShouldThrowEntityNotFoundException() {
        String newExternalBankAccountNumber = "NEW123456789";
        log.info("Testing updating external bank account number failure for non-existent bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.updateExternalBankAccountNumber(bankAccount.getId(), newExternalBankAccountNumber));
    }

    @Test
    void testDeleteBankAccount_ShouldDeleteSuccessfully() {
        log.info("Testing successful deletion of bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));

        bankAccountService.deleteBankAccount(bankAccount.getId());

        verify(bankAccountRepository, times(1)).delete(bankAccount);
    }

    @Test
    void testDeleteBankAccount_ShouldThrowEntityNotFoundException() {
        log.info("Testing deletion failure for non-existent bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.deleteBankAccount(bankAccount.getId()));
    }

    @Test
    void testDeleteBankAccount_ShouldThrowEntityDeleteException() {
        log.info("Testing deletion failure due to exception for bank account with ID: {}", bankAccount.getId());
        when(bankAccountRepository.findById(bankAccount.getId())).thenReturn(Optional.of(bankAccount));
        doThrow(new RuntimeException()).when(bankAccountRepository).delete(bankAccount);

        assertThrows(EntityDeleteException.class, () -> bankAccountService.deleteBankAccount(bankAccount.getId()));
    }

    @Test
    void testGetBankAccountsByUser() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1);

        BankAccount bankAccount1 = mock(BankAccount.class);
        BankAccount bankAccount2 = mock(BankAccount.class);

        when(bankAccount1.getUser()).thenReturn(user);
        when(bankAccount2.getUser()).thenReturn(user);

        when(bankAccountRepository.findAllBankAccountByUser(user)).thenReturn(java.util.Optional.of(Arrays.asList(bankAccount1, bankAccount2)));

        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByUser(user);

        assertNotNull(bankAccounts);
        assertEquals(2, bankAccounts.size());
        assertEquals(user, bankAccounts.get(0).getUser());
        assertEquals(user, bankAccounts.get(1).getUser());
    }

    @Test
    void testGetBankAccountsByUserNotFound() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1);

        when(bankAccountRepository.findAllBankAccountByUser(user)).thenReturn(java.util.Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bankAccountService.getBankAccountsByUser(user);
        });

        assertEquals("Bank accounts not found for user ID: " + user.getId(), thrown.getMessage());
    }
}
