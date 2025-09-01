package com.mibanco.view.internal;

import com.mibanco.view.ClientView;
import com.mibanco.view.ConsoleIO;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.List;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Client;
import com.mibanco.util.ReflectionUtil;
import static com.mibanco.view.util.ValidationUtil.*;

/**
 * Implementation of the client management view.
 * Handles user interaction for client operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public class ClientViewImpl implements ClientView {
    
    private final ConsoleIO console;
    
    // Functional interfaces for console operations
    private final Consumer<String> showMessage;
    private final Supplier<String> getInput;
    
    /**
     * Constructor that injects console dependency.
     * 
     * @param console the console input/output interface
     */
    public ClientViewImpl(ConsoleIO console) {
        this.console = console;
        
        // Initialize functional interfaces
        this.showMessage = console::writeOutput;
        this.getInput = console::readInput;
    }
    
    /**
     * Shows the main client management menu and handles user interaction.
     */
    @Override
    public void showClientMenu() {
        AtomicBoolean running = new AtomicBoolean(true);
        
        while (running.get()) {
            showMainClientMenu();
            String option = getInput.get();
            
            // Functional approach to option processing
            Optional.of(option)
                .filter("7"::equals)
                .ifPresentOrElse(
                    exit -> running.set(false),
                    () -> processMainClientMenuOption(option)
                );
        }
    }
    
    /**
     * Shows the client update submenu and handles user interaction.
     */
    @Override
    public void showUpdateClientMenu() {
        AtomicBoolean running = new AtomicBoolean(true);
        
        while (running.get()) {
            showUpdateClientSubmenu();
            String option = getInput.get();
            
            // Functional approach to option processing
            Optional.of(option)
                .filter("5"::equals)
                .ifPresentOrElse(
                    exit -> running.set(false),
                    () -> processUpdateClientMenuOption(option)
                );
        }
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
    
    /**
     * Processes the selected main client menu option.
     * 
     * @param option the selected option
     */
    private void processMainClientMenuOption(String option) {
        switch (option) {
            case "1" -> captureDataClient();
            case "2" -> searchClient();
            case "3" -> showUpdateClientMenu();
            case "4" -> deleteClient();
            case "5" -> restoreClient();
            case "6" -> listAllClients();
            default -> showMessage.accept("Opción inválida. Por favor, elige una opción del 1 al 7.");
        }
    }
    
    /**
     * Processes the selected update client menu option.
     * 
     * @param option the selected option
     */
    private void processUpdateClientMenuOption(String option) {
        switch (option) {
            case "1" -> updateClientEmail();
            case "2" -> updateClientPhone();
            case "3" -> updateClientAddress();
            case "4" -> updateClientMultipleFields();
            default -> showMessage.accept("Opción inválida. Por favor, elige una opción del 1 al 5.");
        }
    }
    
    // Implementation of client operations (placeholder methods for now)
    
    @Override
    public Optional<Map<String, String>> captureDataClient() {
        showMessage.accept("");
        showMessage.accept("=== CREAR NUEVO CLIENTE ===");
        
        try {
            // Capture client data
            String firstName = captureStringInput("Nombre: ", showMessage, getInput);
            String lastName = captureStringInput("Apellido: ", showMessage, getInput);
            String dni = captureStringInput("DNI: ", showMessage, getInput);
            String birthDate = captureStringInput("Fecha de nacimiento (YYYY-MM-DD): ", showMessage, getInput);
            String email = captureEmailInput("Email: ", showMessage, getInput);
            String phone = captureStringInput("Teléfono: ", showMessage, getInput);
            String address = captureStringInput("Dirección: ", showMessage, getInput);
            
            // No validation here - Controller will handle all validations
            
            // Show confirmation
            showMessage.accept("");
            showMessage.accept("=== RESUMEN DEL CLIENTE ===");
            showMessage.accept("Nombre: " + firstName + " " + lastName);
            showMessage.accept("DNI: " + dni);
            showMessage.accept("Fecha de nacimiento: " + birthDate);
            showMessage.accept("Email: " + email);
            showMessage.accept("Teléfono: " + phone);
            showMessage.accept("Dirección: " + address);
            
            // Ask for confirmation
            String confirm = captureStringInput("¿Confirmar creación? (s/n): ", showMessage, getInput);
            
            if ("s".equalsIgnoreCase(confirm)) {
                // Create Map with client data for the service using reflection
                Map<String, String> clientData = createClientDataMap(firstName, lastName, dni, birthDate, email, phone, address);
                
                showMessage.accept("Datos capturados exitosamente.");
                return Optional.of(clientData);
            } else {
                showMessage.accept("Creación cancelada.");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            showMessage.accept("Error al crear cliente: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<String, String>> searchClient() {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR CLIENTE ===");
        showMessage.accept("1. Buscar por ID");
        showMessage.accept("2. Buscar por DNI");
        showMessage.accept("3. Volver al menú de clientes");
        showMessage.accept("");
        showMessage.accept("Elige una opción:");
        
        String option = getInput.get();
        
        return switch (option) {
            case "1" -> searchClientById();
            case "2" -> searchClientByDni();
            case "3" -> Optional.empty(); // Return to menu
            default -> {
                showMessage.accept("Opción inválida. Por favor, elige una opción del 1 al 3.");
                yield Optional.empty();
            }
        };
    }
    
    /**
     * Searches for a client by ID.
     * 
     * @return Optional with search criteria (ID), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchClientById() {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR CLIENTE POR ID ===");
        
        String idInput = captureStringInput("Ingresa el ID del cliente: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            showMessage.accept("Buscando cliente con ID: " + id);
            
            Map<String, String> searchCriteria = Map.of("searchType", "id", "searchValue", id.toString());
            return Optional.of(searchCriteria);
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    /**
     * Searches for a client by DNI.
     * 
     * @return Optional with search criteria (DNI), or empty if search was cancelled
     */
    private Optional<Map<String, String>> searchClientByDni() {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR CLIENTE POR DNI ===");
        
        String dni = captureStringInput("Ingresa el DNI del cliente: ", showMessage, getInput);
        
        if (dni != null && !dni.trim().isEmpty()) {
            showMessage.accept("Buscando cliente con DNI: " + dni);
            
            Map<String, String> searchCriteria = Map.of("searchType", "dni", "searchValue", dni);
            return Optional.of(searchCriteria);
        } else {
            showMessage.accept("Error: El DNI no puede estar vacío.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<String, String>> updateClientEmail() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR EMAIL DEL CLIENTE ===");
        
        String idInput = captureStringInput("Ingresa el ID del cliente: ", showMessage, getInput);
        String newEmail = captureEmailInput("Ingresa el nuevo email: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            
            return Optional.of(newEmail)
                .filter(email -> email != null && !email.trim().isEmpty())
                .map(email -> {
                    showMessage.accept("");
                    showMessage.accept("=== RESUMEN DE ACTUALIZACIÓN ===");
                    showMessage.accept("ID del cliente: " + id);
                    showMessage.accept("Nuevo email: " + email);
                    
                    return captureStringInput("¿Confirmar actualización? (s/n): ", showMessage, getInput);
                })
                .filter(confirm -> "s".equalsIgnoreCase(confirm))
                .map(confirm -> {
                    Map<String, String> updateData = Map.of("id", id.toString(), "newEmail", newEmail);
                    showMessage.accept("Datos de actualización capturados exitosamente.");
                    return updateData;
                })
                .map(updateData -> Optional.of(updateData))
                .orElseGet(() -> {
                    showMessage.accept("Error: El email no puede estar vacío o la actualización fue cancelada.");
                    return Optional.empty();
                });
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public boolean updateClientPhone() {
        showMessage.accept("Actualizar Teléfono - En desarrollo");
        return true;
    }
    
    @Override
    public boolean updateClientAddress() {
        showMessage.accept("Actualizar Dirección - En desarrollo");
        return true;
    }
    
    @Override
    public boolean updateClientMultipleFields() {
        showMessage.accept("Actualizar Múltiples Campos - En desarrollo");
        return true;
    }
    
    @Override
    public boolean deleteClient() {
        showMessage.accept("Eliminar Cliente - En desarrollo");
        return true;
    }
    
    @Override
    public boolean restoreClient() {
        showMessage.accept("Restaurar Cliente - En desarrollo");
        return true;
    }
    
    @Override
    public boolean listAllClients() {
        showMessage.accept("Listar Clientes - En desarrollo");
        return true;
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
        showMessage.accept("");
        showMessage.accept("=== INFORMACIÓN DEL CLIENTE ===");
        showMessage.accept("ID: " + client.getId());
        showMessage.accept("Nombre: " + client.getFirstName() + " " + client.getLastName());
        showMessage.accept("DNI: " + client.getDni());
        showMessage.accept("Fecha de nacimiento: " + client.getBirthDate());
        showMessage.accept("Email: " + client.getEmail());
        showMessage.accept("Teléfono: " + client.getPhone());
        showMessage.accept("Dirección: " + client.getAddress());
        showMessage.accept("");
        
        String confirm = captureStringInput("¿Los datos mostrados son correctos? (s/n): ", showMessage, getInput);
        return "s".equalsIgnoreCase(confirm);
    }
}
