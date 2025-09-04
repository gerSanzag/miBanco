package com.mibanco.view.internal;

import com.mibanco.view.AccountView;
import com.mibanco.view.ConsoleIO;
import com.mibanco.model.Account;
import com.mibanco.util.ReflectionUtil;
import static com.mibanco.view.util.ValidationUtil.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.List;
import com.mibanco.dto.AccountDTO;

/**
 * Implementation of the account management view.
 * Handles user interaction for account operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public class AccountViewImpl implements AccountView {
    
    // Functional interfaces for console operations
    private final Consumer<String> showMessage;
    private final Supplier<String> getInput;
    
    /**
     * Constructor that injects console dependency.
     * 
     * @param console the console input/output interface
     */
    public AccountViewImpl(ConsoleIO console) {
        // Initialize functional interfaces
        this.showMessage = console::writeOutput;
        this.getInput = console::readInput;
    }
    
    @Override
    public Optional<Map<String, String>> captureDataAccount() {
        showMessage.accept("");
        showMessage.accept("=== CREAR NUEVA CUENTA ===");
        
        try {
            // Capture account data
            String idHolder = captureStringInput("ID del titular: ", showMessage, getInput);
            String accountType = captureAccountTypeInput("Tipo de cuenta (1=AHORRO, 2=CORRIENTE, 3=PLAZO_FIJO): ", showMessage, getInput);
            String initialBalance = captureStringInput("Saldo inicial: ", showMessage, getInput);
            
            // Validate that accountType was captured successfully
            if (accountType == null) {
                showMessage.accept("Error: No se pudo capturar el tipo de cuenta correctamente.");
                return Optional.empty();
            }
            
            // Show confirmation
            showMessage.accept("");
            showMessage.accept("=== RESUMEN DE LA CUENTA ===");
            showMessage.accept("ID del titular: " + idHolder);
            showMessage.accept("Tipo de cuenta: " + accountType);
            showMessage.accept("Saldo inicial: " + initialBalance);
            
            // Ask for confirmation
            String confirm = captureStringInput("¿Confirmar creación? (s/n): ", showMessage, getInput);
            
            if ("s".equalsIgnoreCase(confirm)) {
                // Create Map with account data for the service using reflection
                Map<String, String> accountData = createAccountDataMap(idHolder, accountType, initialBalance);
                
                showMessage.accept("Datos capturados exitosamente.");
                return Optional.of(accountData);
            } else {
                showMessage.accept("Creación cancelada.");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            showMessage.accept("Error al crear cuenta: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Captures account type input with validation.
     * 
     * @param prompt the prompt to show to the user
     * @param showMessage consumer to display messages
     * @param getInput supplier to get user input
     * @return the account type if valid, null if invalid
     */
    private String captureAccountTypeInput(String prompt, Consumer<String> showMessage, Supplier<String> getInput) {
        String input = captureStringInput(prompt, showMessage, getInput);
        
        if (input != null && !input.trim().isEmpty()) {
            // Convert number input to enum value
            String accountType = switch (input.trim()) {
                case "1" -> "SAVINGS";
                case "2" -> "CHECKING";
                case "3" -> "FIXED_TERM";
                default -> null;
            };
            
            if (accountType != null) {
                return accountType;
            } else {
                showMessage.accept("Error: Tipo de cuenta inválido. Use 1, 2 o 3.");
                return null;
            }
        }
        
        return input;
    }
    
    /**
     * Creates a Map with account data using dynamic field names from reflection.
     * 
     * @param idHolder the holder ID
     * @param accountType the account type
     * @param initialBalance the initial balance
     * @return Map with account data using dynamic keys
     */
    private Map<String, String> createAccountDataMap(String idHolder, String accountType, String initialBalance) {
        Map<String, String> accountData = new HashMap<>();
        
        // Get required fields dynamically using reflection
        List<String> requiredFields = ReflectionUtil.getRequiredFields(Account.class);
        
        // Create a map of field names to captured values
        Map<String, String> capturedData = new HashMap<>();
        capturedData.put("holder", idHolder);
        capturedData.put("type", accountType);
        capturedData.put("initialBalance", initialBalance);
        
        // Use required fields as keys to ensure consistency with the entity
        for (String fieldName : requiredFields) {
            String fieldValue = capturedData.get(fieldName);
            if (fieldValue != null) {
                accountData.put(fieldName, fieldValue);
            }
        }
        
        return accountData;
    }
    
    @Override
    public Optional<Map<String, String>> searchAccount() {
        try {
            showMessage.accept("");
            showMessage.accept("=== BUSCAR CUENTA ===");
            showMessage.accept("1. Buscar por Número de Cuenta");
            showMessage.accept("2. Buscar por ID del Titular");
            showMessage.accept("3. Volver al menú de cuentas");
            showMessage.accept("");
            showMessage.accept("Elige una opción:");
            
            String option = getInput.get();
            
            return switch (option) {
                case "1" -> searchAccountByNumber();
                case "2" -> searchAccountByHolderId();
                case "3" -> Optional.empty(); // Return to menu
                default -> {
                    showMessage.accept("Opción inválida. Por favor, elige una opción del 1 al 3.");
                    yield Optional.empty();
                }
            };
        } catch (Exception e) {
            showMessage.accept("Error al buscar cuenta: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Searches for an account by account number.
     * 
     * @return Optional with search criteria (accountNumber), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchAccountByNumber() {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR CUENTA POR NÚMERO ===");
        
        String accountNumber = captureStringInput("Ingresa el número de cuenta: ", showMessage, getInput);
        
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            showMessage.accept("Buscando cuenta con número: " + accountNumber);
            
            Map<String, String> searchCriteria = Map.of("searchType", "accountNumber", "searchValue", accountNumber);
            return Optional.of(searchCriteria);
        } else {
            showMessage.accept("Error: El número de cuenta no puede estar vacío.");
            return Optional.empty();
        }
    }
    
    /**
     * Searches for accounts by holder ID.
     * 
     * @return Optional with search criteria (holderId), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchAccountByHolderId() {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR CUENTA POR ID DEL TITULAR ===");
        
        String holderId = captureStringInput("Ingresa el ID del titular: ", showMessage, getInput);
        
        if (holderId != null && !holderId.trim().isEmpty()) {
            showMessage.accept("Buscando cuentas del titular con ID: " + holderId);
            
            Map<String, String> searchCriteria = Map.of("searchType", "holderId", "searchValue", holderId);
            return Optional.of(searchCriteria);
        } else {
            showMessage.accept("Error: El ID del titular no puede estar vacío.");
            return Optional.empty();
        }
    }

    // Placeholder methods - will be implemented one by one
    @Override
    public Optional<String> showAccountMenu() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public Optional<String> showUpdateAccountMenu() {
        return Optional.empty(); // TODO: Implement
    }
    
    
    @Override
    public Optional<Map<String, String>> updateAccountBalance() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public Optional<Map<String, String>> updateAccountStatus() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public Optional<Map<String, String>> updateAccountHolder() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public Optional<Map<String, Object>> updateAccountMultipleFields() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public Optional<String> deleteAccount() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public Optional<String> restoreAccount() {
        return Optional.empty(); // TODO: Implement
    }
    
    @Override
    public void listAllAccounts(List<AccountDTO> accounts) {
        // TODO: Implement
    }
    
    @Override
    public boolean displayAccount(Account account) {
        return false; // TODO: Implement
    }

    @Override
    public void showMessage(String message) {
        showMessage.accept(message);
    }
}
