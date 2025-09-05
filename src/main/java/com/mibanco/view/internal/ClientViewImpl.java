package com.mibanco.view.internal;

import com.mibanco.view.ClientView;
import com.mibanco.view.ConsoleIO;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.List;
import com.mibanco.model.Client;
import com.mibanco.dto.ClientDTO;
import com.mibanco.util.ReflectionUtil;
import static com.mibanco.view.util.ValidationUtil.*;
import com.mibanco.view.util.ViewUpdateUtil;

/**
 * Implementation of the client management view.
 * Handles user interaction for client operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public class ClientViewImpl implements ClientView {
    
    
    // Functional interfaces for console operations
    private final Consumer<String> showMessage;
    private final Supplier<String> getInput;
    
    /**
     * Constructor that injects console dependency.
     * 
     * @param console the console input/output interface
     */
    public ClientViewImpl(ConsoleIO console) {
    
        // Initialize functional interfaces
        this.showMessage = console::writeOutput;
        this.getInput = console::readInput;
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
            return ViewUpdateUtil.updateEntityFieldGeneric(id, newEmail, "email", "cliente", showMessage, getInput);
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
            return ViewUpdateUtil.updateEntityFieldGeneric(id, newPhone, "teléfono", "cliente", showMessage, getInput);
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
            return ViewUpdateUtil.updateEntityFieldGeneric(id, newAddress, "dirección", "cliente", showMessage, getInput);
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<String, Object>> updateClientMultipleFields() {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR MÚLTIPLES CAMPOS DEL CLIENTE ===");
        
        // Capture client ID
        String idInput = captureStringInput("Ingresa el ID del cliente: ", showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            
            // Capture multiple fields
            showMessage.accept("");
            showMessage.accept("Ingresa los nuevos valores (deja vacío para no cambiar):");
            
            String newEmail = captureEmailInput("Nuevo email: ", showMessage, getInput);
            String newPhone = captureStringInput("Nuevo teléfono: ", showMessage, getInput);
            String newAddress = captureStringInput("Nueva dirección: ", showMessage, getInput);
            
            // Build updates map with only non-empty values
            Map<String, Object> updates = new HashMap<>();
            
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                updates.put("email", newEmail);
            }
            if (newPhone != null && !newPhone.trim().isEmpty()) {
                updates.put("phone", newPhone);
            }
            if (newAddress != null && !newAddress.trim().isEmpty()) {
                updates.put("address", newAddress);
            }
            
            // Add ID to the updates map for the controller
            updates.put("id", id);
            
            // Check if any fields were provided
            if (updates.isEmpty()) {
                showMessage.accept("Error: No se proporcionaron campos para actualizar.");
                return Optional.empty();
            }
            
            // Show summary and ask for confirmation
            showMessage.accept("");
            showMessage.accept("=== RESUMEN DE ACTUALIZACIÓN ===");
            showMessage.accept("ID del cliente: " + id);
            
            updates.forEach((field, value) -> {
                String fieldName = switch (field) {
                    case "email" -> "Email";
                    case "phone" -> "Teléfono";
                    case "address" -> "Dirección";
                    default -> field;
                };
                showMessage.accept("Nuevo " + fieldName + ": " + value);
            });
            
            String confirm = captureStringInput("¿Confirmar actualización? (s/n): ", showMessage, getInput);
            
            if ("s".equalsIgnoreCase(confirm)) {
                showMessage.accept("Datos de actualización múltiple capturados exitosamente.");
                return Optional.of(updates);
            } else {
                showMessage.accept("Actualización cancelada por el usuario.");
                return Optional.empty();
            }
            
        } catch (NumberFormatException e) {
            showMessage.accept("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<String> deleteClient() {
        showMessage.accept("");
        showMessage.accept("=== ELIMINAR CLIENTE ===");
        
        // Capture client ID as string (controller will handle validation)
        String idInput = captureStringInput("Ingresa el ID del cliente a eliminar: ", showMessage, getInput);
        
        if (idInput != null && !idInput.trim().isEmpty()) {
            return Optional.of(idInput);
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<String> restoreClient() {
        showMessage.accept("");
        showMessage.accept("=== RESTAURAR CLIENTE ===");
        
        // Capture client ID as string (controller will handle validation)
        String idInput = captureStringInput("Ingresa el ID del cliente a restaurar: ", showMessage, getInput);
        
        if (idInput != null && !idInput.trim().isEmpty()) {
            return Optional.of(idInput);
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public void listAllClients(List<ClientDTO> clients) {
        showMessage.accept("");
        showMessage.accept("=== LISTA DE CLIENTES ===");
        
        if (clients == null || clients.isEmpty()) {
            showMessage.accept("No hay clientes para mostrar.");
            return;
        }
        
        // Usar forEach con lambda (Consumer implícito)
        clients.forEach(client -> {
            showMessage.accept("ID: " + client.getId() + 
                             " - Nombre: " + client.getFirstName() + " " + client.getLastName() +
                             " - DNI: " + client.getDni() +
                             " - Email: " + client.getEmail());
        });
        
        showMessage.accept("");
        showMessage.accept("Total de clientes: " + clients.size());
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

    @Override
    public void showMessage(String message) {
        showMessage.accept(message);
    }
}
