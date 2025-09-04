package com.mibanco.viewTest.internalTest;

import com.mibanco.view.internal.ViewImpl;
import com.mibanco.view.ConsoleIO;
import com.mibanco.service.AccountService;
import com.mibanco.service.CardService;
import com.mibanco.service.ClientService;
import com.mibanco.service.TransactionOperationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Test class for ViewImpl.
 * Tests the main view functionality including menu navigation and option processing.
 */
class ViewImplTest {
    
    private ViewImpl viewImpl;
    private ConsoleIO mockConsole;
    private AccountService mockAccountService;
    private ClientService mockClientService;
    private CardService mockCardService;
    private TransactionOperationsService mockTransactionService;
    
    @BeforeEach
    void setUp() {
        mockConsole = mock(ConsoleIO.class);
        mockAccountService = mock(AccountService.class);
        mockClientService = mock(ClientService.class);
        mockCardService = mock(CardService.class);
        mockTransactionService = mock(TransactionOperationsService.class);
        
        viewImpl = new ViewImpl(mockConsole, mockAccountService, mockClientService, 
                               mockCardService, mockTransactionService);
    }
    
    @Test
    @DisplayName("Should show welcome message when start is called")
    void shouldShowWelcomeMessageWhenStartIsCalled() {
        // Given
        when(mockConsole.readInput()).thenReturn("5"); // Exit immediately

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("=== BANCO MI BANCO ===");
        verify(mockConsole).writeOutput("Bienvenido al sistema bancario");
        verify(mockConsole, atLeast(1)).writeOutput(""); // At least one empty line
    }
    
    @Test
    @DisplayName("Should show goodbye message when start is called and user exits")
    void shouldShowGoodbyeMessageWhenStartIsCalledAndUserExits() {
        // Given
        when(mockConsole.readInput()).thenReturn("5"); // Exit immediately

        // When
        viewImpl.start();

        // Then
        verify(mockConsole, atLeast(1)).writeOutput(""); // At least one empty line
        verify(mockConsole).writeOutput("¡Gracias por usar nuestro banco!");
        verify(mockConsole).writeOutput("¡Hasta luego!");
    }
    
    @Test
    @DisplayName("Should show main menu when start is called")
    void shouldShowMainMenuWhenStartIsCalled() {
        // Given
        when(mockConsole.readInput()).thenReturn("5"); // Exit immediately

        // When
        viewImpl.start();

        // Then
        verify(mockConsole, atLeast(1)).writeOutput(""); // At least one empty line
        verify(mockConsole).writeOutput("=== MENÚ PRINCIPAL ===");
        verify(mockConsole).writeOutput("1. Gestión de Clientes");
        verify(mockConsole).writeOutput("2. Gestión de Cuentas");
        verify(mockConsole).writeOutput("3. Gestión de Tarjetas");
        verify(mockConsole).writeOutput("4. Transacciones");
        verify(mockConsole).writeOutput("5. Salir");
        verify(mockConsole, atLeast(1)).writeOutput(""); // At least one empty line
        verify(mockConsole).writeOutput("Elige una opción:");
    }
    
