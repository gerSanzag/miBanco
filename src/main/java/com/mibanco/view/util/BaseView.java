package com.mibanco.view.util;

import com.mibanco.view.ConsoleIO;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import static com.mibanco.view.util.ValidationUtil.*;

public abstract class BaseView {
    
    protected final Consumer<String> showMessage;
    protected final Supplier<String> getInput;
    
    /**
     * Constructor that receives ConsoleIO dependency and creates functional interfaces
     * 
     * @param console the console input/output interface
     */
    protected BaseView(ConsoleIO console) {
        this.showMessage = console::writeOutput;
        this.getInput = console::readInput;
    }
    
    /**
     * Generic method to update entity fields with common validation and confirmation logic
     * 
     * @param viewMethod Supplier that provides the update data from the view
     * @param fieldName Name of the field being updated (for display purposes)
     * @param validationFunction Function to validate the new value
     * @param id Entity ID for display
     * @return Optional containing the update data if successful, empty otherwise
     */
    protected <T> Optional<Map<String, String>> updateEntityFieldGeneric(
            Supplier<Optional<Map<String, String>>> viewMethod,
            String fieldName,
            Function<String, Boolean> validationFunction,
            Long id
    ) {
        return viewMethod.get()
                .filter(updateData -> {
                    String newValue = updateData.get("newValue");
                    return newValue != null && !newValue.trim().isEmpty() && validationFunction.apply(newValue);
                })
                .filter(updateData -> {
                    String newValue = updateData.get("newValue");
                    showMessage.accept("ID del cliente: " + id);
                    showMessage.accept("Nuevo " + fieldName + ": " + newValue);
                    String confirm = getInput.get();
                    return "s".equalsIgnoreCase(confirm);
                });
    }
    
