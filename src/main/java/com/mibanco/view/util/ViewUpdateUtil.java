package com.mibanco.view.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for common view update operations.
 * Provides generic methods that can be reused across different entity views.
 * Follows functional programming approach with Optionals and lambdas.
 */
public class ViewUpdateUtil {
    
    /**
     * Generic method for updating entity fields in views.
     * Handles the common logic for validation, confirmation and data return.
     * Receives data already captured by the specific methods.
     * 
     * @param id Entity ID already captured and validated
     * @param newValue New field value already captured
     * @param fieldDescription Field description for user messages (e.g., "email", "saldo", "estado")
     * @param entityType Type of entity for user messages (e.g., "cliente", "cuenta")
     * @param showMessage Consumer to display messages
     * @param getInput Supplier to get user input
     * @return Optional with Map containing update data (id and newValue), or empty if update was cancelled
     */
    public static Optional<Map<String, String>> updateEntityFieldGeneric(
            Long id,
            String newValue,
            String fieldDescription,
            String entityType,
            Consumer<String> showMessage,
            Supplier<String> getInput
    ) {
        return Optional.of(newValue)
            .filter(value -> value != null && !value.trim().isEmpty())
            .map(value -> {
                showMessage.accept("");
                showMessage.accept("=== RESUMEN DE ACTUALIZACIÓN ===");
                showMessage.accept("ID del " + entityType + ": " + id);
                showMessage.accept("Nuevo " + fieldDescription + ": " + value);
                
                return ValidationUtil.captureStringInput("¿Confirmar actualización? (s/n): ", showMessage, getInput);
            })
            .filter(confirm -> "s".equalsIgnoreCase(confirm))
            .map(confirm -> {
                Map<String, String> updateData = Map.of("id", id.toString(), "newValue", newValue);
                showMessage.accept("Datos de actualización capturados exitosamente.");
                return updateData;
            })
            .map(updateData -> Optional.of(updateData))
            .orElseGet(() -> {
                showMessage.accept("Error: El " + fieldDescription + " no puede estar vacío o la actualización fue cancelada.");
                return Optional.empty();
            });
    }
}
