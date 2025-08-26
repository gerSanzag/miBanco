package com.mibanco.viewTest.internalTest;

import com.mibanco.view.internal.ConsoleIOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ConsoleIOImpl.
 * Tests console input/output operations and resource management.
 */
class ConsoleIOImplTest {

    private ConsoleIOImpl consoleIO;
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
        
        // Close consoleIO if it exists
        if (consoleIO != null) {
            try {
                consoleIO.close();
            } catch (Exception e) {
                // Ignore close errors in tests
            }
        }
    }

    @Test
    void testReadInput() {
        // Given: Simulate user input
        String expectedInput = "test input";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(expectedInput.getBytes());
        System.setIn(inputStream);
        
        // When: Create ConsoleIO and read input
        consoleIO = new ConsoleIOImpl();
        String actualInput = consoleIO.readInput();
        
        // Then: Verify input is read correctly
        assertEquals(expectedInput, actualInput);
    }

    @Test
    void testWriteOutput() {
        // Given: Message to write
        String message = "Test message";
        
        // When: Create ConsoleIO and write output
        consoleIO = new ConsoleIOImpl();
        consoleIO.writeOutput(message);
        
        // Then: Verify output is written correctly
        String actualOutput = outputStream.toString().trim();
        assertEquals(message, actualOutput);
    }

    @Test
    void testMultipleWriteOutput() {
        // Given: Multiple messages
        String message1 = "First message";
        String message2 = "Second message";
        
        // When: Write multiple outputs
        consoleIO = new ConsoleIOImpl();
        consoleIO.writeOutput(message1);
        consoleIO.writeOutput(message2);
        
        // Then: Verify all outputs are written
        String actualOutput = outputStream.toString();
        assertTrue(actualOutput.contains(message1));
        assertTrue(actualOutput.contains(message2));
    }

    @Test
    void testClose() {
        // Given: ConsoleIO instance
        consoleIO = new ConsoleIOImpl();
        
        // When: Close the console
        // Then: Should not throw exception
        assertDoesNotThrow(() -> consoleIO.close());
    }

    @Test
    void testCloseMultipleTimes() {
        // Given: ConsoleIO instance
        consoleIO = new ConsoleIOImpl();
        
        // When: Close multiple times
        // Then: Should not throw exception on subsequent closes
        assertDoesNotThrow(() -> {
            consoleIO.close();
            consoleIO.close();
        });
    }

    @Test
    void testReadInputAfterClose() {
        // Given: ConsoleIO instance
        consoleIO = new ConsoleIOImpl();
        consoleIO.close();
        
        // When & Then: Reading after close should throw IllegalStateException
        // Scanner.close() prevents further reading operations
        assertThrows(IllegalStateException.class, () -> consoleIO.readInput());
    }
}