    /**
     * Generic method to update multiple entity fields with common validation and confirmation logic
     * 
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param entityIdPrompt Prompt to ask for entity ID
     * @param fieldCaptures List of field capture functions
     * @param fieldDisplayNames Map of field names to display names
     * @return Optional containing the updates map if successful, empty otherwise
     */
    protected Optional<Map<String, Object>> updateMultipleFieldsGeneric(
            String entityName,
            String entityIdPrompt,
            Map<String, Supplier<String>> fieldCaptures,
            Map<String, String> fieldDisplayNames
    ) {
        showMessage.accept("");
        showMessage.accept("=== ACTUALIZAR MÚLTIPLES CAMPOS DEL " + entityName.toUpperCase() + " ===");
        
        // Capture entity ID
        String idInput = captureStringInput(entityIdPrompt, showMessage, getInput);
        
        try {
            Long id = Long.parseLong(idInput);
            
            // Capture multiple fields
            showMessage.accept("");
            showMessage.accept("Ingresa los nuevos valores (deja vacío para no cambiar):");
            
            Map<String, Object> updates = new HashMap<>();
            
            // Capture each field
            fieldCaptures.forEach((fieldName, captureFunction) -> {
                String newValue = captureFunction.get();
                if (newValue != null && !newValue.trim().isEmpty()) {
                    updates.put(fieldName, newValue);
                }
            });
            
            // Add ID to the updates map for the controller
            updates.put("id", id);
            
            // Check if any fields were provided
            if (updates.size() <= 1) { // Only ID was added
                showMessage.accept("Error: No se proporcionaron campos para actualizar.");
                return Optional.empty();
            }
            
            // Show summary and ask for confirmation
            showMessage.accept("");
            showMessage.accept("=== RESUMEN DE ACTUALIZACIÓN ===");
            showMessage.accept("ID del " + entityName + ": " + id);
            
            updates.forEach((field, value) -> {
                if (!"id".equals(field)) {
                    String displayName = fieldDisplayNames.getOrDefault(field, field);
                    showMessage.accept("Nuevo " + displayName + ": " + value);
                }
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
    
    /**
     * Generic method to search entities with common menu logic
     * 
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param searchOptions Map of search options (option number -> search function)
     * @return Optional containing the search criteria if successful, empty otherwise
     */
    protected Optional<Map<String, String>> searchEntityGeneric(
            String entityName,
            Map<String, Supplier<Optional<Map<String, String>>>> searchOptions
    ) {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR " + entityName.toUpperCase() + " ===");
        
        // Show search options
        searchOptions.forEach((option, searchFunction) -> {
            String optionName = switch (option) {
                case "1" -> entityName.equals("cliente") ? "Buscar por ID" : "Buscar por Número de Cuenta";
                case "2" -> entityName.equals("cliente") ? "Buscar por DNI" : "Buscar por ID del Titular";
                default -> "Opción " + option;
            };
            showMessage.accept(option + ". " + optionName);
        });
        
        showMessage.accept((searchOptions.size() + 1) + ". Volver al menú de " + entityName + "s");
        showMessage.accept("");
        showMessage.accept("Elige una opción:");
        
        String option = getInput.get();
        
        // Check if user wants to return to menu
        String returnOption = String.valueOf(searchOptions.size() + 1);
        if (returnOption.equals(option)) {
            return Optional.empty();
        }
        
        // Execute search function for the selected option
        Supplier<Optional<Map<String, String>>> searchFunction = searchOptions.get(option);
        if (searchFunction != null) {
            return searchFunction.get();
        } else {
            showMessage.accept("Opción inválida. Por favor, elige una opción del 1 al " + (searchOptions.size() + 1) + ".");
            return Optional.empty();
        }
    }
    
    /**
     * Generic method to search by a specific field with common validation logic
     * 
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param fieldName Name of the field for display (e.g., "ID", "DNI", "Número de Cuenta")
     * @param fieldPrompt Prompt to ask for the field value
     * @param searchType Type identifier for the search (e.g., "id", "dni", "accountNumber")
     * @param validator Function to validate the input value
     * @return Optional containing the search criteria if successful, empty otherwise
     */
    protected Optional<Map<String, String>> searchByFieldGeneric(
            String entityName,
            String fieldName,
            String fieldPrompt,
            String searchType,
            Function<String, Boolean> validator
    ) {
        showMessage.accept("");
        showMessage.accept("=== BUSCAR " + entityName.toUpperCase() + " POR " + fieldName.toUpperCase() + " ===");
        
        String fieldValue = captureStringInput(fieldPrompt, showMessage, getInput);
        
        if (fieldValue != null && !fieldValue.trim().isEmpty() && validator.apply(fieldValue)) {
            showMessage.accept("Buscando " + entityName + " con " + fieldName.toLowerCase() + ": " + fieldValue);
            
            Map<String, String> searchCriteria = Map.of("searchType", searchType, "searchValue", fieldValue);
            return Optional.of(searchCriteria);
        } else {
            showMessage.accept("Error: El " + fieldName.toLowerCase() + " no puede estar vacío.");
            return Optional.empty();
        }
    }
    
    /**
     * Generic method to capture entity ID for delete/restore operations
     * 
     * @param action Action being performed (e.g., "ELIMINAR", "RESTAURAR")
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param idPrompt Prompt to ask for the entity ID
     * @return Optional containing the entity ID if successful, empty otherwise
     */
    protected Optional<String> captureEntityIdGeneric(
            String action,
            String entityName,
            String idPrompt
    ) {
        showMessage.accept("");
        showMessage.accept("=== " + action.toUpperCase() + " " + entityName.toUpperCase() + " ===");
        
        String idInput = captureStringInput(idPrompt, showMessage, getInput);
        
        if (idInput != null && !idInput.trim().isEmpty()) {
            return Optional.of(idInput);
        } else {
            return Optional.empty();
        }
    }
    
    /**
     * Generic method to list all entities with common display logic
     * 
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param entities List of entities to display
     * @param formatter Function to format each entity for display
     * @param <T> Type of the entity
     */
    protected <T> void listAllEntitiesGeneric(
            String entityName,
            List<T> entities,
            Function<T, String> formatter
    ) {
        showMessage.accept("");
        showMessage.accept("=== LISTA DE " + entityName.toUpperCase() + "S ===");
        
        if (entities == null || entities.isEmpty()) {
            showMessage.accept("No hay " + entityName + "s para mostrar.");
            return;
        }
        
        // Use forEach with lambda (implicit Consumer)
        entities.forEach(entity -> {
            showMessage.accept(formatter.apply(entity));
        });
        
        showMessage.accept("");
        showMessage.accept("Total de " + entityName + "s: " + entities.size());
    }
    
    /**
     * Generic method to display entity information with confirmation
     * 
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param entity The entity to display
     * @param formatter Function to format the entity information for display
     * @param <T> Type of the entity
     * @return true if user confirms the information is correct, false otherwise
     */
    protected <T> boolean displayEntityGeneric(
            String entityName,
            T entity,
            Function<T, String> formatter
    ) {
        showMessage.accept("");
        showMessage.accept("=== INFORMACIÓN DEL " + entityName.toUpperCase() + " ===");
        
        // Display entity information using the formatter
        String[] lines = formatter.apply(entity).split("\n");
        for (String line : lines) {
            showMessage.accept(line);
        }
        
        showMessage.accept("");
        
        String confirm = captureStringInput("¿Los datos mostrados son correctos? (s/n): ", showMessage, getInput);
        return "s".equalsIgnoreCase(confirm);
    }
    
    /**
     * Generic method to capture entity data with common validation and confirmation logic
     * 
     * @param entityName Name of the entity (e.g., "cliente", "cuenta")
     * @param fieldCaptures Map of field names to capture functions
     * @param summaryFormatter Function to format the summary for display
     * @param dataMapper Function to create the final data map
     * @return Optional containing the captured data if successful, empty otherwise
     */
    protected Optional<Map<String, String>> captureEntityDataGeneric(
            String entityName,
            Map<String, Supplier<String>> fieldCaptures,
            Function<Map<String, String>, String> summaryFormatter,
            Function<Map<String, String>, Map<String, String>> dataMapper
    ) {
        showMessage.accept("");
        showMessage.accept("=== CREAR NUEVO " + entityName.toUpperCase() + " ===");
        
        try {
            // Capture all fields
            Map<String, String> capturedData = new HashMap<>();
            
            fieldCaptures.forEach((fieldName, captureFunction) -> {
                String value = captureFunction.get();
                if (value != null) {
                    capturedData.put(fieldName, value);
                }
            });
            
            // Show summary and ask for confirmation
            showMessage.accept("");
            showMessage.accept("=== RESUMEN DEL " + entityName.toUpperCase() + " ===");
            
            String[] summaryLines = summaryFormatter.apply(capturedData).split("\n");
            for (String line : summaryLines) {
                showMessage.accept(line);
            }
            
            String confirm = captureStringInput("¿Confirmar creación? (s/n): ", showMessage, getInput);
            
            if ("s".equalsIgnoreCase(confirm)) {
                // Create final data map using the mapper
                Map<String, String> finalData = dataMapper.apply(capturedData);
                
                showMessage.accept("Datos capturados exitosamente.");
                return Optional.of(finalData);
            } else {
                showMessage.accept("Creación cancelada.");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            showMessage.accept("Error al crear " + entityName + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
