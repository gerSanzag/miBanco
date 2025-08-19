package com.mibanco.util;

import java.util.concurrent.atomic.AtomicBoolean;

import com.mibanco.repository.internal.RepositoryServiceLocator;
import com.mibanco.repository.ClientRepository;
import com.mibanco.repository.AccountRepository;
import com.mibanco.repository.CardRepository;
import com.mibanco.repository.TransactionRepository;

/**
 * Utility class for handling application shutdown
 * Registers a shutdown hook that forces data saving before closing
 */
public class ShutdownHookUtil {
    
    private static final AtomicBoolean hookRegistered = new AtomicBoolean(false);
    
    /**
     * Registers a shutdown hook to save data when closing the application
     * Only registers once, even if called multiple times
     */
    public static void registerShutdownHook() {
        if (hookRegistered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🔄 Saving data before closing the application...");
                
                try {
                    // Get repositories through service locator
                    ClientRepository clientRepository = RepositoryServiceLocator.getService(ClientRepository.class);
                    AccountRepository accountRepository = RepositoryServiceLocator.getService(AccountRepository.class);
                    CardRepository cardRepository = RepositoryServiceLocator.getService(CardRepository.class);
                    TransactionRepository transactionRepository = RepositoryServiceLocator.getService(TransactionRepository.class);
                    
                    // Save data from all repositories
                    clientRepository.saveData();
                    accountRepository.saveData();
                    cardRepository.saveData();
                    transactionRepository.saveData();
                    
                    System.out.println("✅ Data saved successfully");
                } catch (Exception e) {
                    System.err.println("❌ Error saving data during shutdown: " + e.getMessage());
                }
            }, "ShutdownHook-Save"));
            
            System.out.println("🔧 Shutdown hook registered for automatic saving");
        }
    }
    
    /**
     * Checks if the shutdown hook is already registered
     * @return true if registered, false otherwise
     */
    public static boolean isRegistered() {
        return hookRegistered.get();
    }
} 