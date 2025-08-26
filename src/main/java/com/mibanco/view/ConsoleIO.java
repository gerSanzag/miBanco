package com.mibanco.view;

/**
 * Interface for console input/output operations.
 * Provides methods for reading input and writing output to the console.
 */
public interface ConsoleIO {
    
    /**
     * Reads input from the console.
     * 
     * @return the input string read from the console
     */
    String readInput();
    
    /**
     * Writes a message to the console.
     * 
     * @param message the message to write to the console
     */
    void writeOutput(String message);
}
