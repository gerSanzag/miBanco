package com.mibanco.view.internal;

import com.mibanco.view.ConsoleIO;
import java.util.Scanner;

/**
 * Implementation of ConsoleIO interface using standard console input/output.
 * Uses Scanner for reading input and System.out for writing output.
 * Implements AutoCloseable to ensure proper resource management.
 */
public class ConsoleIOImpl implements ConsoleIO, AutoCloseable {
    
    private final Scanner scanner;
    
    /**
     * Constructor that initializes the Scanner for reading console input.
     */
    public ConsoleIOImpl() {
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public String readInput() {
        return scanner.nextLine();
    }
    
    @Override
    public void writeOutput(String message) {
        System.out.println(message);
    }
    
    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
