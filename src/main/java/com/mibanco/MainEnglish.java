package com.mibanco;

import com.mibanco.utilEnglish.ShutdownHookUtil;
import com.mibanco.repositoryEnglish.internal.RepositoryFactory;
import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.Account;
import com.mibanco.modelEnglish.enums.AccountType;
import com.mibanco.serviceEnglish.ClientService;
import com.mibanco.serviceEnglish.AccountService;
import com.mibanco.serviceEnglish.internal.ServiceFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class of the miBanco application
 * Demonstrates the functionality of ShutdownHookUtil
 */
public class MainEnglish {
    
    public static void main(String[] args) {
        System.out.println("🏦 ===== MIBANCO APPLICATION ===== 🏦");
        System.out.println("Starting banking system...\n");
        
        // 🔧 STEP 1: Register the shutdown hook
        System.out.println("📋 STEP 1: Registering shutdown hook...");
        ShutdownHookUtil.registerShutdownHook();
        
        if (ShutdownHookUtil.isRegistered()) {
            System.out.println("✅ Shutdown hook registered successfully");
        } else {
            System.out.println("❌ Error registering shutdown hook");
        }
        
        // 🔄 STEP 2: Demonstrate functionality
        System.out.println("\n📋 STEP 2: Demonstrating system functionality...");
        demonstrateFunctionality();
        
        // ⏰ STEP 3: Simulate application usage
        System.out.println("\n📋 STEP 3: Simulating application usage...");
        simulateApplicationUsage();
        
        // 🚪 STEP 4: Instructions for the user
        System.out.println("\n📋 STEP 4: Instructions to close the application");
        System.out.println("💡 To see the shutdown hook in action:");
        System.out.println("   - Press Ctrl+C to close the application");
        System.out.println("   - Or execute: System.exit(0)");
        System.out.println("   - The system will automatically save all data");
        
        // 🔄 Keep the application running for demonstration
        keepApplicationActive();
    }
    
    /**
     * Demonstrates basic system functionality
     */
    private static void demonstrateFunctionality() {
        try {
            // Get services through the factory
            ClientService clientService = ServiceFactory.getInstance().getClientService();
            AccountService accountService = ServiceFactory.getInstance().getAccountService();
            
            // Create client data using Map (as expected by the service)
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("email", "john@example.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Example Street 123");
            clientData.put("birthDate", "1990-01-01");
            
            // Create client using the service
            clientService.createClientDto(clientData)
                .ifPresent(createdClient -> {
                    System.out.println("✅ Client created: " + createdClient.getFirstName() + " " + createdClient.getLastName());
                    System.out.println("   - ID: " + createdClient.getId());
                    System.out.println("   - DNI: " + createdClient.getDni());
                    System.out.println("   - Email: " + createdClient.getEmail());
                });
            
        } catch (Exception e) {
            System.err.println("❌ Error in demonstration: " + e.getMessage());
        }
    }
    
    /**
     * Simulates normal application usage
     */
    private static void simulateApplicationUsage() {
        System.out.println("🔄 Simulating banking operations...");
        
        try {
            // Simulate some operations
            Thread.sleep(1000);
            System.out.println("💳 Processing transactions...");
            Thread.sleep(500);
            System.out.println("📊 Generating reports...");
            Thread.sleep(500);
            System.out.println("🔐 Verifying security...");
            Thread.sleep(500);
            System.out.println("✅ System operating correctly");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Interruption during simulation");
        }
    }
    
    /**
     * Keeps the application active for demonstration
     */
    private static void keepApplicationActive() {
        System.out.println("\n🔄 Application running...");
        System.out.println("⏳ Waiting for user instructions...");
        
        try {
            // Keep the application running
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\n👋 Application interrupted by user");
        }
    }
} 