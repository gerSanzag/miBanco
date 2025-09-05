package com.mibanco.view.internal;

import com.mibanco.view.ClientView;
import com.mibanco.view.ConsoleIO;
import com.mibanco.view.util.BaseView;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import com.mibanco.model.Client;
import com.mibanco.dto.ClientDTO;
import com.mibanco.util.ReflectionUtil;
import static com.mibanco.view.util.ValidationUtil.*;

/**
 * Implementation of the client management view.
 * Handles user interaction for client operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public class ClientViewImpl extends BaseView implements ClientView {
    
    /**
     * Constructor that injects console dependency.
     * 
     * @param console the console input/output interface
     */
    public ClientViewImpl(ConsoleIO console) {
        super(console); // Pass console to BaseView
    }
    
    /**
     * Shows the main client management menu.
     * Returns the selected option for the controller to handle.
     * 
     * @return Optional with the selected menu option, or empty if user wants to exit
     */
    @Override
    public Optional<String> showClientMenu() {
        showMainClientMenu();
        String option = getInput.get();
        
        // Return empty if user wants to exit (option "7")
        return Optional.of(option)
            .filter(opt -> !"7".equals(opt))
            .or(() -> Optional.empty());
    }
    
    /**
     * Shows the client update submenu.
     * Returns the selected option for the controller to handle.
     * 
     * @return Optional with the selected menu option, or empty if user wants to exit
     */
    @Override
    public Optional<String> showUpdateClientMenu() {
        showUpdateClientSubmenu();
        String option = getInput.get();
        
        // Return empty if user wants to exit (option "5")
        return Optional.of(option)
            .filter(opt -> !"5".equals(opt))
            .or(() -> Optional.empty());
    }
    
    /**
     * Shows the main client management menu.
     */
    private void showMainClientMenu() {
        showMessage.accept("");
        showMessage.accept("=== GESTIÓN DE CLIENTES ===");
        showMessage.accept("1. Crear Cliente");
        showMessage.accept("2. Buscar Cliente");
        showMessage.accept("3. Actualizar Cliente");
        showMessage.accept("4. Eliminar Cliente");
        showMessage.accept("5. Restaurar Cliente");
        showMessage.accept("6. Listar Clientes");
        showMessage.accept("7. Volver al menú principal");
        showMessage.accept("");
        showMessage.accept("Elige una opción:");
    }
    
    /**
     * Shows the client update submenu.
     */
    private void showUpdateClientSubmenu() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR CLIENTE ===");
        showMessage.accept("1. Actualizar Email");
        showMessage.accept("2. Actualizar Teléfono");
        showMessage.accept("3. Actualizar Dirección");
        showMessage.accept("4. Actualizar Múltiples Campos");
        showMessage.accept("5. Volver al menú de clientes");
        showMessage.accept("");
        showMessage.accept("Elige una opción:");
    }
    
    
    // Implementation of client operations (placeholder methods for now)
    
    @Override
    public Optional<Map<String, String>> captureDataClient() {
        // Define field capture functions
        Map<String, Supplier<String>> fieldCaptures = new HashMap<>();
        fieldCaptures.put("firstName", () -> captureStringInput("Nombre: ", showMessage, getInput));
        fieldCaptures.put("lastName", () -> captureStringInput("Apellido: ", showMessage, getInput));
        fieldCaptures.put("dni", () -> captureStringInput("DNI: ", showMessage, getInput));
        fieldCaptures.put("birthDate", () -> captureStringInput("Fecha de nacimiento (YYYY-MM-DD): ", showMessage, getInput));
        fieldCaptures.put("email", () -> captureEmailInput("Email: ", showMessage, getInput));
        fieldCaptures.put("phone", () -> captureStringInput("Teléfono: ", showMessage, getInput));
        fieldCaptures.put("address", () -> captureStringInput("Dirección: ", showMessage, getInput));
        
        // Define summary formatter
        Function<Map<String, String>, String> summaryFormatter = data -> 
            "Nombre: " + data.get("firstName") + " " + data.get("lastName") + "\n" +
            "DNI: " + data.get("dni") + "\n" +
            "Fecha de nacimiento: " + data.get("birthDate") + "\n" +
            "Email: " + data.get("email") + "\n" +
            "Teléfono: " + data.get("phone") + "\n" +
            "Dirección: " + data.get("address");
        
        // Define data mapper
        Function<Map<String, String>, Map<String, String>> dataMapper = data -> 
            createClientDataMap(
                data.get("firstName"), data.get("lastName"), data.get("dni"), 
                data.get("birthDate"), data.get("email"), data.get("phone"), data.get("address")
            );
        
        return captureEntityDataGeneric("cliente", fieldCaptures, summaryFormatter, dataMapper);
    }
    
    @Override
    public Optional<Map<String, String>> searchClient() {
        // Define search options
        Map<String, Supplier<Optional<Map<String, String>>>> searchOptions = new HashMap<>();
        searchOptions.put("1", this::searchClientById);
        searchOptions.put("2", this::searchClientByDni);
        
        return searchEntityGeneric("cliente", searchOptions);
    }
    
    /**
     * Searches for a client by ID.
     * 
     * @return Optional with search criteria (ID), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchClientById() {
        return searchByFieldGeneric(
            "cliente",
            "ID",
            "Ingresa el ID del cliente: ",
            "id",
            value -> {
                try {
                    Long.parseLong(value);
                    return true;
                } catch (NumberFormatException e) {
                    showMessage.accept("Error: El ID debe ser un número válido.");
                    return false;
                }
            }
        );
    }
    
    /**
     * Searches for a client by DNI.
     * 
     * @return Optional with search criteria (DNI), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchClientByDni() {
        return searchByFieldGeneric(
            "cliente",
            "DNI",
            "Ingresa el DNI del cliente: ",
            "dni",
            value -> true // DNI validation can be added here if needed
        );
    }
    
    @Override
    public Optional<Map<String, String>> updateClientEmail() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR EMAIL DEL CLIENTE ===");
        
        String idInput = captureStringInput("Ingresa el ID del cliente: ", showMessage, getInput);
        String newEmail = captureEmailInput("Ingresa el nuevo email: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            return updateEntityFieldGeneric(
                () -> Optional.of(Map.of("id", id.toString(), "newValue", newEmail)),
                "email",
                value -> value != null && !value.trim().isEmpty(),
                id
            );
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    
    @Override
    public Optional<Map<String, String>> updateClientPhone() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR TELÉFONO DEL CLIENTE ===");
        
        String idInput = captureStringInput("Ingresa el ID del cliente: ", showMessage, getInput);
        String newPhone = captureStringInput("Ingresa el nuevo teléfono: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            return updateEntityFieldGeneric(
                () -> Optional.of(Map.of("id", id.toString(), "newValue", newPhone)),
                "teléfono",
                value -> value != null && !value.trim().isEmpty(),
                id
            );
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<String, String>> updateClientAddress() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR DIRECCIÓN DEL CLIENTE ===");
        
        String idInput = captureStringInput("Ingresa el ID del cliente: ", showMessage, getInput);
        String newAddress = captureStringInput("Ingresa la nueva dirección: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            return updateEntityFieldGeneric(
                () -> Optional.of(Map.of("id", id.toString(), "newValue", newAddress)),
                "dirección",
                value -> value != null && !value.trim().isEmpty(),
                id
            );
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<String, Object>> updateClientMultipleFields() {
        // Define field capture functions
        Map<String, Supplier<String>> fieldCaptures = new HashMap<>();
        fieldCaptures.put("email", () -> captureEmailInput("Nuevo email: ", showMessage, getInput));
        fieldCaptures.put("phone", () -> captureStringInput("Nuevo teléfono: ", showMessage, getInput));
        fieldCaptures.put("address", () -> captureStringInput("Nueva dirección: ", showMessage, getInput));
        
        // Define field display names
        Map<String, String> fieldDisplayNames = new HashMap<>();
        fieldDisplayNames.put("email", "Email");
        fieldDisplayNames.put("phone", "Teléfono");
        fieldDisplayNames.put("address", "Dirección");
        
        return updateMultipleFieldsGeneric(
            "cliente",
            "Ingresa el ID del cliente: ",
            fieldCaptures,
            fieldDisplayNames
        );
    }
    
    @Override
    public Optional<String> deleteClient() {
        return captureEntityIdGeneric(
            "ELIMINAR",
            "cliente",
            "Ingresa el ID del cliente a eliminar: "
        );
    }
    
    @Override
    public Optional<String> restoreClient() {
        return captureEntityIdGeneric(
            "RESTAURAR",
            "cliente",
            "Ingresa el ID del cliente a restaurar: "
        );
    }
    
    @Override
    public void listAllClients(List<ClientDTO> clients) {
        listAllEntitiesGeneric(
            "cliente",
            clients,
            client -> "ID: " + client.getId() + 
                     " - Nombre: " + client.getFirstName() + " " + client.getLastName() +
                     " - DNI: " + client.getDni() +
                     " - Email: " + client.getEmail()
        );
    }
    
    // Helper methods for data capture and validation
    
    // Input capture methods moved to ValidationUtil class for reusability
    
    // Validation methods moved to ValidationUtil class for reusability
    
    /**
     * Creates a Map with client data using dynamic field names from reflection.
     * 
     * @param firstName the first name
     * @param lastName the last name
     * @param dni the DNI
     * @param birthDate the birth date
     * @param email the email
     * @param phone the phone
     * @param address the address
     * @return Map with client data using dynamic keys
     */
    private Map<String, String> createClientDataMap(String firstName, String lastName, String dni, 
                                                   String birthDate, String email, String phone, String address) {
        Map<String, String> clientData = new HashMap<>();
        
        // Get required fields dynamically using reflection
        List<String> requiredFields = ReflectionUtil.getRequiredFields(Client.class);
        
        // Create a map of field names to captured values
        Map<String, String> capturedData = new HashMap<>();
        capturedData.put("firstName", firstName);
        capturedData.put("lastName", lastName);
        capturedData.put("dni", dni);
        capturedData.put("birthDate", birthDate);
        capturedData.put("email", email);
        capturedData.put("phone", phone);
        capturedData.put("address", address);
        
        // Use required fields as keys to ensure consistency with the entity
        for (String fieldName : requiredFields) {
            String fieldValue = capturedData.get(fieldName);
            if (fieldValue != null) {
                clientData.put(fieldName, fieldValue);
            }
        }
        
        return clientData;
    }
    
    @Override
    public boolean displayClient(Client client) {
        return displayEntityGeneric(
            "cliente",
            client,
            c -> "ID: " + c.getId() + "\n" +
                 "Nombre: " + c.getFirstName() + " " + c.getLastName() + "\n" +
                 "DNI: " + c.getDni() + "\n" +
                 "Fecha de nacimiento: " + c.getBirthDate() + "\n" +
                 "Email: " + c.getEmail() + "\n" +
                 "Teléfono: " + c.getPhone() + "\n" +
                 "Dirección: " + c.getAddress()
        );
    }

    @Override
    public void showMessage(String message) {
        showMessage.accept(message);
    }
}
