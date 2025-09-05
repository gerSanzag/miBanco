package com.mibanco.view.util;

import com.mibanco.view.ConsoleIO;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
}
