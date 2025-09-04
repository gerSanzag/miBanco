package com.mibanco.viewTest.internalTest;

import com.mibanco.view.AccountView;
import com.mibanco.view.ConsoleIO;
import com.mibanco.view.internal.AccountViewImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for AccountViewImpl
 * Tests the captureDataAccount method following the same pattern as ClientViewImplTest
 */
@ExtendWith(MockitoExtension.class)
class AccountViewImplTest {

    @Mock
    private ConsoleIO mockConsole;
    
    private AccountView accountView;
    
    @BeforeEach
    void setUp() {
        accountView = new AccountViewImpl(mockConsole);
    }
    
    @Test
    void shouldReturnAccountDataWhenUserConfirmsCreation() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                        // idHolder
            .thenReturn("1")                          // accountType (1=SAVINGS)
            .thenReturn("1000.00")                    // initialBalance
            .thenReturn("s");                         // confirmation
            
        // When
        Optional<Map<String, String>> result = accountView.captureDataAccount();
        
        // Then
        assertThat(result).isPresent();
        Map<String, String> accountData = result.get();
        
        assertThat(accountData).containsEntry("holder", "123");
        assertThat(accountData).containsEntry("type", "SAVINGS");
        assertThat(accountData).containsEntry("initialBalance", "1000.00");
        
        // Verify console interactions
        verify(mockConsole, times(4)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenUserCancelsCreation() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                        // idHolder
            .thenReturn("1")                          // accountType
            .thenReturn("1000.00")                    // initialBalance
            .thenReturn("n");                         // confirmation (cancel)
            
        // When
        Optional<Map<String, String>> result = accountView.captureDataAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole, times(4)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenAccountTypeIsInvalid() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                        // idHolder
            .thenReturn("4")                          // invalid accountType
            .thenReturn("1000.00");                   // initialBalance (not reached)
            
        // When
        Optional<Map<String, String>> result = accountView.captureDataAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole, times(3)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    
    @Test
    void shouldHandleDifferentAccountTypes() {
        // Test CHECKING account type
        when(mockConsole.readInput())
            .thenReturn("456")                        // idHolder
            .thenReturn("2")                          // accountType (2=CHECKING)
            .thenReturn("2000.00")                    // initialBalance
            .thenReturn("s");                         // confirmation
            
        // When
        Optional<Map<String, String>> result = accountView.captureDataAccount();
        
        // Then
        assertThat(result).isPresent();
        Map<String, String> accountData = result.get();
        
        assertThat(accountData).containsEntry("type", "CHECKING");
        
        // Verify console interactions
        verify(mockConsole, times(4)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldHandleFixedTermAccountType() {
        // Test FIXED_TERM account type
        when(mockConsole.readInput())
            .thenReturn("789")                        // idHolder
            .thenReturn("3")                          // accountType (3=FIXED_TERM)
            .thenReturn("5000.00")                    // initialBalance
            .thenReturn("s");                         // confirmation
            
        // When
        Optional<Map<String, String>> result = accountView.captureDataAccount();
        
        // Then
        assertThat(result).isPresent();
        Map<String, String> accountData = result.get();
        
        assertThat(accountData).containsEntry("type", "FIXED_TERM");
        
        // Verify console interactions
        verify(mockConsole, times(4)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    // Search Account Tests
    
    @Test
    void shouldReturnSearchCriteriaWhenSearchByAccountNumber() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("1")                          // option: search by account number
            .thenReturn("ES3400000000000000001002");  // account number
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isPresent();
        Map<String, String> searchCriteria = result.get();
        
        assertThat(searchCriteria).containsEntry("searchType", "accountNumber");
        assertThat(searchCriteria).containsEntry("searchValue", "ES3400000000000000001002");
        
        // Verify console interactions
        verify(mockConsole, times(2)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnSearchCriteriaWhenSearchByHolderId() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("2")                          // option: search by holder ID
            .thenReturn("123");                       // holder ID
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isPresent();
        Map<String, String> searchCriteria = result.get();
        
        assertThat(searchCriteria).containsEntry("searchType", "holderId");
        assertThat(searchCriteria).containsEntry("searchValue", "123");
        
        // Verify console interactions
        verify(mockConsole, times(2)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenUserChoosesToReturnToMenu() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("3");                         // option: return to menu
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole, times(1)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenInvalidOptionIsSelected() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("4");                         // invalid option
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole, times(1)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenAccountNumberIsEmpty() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("1")                          // option: search by account number
            .thenReturn("");                          // empty account number
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole, times(2)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenHolderIdIsEmpty() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("2")                          // option: search by holder ID
            .thenReturn("");                          // empty holder ID
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole, times(2)).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
    
    @Test
    void shouldReturnEmptyWhenExceptionOccurs() {
        // Given
        when(mockConsole.readInput())
            .thenThrow(new RuntimeException("Console error"));
            
        // When
        Optional<Map<String, String>> result = accountView.searchAccount();
        
        // Then
        assertThat(result).isEmpty();
        
        // Verify console interactions
        verify(mockConsole).readInput();
        verify(mockConsole, atLeast(1)).writeOutput(anyString());
    }
}
