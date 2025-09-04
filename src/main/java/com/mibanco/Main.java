package com.mibanco;

import com.mibanco.service.internal.ServiceFactory;
import com.mibanco.util.ShutdownHookUtil;
import com.mibanco.view.internal.ConsoleIOImpl;
import com.mibanco.view.internal.ViewImpl;

/**
 * Main class for the MiBanco application
 * Entry point that initializes the application and starts the main view
 */
public class Main {
    
    /**
     * Main method - entry point of the application
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Register shutdown hook to save data before closing
            ShutdownHookUtil.registerShutdownHook();
            
            // Initialize services using factory
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            
            // Initialize console input/output
            ConsoleIOImpl console = new ConsoleIOImpl();
            
            // Initialize and start the main view with all dependencies
            ViewImpl mainView = new ViewImpl(
                console,
                serviceFactory.getAccountService(),
                serviceFactory.getClientService(),
                serviceFactory.getCardService(),
                serviceFactory.getTransactionOperationsService()
            );
            
            // Start the application
            mainView.start();
            
        } catch (Exception e) {
            System.err.println("Error starting the application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
