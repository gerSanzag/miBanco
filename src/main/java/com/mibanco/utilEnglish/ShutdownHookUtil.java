package com.mibanco.utilEnglish;

import com.mibanco.repositoryEnglish.internal.RepositoryFactory;
import java.util.concurrent.atomic.AtomicBoolean;

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
                    // Get instance of repository factory
                    RepositoryFactory factory = RepositoryFactory.getInstance();
                    
                    // Save data from all repositories
                    factory.getClientRepository().saveData();
                    factory.getAccountRepository().saveData();
                    factory.getCardRepository().saveData();
                    factory.getTransactionRepository().saveData();
                    
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