package com.mibanco.view.internal;

import com.mibanco.view.AccountView;
import com.mibanco.view.ConsoleIO;
import com.mibanco.view.util.BaseView;
import com.mibanco.model.Account;
import com.mibanco.util.ReflectionUtil;
import static com.mibanco.view.util.ValidationUtil.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.List;
import com.mibanco.dto.AccountDTO;

/**
 * Implementation of the account management view.
 * Handles user interaction for account operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public class AccountViewImpl extends BaseView implements AccountView {
    
    /**
     * Constructor that injects console dependency.
     * 
     * @param console the console input/output interface
     */
    public AccountViewImpl(ConsoleIO console) {
        super(console); // Pass console to BaseView
    }
    
    @Override
    public Optional<Map<String, String>> captureDataAccount() {
        // Define field capture functions
        Map<String, Supplier<String>> fieldCaptures = new HashMap<>();
        fieldCaptures.put("idHolder", () -> captureStringInput("ID del titular: ", showMessage, getInput));
        fieldCaptures.put("accountType", () -> captureAccountTypeInput("Tipo de cuenta (1=AHORRO, 2=CORRIENTE, 3=PLAZO_FIJO): ", showMessage, getInput));
        fieldCaptures.put("initialBalance", () -> captureStringInput("Saldo inicial: ", showMessage, getInput));
        
        // Define summary formatter
        Function<Map<String, String>, String> summaryFormatter = data -> 
            "ID del titular: " + data.get("idHolder") + "\n" +
            "Tipo de cuenta: " + data.get("accountType") + "\n" +
            "Saldo inicial: " + data.get("initialBalance");
        
        // Define data mapper with validation
        Function<Map<String, String>, Map<String, String>> dataMapper = data -> {
            // Validate that accountType was captured successfully
            if (data.get("accountType") == null) {
                showMessage.accept("Error: No se pudo capturar el tipo de cuenta correctamente.");
                return null;
            }
            return createAccountDataMap(data.get("idHolder"), data.get("accountType"), data.get("initialBalance"));
        };
        
        return captureEntityDataGeneric("cuenta", fieldCaptures, summaryFormatter, dataMapper);
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
            // Define search options
            Map<String, Supplier<Optional<Map<String, String>>>> searchOptions = new HashMap<>();
            searchOptions.put("1", this::searchAccountByNumber);
            searchOptions.put("2", this::searchAccountByHolderId);
            
            return searchEntityGeneric("cuenta", searchOptions);
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
        return searchByFieldGeneric(
            "cuenta",
            "Número de Cuenta",
            "Ingresa el número de cuenta: ",
            "accountNumber",
            value -> true // Account number validation can be added here if needed
        );
    }
    
    /**
     * Searches for accounts by holder ID.
     * 
     * @return Optional with search criteria (holderId), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchAccountByHolderId() {
        return searchByFieldGeneric(
            "cuenta",
            "ID del Titular",
            "Ingresa el ID del titular: ",
            "holderId",
            value -> {
                try {
                    Long.parseLong(value);
                    return true;
                } catch (NumberFormatException e) {
                    showMessage.accept("Error: El ID del titular debe ser un número válido.");
                    return false;
                }
            }
        );
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
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR SALDO DE LA CUENTA ===");
        
        String idInput = captureStringInput("Ingresa el ID de la cuenta: ", showMessage, getInput);
        String newBalance = captureStringInput("Ingresa el nuevo saldo: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            return updateEntityFieldGeneric(
                () -> Optional.of(Map.of("id", id.toString(), "newValue", newBalance)),
                "saldo",
                value -> value != null && !value.trim().isEmpty(),
                id
            );
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    
    @Override
    public Optional<Map<String, String>> updateAccountStatus() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR ESTADO DE LA CUENTA ===");
        
        String idInput = captureStringInput("Ingresa el ID de la cuenta: ", showMessage, getInput);
        String newStatus = captureAccountStatusInput("Ingresa el nuevo estado (1=activa, 0=inactiva): ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            return updateEntityFieldGeneric(
                () -> Optional.of(Map.of("id", id.toString(), "newValue", newStatus)),
                "estado",
                value -> value != null && !value.trim().isEmpty(),
                id
            );
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    /**
     * Captures account status input with validation.
     * 
     * @param prompt the prompt to show to the user
     * @param showMessage consumer to display messages
     * @param getInput supplier to get user input
     * @return the account status if valid, null if invalid
     */
    private String captureAccountStatusInput(String prompt, Consumer<String> showMessage, Supplier<String> getInput) {
        String input = captureStringInput(prompt, showMessage, getInput);
        
        if (input != null && !input.trim().isEmpty()) {
            // Validate numeric input
            String status = switch (input.trim()) {
                case "1" -> "true";
                case "0" -> "false";
                default -> null;
            };
            
            if (status != null) {
                return status;
            } else {
                showMessage.accept("Error: Estado inválido. Use 1 para activa o 0 para inactiva.");
                return null;
            }
        }
        
        return input;
    }
    
    @Override
    public Optional<Map<String, String>> updateAccountHolder() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR TITULAR DE LA CUENTA ===");
        
        String idInput = captureStringInput("Ingresa el ID de la cuenta: ", showMessage, getInput);
        String newHolderId = captureStringInput("Ingresa el ID del nuevo titular: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            Long.parseLong(newHolderId); // Validate that holderId is a valid number
            return updateEntityFieldGeneric(
                () -> Optional.of(Map.of("id", id.toString(), "newValue", newHolderId)),
                "ID del titular",
                value -> value != null && !value.trim().isEmpty(),
                id
            );
        } catch (NumberFormatException e) {
            showMessage.accept("Error: Los IDs deben ser números válidos.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<String, Object>> updateAccountMultipleFields() {
        // Define field capture functions
        Map<String, Supplier<String>> fieldCaptures = new HashMap<>();
        fieldCaptures.put("balance", () -> captureStringInput("Nuevo saldo: ", showMessage, getInput));
        fieldCaptures.put("active", () -> captureAccountStatusInput("Nuevo estado (1=Activo, 0=Inactivo): ", showMessage, getInput));
        fieldCaptures.put("idHolder", () -> captureStringInput("Nuevo ID del titular: ", showMessage, getInput));
        
        // Define field display names
        Map<String, String> fieldDisplayNames = new HashMap<>();
        fieldDisplayNames.put("balance", "Saldo");
        fieldDisplayNames.put("active", "Estado");
        fieldDisplayNames.put("idHolder", "ID del Titular");
        
        return updateMultipleFieldsGeneric(
            "cuenta",
            "Ingresa el ID de la cuenta: ",
            fieldCaptures,
            fieldDisplayNames
        );
    }
    
    @Override
    public Optional<String> deleteAccount() {
        return captureEntityIdGeneric(
            "ELIMINAR",
            "cuenta",
            "Ingresa el ID de la cuenta a eliminar: "
        );
    }
    
    @Override
    public Optional<String> restoreAccount() {
        return captureEntityIdGeneric(
            "RESTAURAR",
            "cuenta",
            "Ingresa el ID de la cuenta a restaurar: "
        );
    }
    
    @Override
    public void listAllAccounts(List<AccountDTO> accounts) {
        listAllEntitiesGeneric(
            "cuenta",
            accounts,
            account -> "Número: " + account.getAccountNumber() + 
                      " - Tipo: " + account.getType() +
                      " - Saldo: " + account.getBalance() +
                      " - Estado: " + (account.isActive() ? "Activo" : "Inactivo") +
                      " - Titular: " + (account.getHolder() != null ? account.getHolder().getFirstName() + " " + account.getHolder().getLastName() : "Sin titular")
        );
    }
    
    @Override
    public boolean displayAccount(Account account) {
        return displayEntityGeneric(
            "cuenta",
            account,
            a -> "Número: " + a.getAccountNumber() + "\n" +
                 "Tipo: " + a.getType() + "\n" +
                 "Fecha de creación: " + a.getCreationDate() + "\n" +
                 "Saldo inicial: " + a.getInitialBalance() + "\n" +
                 "Saldo actual: " + a.getBalance() + "\n" +
                 "Estado: " + (a.isActive() ? "Activo" : "Inactivo") + "\n" +
                 "Titular: " + (a.getHolder() != null ? a.getHolder().getFirstName() + " " + a.getHolder().getLastName() : "Sin titular")
        );
    }

    @Override
    public void showMessage(String message) {
        showMessage.accept(message);
    }
}