    @Test
    @DisplayName("Should handle option 1 (Client Management) and show development message")
    void shouldHandleOption1ClientManagementAndShowDevelopmentMessage() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("1")  // Select option 1
            .thenReturn("5"); // Then exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Gestión de Clientes - En desarrollo");
        verify(mockConsole, times(2)).readInput(); // Option 1 + Exit
    }
    
    @Test
    @DisplayName("Should handle option 2 (Account Management) and show development message")
    void shouldHandleOption2AccountManagementAndShowDevelopmentMessage() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("2")  // Select option 2
            .thenReturn("5"); // Then exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Gestión de Cuentas - En desarrollo");
        verify(mockConsole, times(2)).readInput(); // Option 2 + Exit
    }
    
    @Test
    @DisplayName("Should handle option 3 (Card Management) and show development message")
    void shouldHandleOption3CardManagementAndShowDevelopmentMessage() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("3")  // Select option 3
            .thenReturn("5"); // Then exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Gestión de Tarjetas - En desarrollo");
        verify(mockConsole, times(2)).readInput(); // Option 3 + Exit
    }
    
    @Test
    @DisplayName("Should handle option 4 (Transactions) and show development message")
    void shouldHandleOption4TransactionsAndShowDevelopmentMessage() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("4")  // Select option 4
            .thenReturn("5"); // Then exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Transacciones - En desarrollo");
        verify(mockConsole, times(2)).readInput(); // Option 4 + Exit
    }
    
    @Test
    @DisplayName("Should handle invalid option and show error message")
    void shouldHandleInvalidOptionAndShowErrorMessage() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("99") // Invalid option
            .thenReturn("5"); // Then exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Opción inválida. Por favor, elige una opción del 1 al 5.");
        verify(mockConsole, times(2)).readInput(); // Invalid option + Exit
    }
    
    @Test
    @DisplayName("Should handle multiple valid options before exiting")
    void shouldHandleMultipleValidOptionsBeforeExiting() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("1")  // Client Management
            .thenReturn("2")  // Account Management
            .thenReturn("3")  // Card Management
            .thenReturn("4")  // Transactions
            .thenReturn("5"); // Exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Gestión de Clientes - En desarrollo");
        verify(mockConsole).writeOutput("Gestión de Cuentas - En desarrollo");
        verify(mockConsole).writeOutput("Gestión de Tarjetas - En desarrollo");
        verify(mockConsole).writeOutput("Transacciones - En desarrollo");
        verify(mockConsole, times(5)).readInput(); // 4 options + Exit
    }
    
    @Test
    @DisplayName("Should handle multiple invalid options before exiting")
    void shouldHandleMultipleInvalidOptionsBeforeExiting() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("99") // Invalid option
            .thenReturn("88") // Invalid option
            .thenReturn("77") // Invalid option
            .thenReturn("5"); // Exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole, times(3)).writeOutput("Opción inválida. Por favor, elige una opción del 1 al 5.");
        verify(mockConsole, times(4)).readInput(); // 3 invalid options + Exit
    }
    
    @Test
    @DisplayName("Should handle mixed valid and invalid options before exiting")
    void shouldHandleMixedValidAndInvalidOptionsBeforeExiting() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("1")  // Valid option
            .thenReturn("99") // Invalid option
            .thenReturn("2")  // Valid option
            .thenReturn("88") // Invalid option
            .thenReturn("5"); // Exit

        // When
        viewImpl.start();

        // Then
        verify(mockConsole).writeOutput("Gestión de Clientes - En desarrollo");
        verify(mockConsole).writeOutput("Gestión de Cuentas - En desarrollo");
        verify(mockConsole, times(2)).writeOutput("Opción inválida. Por favor, elige una opción del 1 al 5.");
        verify(mockConsole, times(5)).readInput(); // 2 valid + 2 invalid + Exit
    }
    
    @Test
    @DisplayName("Should show main menu multiple times during navigation")
    void shouldShowMainMenuMultipleTimesDuringNavigation() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("1")  // Client Management
            .thenReturn("2")  // Account Management
            .thenReturn("5"); // Exit

        // When
        viewImpl.start();

        // Then
        // Main menu should be shown 3 times: initial + after option 1 + after option 2
        verify(mockConsole, times(3)).writeOutput("=== MENÚ PRINCIPAL ===");
        verify(mockConsole, times(3)).writeOutput("1. Gestión de Clientes");
        verify(mockConsole, times(3)).writeOutput("2. Gestión de Cuentas");
        verify(mockConsole, times(3)).writeOutput("3. Gestión de Tarjetas");
        verify(mockConsole, times(3)).writeOutput("4. Transacciones");
        verify(mockConsole, times(3)).writeOutput("5. Salir");
        verify(mockConsole, atLeast(3)).writeOutput(""); // At least 3 empty lines
        verify(mockConsole, times(3)).writeOutput("Elige una opción:");
    }
}
