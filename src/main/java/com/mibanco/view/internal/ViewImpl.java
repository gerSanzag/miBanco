package com.mibanco.view.internal;

import com.mibanco.service.AccountService;
import com.mibanco.service.CardService;
import com.mibanco.service.ClientService;
import com.mibanco.service.TransactionOperationsService;
import com.mibanco.view.ConsoleIO;
import com.mibanco.view.View;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implementation of the main view for the banking application.
 * Handles user interaction through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public class ViewImpl implements View {
    
    private final ConsoleIO console;
    private final AccountService accountService;
    private final ClientService clientService;
    private final CardService cardService;
    private final TransactionOperationsService transactionService;
    
    // Functional interfaces for console operations
    private final Consumer<String> showMessage;
    private final Supplier<String> getInput;
    private final Runnable showMenu;
    
    // Menu actions using functional approach
    private final Map<String, Runnable> menuActions;
    
    /**
     * Constructor that injects all required dependencies.
     * 
     * @param console the console input/output interface
     * @param accountService the account service
     * @param clientService the client service
     * @param cardService the card service
     * @param transactionService the transaction operations service
     */
    public ViewImpl(ConsoleIO console, AccountService accountService, 
             ClientService clientService, CardService cardService, 
             TransactionOperationsService transactionService) {
        this.console = console;
        this.accountService = accountService;
        this.clientService = clientService;
        this.cardService = cardService;
        this.transactionService = transactionService;
        
        // Initialize functional interfaces
        this.showMessage = console::writeOutput;
        this.getInput = console::readInput;
        this.showMenu = this::showMainMenu;
        
        // Initialize menu actions using functional approach
        this.menuActions = Map.of(
            "1", () -> showMessage.accept("Gestión de Clientes - En desarrollo"),
            "2", () -> showMessage.accept("Gestión de Cuentas - En desarrollo"),
            "3", () -> showMessage.accept("Gestión de Tarjetas - En desarrollo"),
            "4", () -> showMessage.accept("Transacciones - En desarrollo")
        );
    }
    
    /**
     * Starts the console application and handles the main menu loop.
     */
    @Override
    public void start() {
        showWelcomeMessage();
        
        // Functional approach to main loop
        generateMainLoop().run();
        
        showGoodbyeMessage();
    }
    
    /**
     * Generates the main application loop using functional approach.
     * 
     * @return Runnable representing the main loop
     */
    private Runnable generateMainLoop() {
        return () -> {
            AtomicBoolean running = new AtomicBoolean(true);
            
            while (running.get()) {
                showMenu.run();
                String option = getInput.get();
                
                // Functional approach to option processing
                Optional.of(option)
                    .filter("5"::equals)
                    .ifPresentOrElse(
                        exit -> running.set(false),
                        () -> processMainMenuOption(option)
                    );
            }
        };
    }
    
    /**
     * Shows the welcome message when the application starts.
     */
    private void showWelcomeMessage() {
        showMessage.accept("=== BANCO MI BANCO ===");
        showMessage.accept("Bienvenido al sistema bancario");
        showMessage.accept("");
    }
    
    /**
     * Shows the goodbye message when the application ends.
     */
    private void showGoodbyeMessage() {
        showMessage.accept("");
        showMessage.accept("¡Gracias por usar nuestro banco!");
        showMessage.accept("¡Hasta luego!");
    }
    
    /**
     * Shows the main menu options.
     */
    private void showMainMenu() {
        showMessage.accept("");
        showMessage.accept("=== MENÚ PRINCIPAL ===");
        showMessage.accept("1. Gestión de Clientes");
        showMessage.accept("2. Gestión de Cuentas");
        showMessage.accept("3. Gestión de Tarjetas");
        showMessage.accept("4. Transacciones");
        showMessage.accept("5. Salir");
        showMessage.accept("");
        showMessage.accept("Elige una opción:");
    }
    
    /**
     * Processes the selected main menu option using functional approach.
     * 
     * @param option the selected option
     */
    private void processMainMenuOption(String option) {
        // Functional approach using Map and Optional
        Optional.ofNullable(menuActions.get(option))
            .ifPresentOrElse(
                Runnable::run,
                () -> showMessage.accept("Opción inválida. Por favor, elige una opción del 1 al 5.")
            );
    }
}
