package com.mibanco.viewTest;

import com.mibanco.view.View;
import com.mibanco.view.ConsoleIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for View interface.
 * Tests the main view functionality through the interface.
 */
class ViewTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

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
    void testViewInterfaceExists() {
        // Given & When: Check if View interface exists
        // Then: Should not throw exception when accessing the interface
        assertDoesNotThrow(() -> {
            Class<?> viewClass = View.class;
            assertNotNull(viewClass);
            assertTrue(viewClass.isInterface());
        });
    }

    @Test
    void testConsoleIOInterfaceExists() {
        // Given & When: Check if ConsoleIO interface exists
        // Then: Should not throw exception when accessing the interface
        assertDoesNotThrow(() -> {
            Class<?> consoleIOClass = ConsoleIO.class;
            assertNotNull(consoleIOClass);
            assertTrue(consoleIOClass.isInterface());
        });
    }

    @Test
    void testViewInterfaceHasStartMethod() {
        // Given & When: Check if View interface has start method
        // Then: Should have the start method
        assertDoesNotThrow(() -> {
            View.class.getMethod("start");
        });
    }

    @Test
    void testConsoleIOInterfaceHasRequiredMethods() {
        // Given & When: Check if ConsoleIO interface has required methods
        // Then: Should have readInput and writeOutput methods
        assertDoesNotThrow(() -> {
            ConsoleIO.class.getMethod("readInput");
            ConsoleIO.class.getMethod("writeOutput", String.class);
        });
    }
}
