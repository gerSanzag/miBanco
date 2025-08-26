package com.mibanco.viewTest;

import com.mibanco.service.AccountService;
import com.mibanco.service.CardService;
import com.mibanco.service.ClientService;
import com.mibanco.service.TransactionOperationsService;
import com.mibanco.view.ConsoleIO;
import com.mibanco.view.internal.ViewImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

import com.mibanco.dto.AccountDTO;
import com.mibanco.model.enums.AccountType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ViewImpl.
 * Tests the actual implementation by executing real code and capturing output.
 */
class ViewImplIntegrationTest {

    private ViewImpl view;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private PrintStream originalIn;

    @BeforeEach
    void setUp() {
        // Capture System.out for testing output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    void testViewImplCreation() {
        // Given: Mock services using null (ViewImpl doesn't use them in constructor)
        ConsoleIO mockConsoleIO = new MockConsoleIO();
        
        // When: Create ViewImpl with all dependencies
        // Then: Should not throw exception
        assertDoesNotThrow(() -> {
            view = new ViewImpl(
                mockConsoleIO,
                null, // AccountService not used in constructor
                null, // ClientService not used in constructor
                null, // CardService not used in constructor
                null  // TransactionService not used in constructor
            );
        });
        
        assertNotNull(view);
    }

    @Test
    void testStartMethodShowsWelcomeMessage() {
        // Given: ViewImpl with mock console that returns exit option
        ConsoleIO mockConsoleIO = new MockConsoleIO();
        view = new ViewImpl(
            mockConsoleIO,
            null, null, null, null
        );
        
        // When: Start the view
        view.start();
        
        // Then: Should show welcome message
        String output = outputStream.toString();
        assertTrue(output.contains("=== BANCO MI BANCO ==="));
        assertTrue(output.contains("Bienvenido al sistema bancario"));
    }

    @Test
    void testStartMethodShowsGoodbyeMessage() {
        // Given: ViewImpl with mock console that returns exit option
        ConsoleIO mockConsoleIO = new MockConsoleIO();
        view = new ViewImpl(
            mockConsoleIO,
            null, null, null, null
        );
        
        // When: Start the view
        view.start();
        
        // Then: Should show goodbye message
        String output = outputStream.toString();
        assertTrue(output.contains("¡Gracias por usar nuestro banco!"));
        assertTrue(output.contains("¡Hasta luego!"));
    }

    @Test
    void testStartMethodShowsMainMenu() {
        // Given: ViewImpl with mock console that returns exit option
        ConsoleIO mockConsoleIO = new MockConsoleIO();
        view = new ViewImpl(
            mockConsoleIO,
            null, null, null, null
        );
        
        // When: Start the view
        view.start();
        
        // Then: Should show main menu
        String output = outputStream.toString();
        assertTrue(output.contains("=== MENÚ PRINCIPAL ==="));
        assertTrue(output.contains("1. Gestión de Clientes"));
        assertTrue(output.contains("2. Gestión de Cuentas"));
        assertTrue(output.contains("3. Gestión de Tarjetas"));
        assertTrue(output.contains("4. Transacciones"));
        assertTrue(output.contains("5. Salir"));
    }

    // Mock implementations for testing
    private static class MockConsoleIO implements ConsoleIO {
        @Override
        public String readInput() {
            return "5"; // Exit option to avoid infinite loop
        }

        @Override
        public void writeOutput(String message) {
            System.out.println(message);
        }
    }
}
