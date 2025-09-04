package com.mibanco.controllerTest.internalTest;

import com.mibanco.controller.internal.AccountControllerImpl;
import com.mibanco.dto.AccountDTO;
import com.mibanco.model.Account;
import com.mibanco.model.enums.AccountType;
import com.mibanco.service.AccountService;
import com.mibanco.view.AccountView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

/**
 * Test class for AccountControllerImpl
 * Tests the createAccount method following the same pattern as ClientControllerImplTest
 */
@ExtendWith(MockitoExtension.class)
class AccountControllerImplTest {

    @Mock
    private AccountService mockAccountService;
    
    @Mock
    private AccountView mockAccountView;
    
    private AccountControllerImpl accountController;
    
    @BeforeEach
    void setUp() {
        accountController = new AccountControllerImpl(mockAccountService, mockAccountView);
    }
    
    @Test
    void shouldReturnTrueWhenCreateAccountSucceeds() {
        // Given
        Map<String, String> accountData = Map.of(
            "holder", "123",
            "type", "SAVINGS",
            "initialBalance", "1000.00"
        );
        
        AccountDTO createdAccount = AccountDTO.builder()
            .accountNumber("ES3400000000000000001002")
            .type(AccountType.SAVINGS)
            .initialBalance(new BigDecimal("1000.00"))
            .balance(new BigDecimal("1000.00"))
            .creationDate(LocalDateTime.now())
            .active(true)
            .build();
        
        when(mockAccountView.captureDataAccount()).thenReturn(Optional.of(accountData));
        when(mockAccountService.createAccountDto(anyMap(), any(BigDecimal.class), any())).thenReturn(Optional.of(createdAccount));
        
        // When
        boolean result = accountController.createAccount();
        
        // Then
        assertThat(result).isTrue();
        verify(mockAccountView).captureDataAccount();
        verify(mockAccountService).createAccountDto(accountData, new BigDecimal("1000.00"), null);
    }
    
    
    @Test
    void shouldReturnFalseWhenServiceReturnsEmpty() {
        // Given
        Map<String, String> accountData = Map.of(
            "holder", "123",
            "type", "SAVINGS",
            "initialBalance", "1000.00"
        );
        
        when(mockAccountView.captureDataAccount()).thenReturn(Optional.of(accountData));
        when(mockAccountService.createAccountDto(anyMap(), any(BigDecimal.class), any())).thenReturn(Optional.empty());
        
        // When
        boolean result = accountController.createAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).captureDataAccount();
        verify(mockAccountService).createAccountDto(accountData, new BigDecimal("1000.00"), null);
    }
    
    @Test
    void shouldReturnFalseWhenInitialBalanceIsNull() {
        // Given
        Map<String, String> accountData = Map.of(
            "holder", "123",
            "type", "SAVINGS"
            // initialBalance is missing
        );
        
        when(mockAccountView.captureDataAccount()).thenReturn(Optional.of(accountData));
        
        // When
        boolean result = accountController.createAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).captureDataAccount();
        verify(mockAccountService, never()).createAccountDto(anyMap(), any(BigDecimal.class), any());
    }
    
    @Test
    void shouldReturnFalseWhenInitialBalanceIsEmpty() {
        // Given
        Map<String, String> accountData = Map.of(
            "holder", "123",
            "type", "SAVINGS",
            "initialBalance", ""
        );
        
        when(mockAccountView.captureDataAccount()).thenReturn(Optional.of(accountData));
        
        // When
        boolean result = accountController.createAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).captureDataAccount();
        verify(mockAccountService, never()).createAccountDto(anyMap(), any(BigDecimal.class), any());
    }
    
    @Test
    void shouldReturnFalseWhenInitialBalanceIsInvalid() {
        // Given
        Map<String, String> accountData = Map.of(
            "holder", "123",
            "type", "SAVINGS",
            "initialBalance", "invalid_number"
        );
        
        when(mockAccountView.captureDataAccount()).thenReturn(Optional.of(accountData));
        
        // When
        boolean result = accountController.createAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).captureDataAccount();
        verify(mockAccountService, never()).createAccountDto(anyMap(), any(BigDecimal.class), any());
    }
    
    @Test
    void shouldReturnFalseWhenRequiredFieldsAreMissing() {
        // Given
        Map<String, String> accountData = Map.of(
            "accountNumber", "ES3400000000000000001002"
            // Missing required fields: holder, type, initialBalance
        );
        
        when(mockAccountView.captureDataAccount()).thenReturn(Optional.of(accountData));
        
        // When
        boolean result = accountController.createAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).captureDataAccount();
        verify(mockAccountService, never()).createAccountDto(anyMap(), any(BigDecimal.class), any());
    }
    
    // Search Account Tests
    
    @Test
    void shouldReturnTrueWhenSearchAccountByNumberSucceeds() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "accountNumber",
            "searchValue", "123"
        );
        
        AccountDTO foundAccount = AccountDTO.builder()
            .accountNumber("ES3400000000000000001002")
            .type(AccountType.SAVINGS)
            .initialBalance(new BigDecimal("1000.00"))
            .balance(new BigDecimal("1000.00"))
            .creationDate(LocalDateTime.now())
            .active(true)
            .build();
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        when(mockAccountService.getAccountByNumber(Optional.of(123L))).thenReturn(Optional.of(foundAccount));
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isTrue();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService).getAccountByNumber(Optional.of(123L));
        verify(mockAccountView).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnTrueWhenSearchAccountByHolderIdSucceeds() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "holderId",
            "searchValue", "456"
        );
        
        AccountDTO foundAccount = AccountDTO.builder()
            .accountNumber("ES3400000000000000001003")
            .type(AccountType.CHECKING)
            .initialBalance(new BigDecimal("2000.00"))
            .balance(new BigDecimal("2000.00"))
            .creationDate(LocalDateTime.now())
            .active(true)
            .build();
        
        List<AccountDTO> accounts = List.of(foundAccount);
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        when(mockAccountService.searchByHolderId(Optional.of(456L))).thenReturn(Optional.of(accounts));
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isTrue();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService).searchByHolderId(Optional.of(456L));
        verify(mockAccountView).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenSearchAccountByNumberFails() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "accountNumber",
            "searchValue", "999"
        );
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        when(mockAccountService.getAccountByNumber(Optional.of(999L))).thenReturn(Optional.empty());
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService).getAccountByNumber(Optional.of(999L));
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenSearchAccountByHolderIdFails() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "holderId",
            "searchValue", "999"
        );
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        when(mockAccountService.searchByHolderId(Optional.of(999L))).thenReturn(Optional.empty());
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService).searchByHolderId(Optional.of(999L));
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenSearchAccountByHolderIdReturnsEmptyList() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "holderId",
            "searchValue", "456"
        );
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        when(mockAccountService.searchByHolderId(Optional.of(456L))).thenReturn(Optional.of(List.of()));
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService).searchByHolderId(Optional.of(456L));
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenSearchAccountWithInvalidNumber() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "accountNumber",
            "searchValue", "invalid_number"
        );
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService, never()).getAccountByNumber(any());
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenSearchAccountWithInvalidHolderId() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "holderId",
            "searchValue", "invalid_id"
        );
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService, never()).searchByHolderId(any());
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenSearchAccountWithInvalidSearchType() {
        // Given
        Map<String, String> searchCriteria = Map.of(
            "searchType", "invalidType",
            "searchValue", "123"
        );
        
        when(mockAccountView.searchAccount()).thenReturn(Optional.of(searchCriteria));
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService, never()).getAccountByNumber(any());
        verify(mockAccountService, never()).searchByHolderId(any());
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
    
    @Test
    void shouldReturnFalseWhenViewReturnsEmpty() {
        // Given
        when(mockAccountView.searchAccount()).thenReturn(Optional.empty());
        
        // When
        boolean result = accountController.searchAccount();
        
        // Then
        assertThat(result).isFalse();
        verify(mockAccountView).searchAccount();
        verify(mockAccountService, never()).getAccountByNumber(any());
        verify(mockAccountService, never()).searchByHolderId(any());
        verify(mockAccountView, never()).displayAccount(any(Account.class));
    }
}
