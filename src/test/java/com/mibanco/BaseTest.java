package com.mibanco;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.mibanco.repository.internal.RepositoryServiceLocator;

/**
 * Base test class that provides common setup for all tests
 * Initializes RepositoryServiceLocator automatically
 * All test classes should extend this class to ensure proper initialization
 */
public abstract class BaseTest {
    
    /**
     * Initialize RepositoryServiceLocator before all tests
     * This method is called once before any test in the class
     */
    @BeforeAll
    static void setUpAll() {
        try {
            // Initialize RepositoryServiceLocator for all tests
            RepositoryServiceLocator.initializeServices();
            System.out.println("✅ RepositoryServiceLocator initialized successfully for tests");
        } catch (Exception e) {
            System.err.println("❌ Error initializing RepositoryServiceLocator: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Clean up RepositoryServiceLocator after all tests
     * This method is called once after all tests in the class
     */
    @AfterAll
    static void tearDownAll() {
        try {
            // Clear RepositoryServiceLocator to reset state
            RepositoryServiceLocator.clearServices();
            System.out.println("✅ RepositoryServiceLocator cleared successfully after tests");
        } catch (Exception e) {
            System.err.println("❌ Error clearing RepositoryServiceLocator: " + e.getMessage());
            // Don't throw here as it's cleanup
        }
    }
}
