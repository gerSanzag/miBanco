package com.mibanco.view.util;

import java.util.Map;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for validation operations in the view layer.
 * Provides reusable validation and input capture methods for all entity views.
 */
public class ValidationUtil {
    
    /**
     * Validates that required fields are not empty.
     * 
     * @param fields Map of field names to field values
     * @param requiredFieldNames List of field names that are required
     * @return true if all required fields are valid, false otherwise
     */
    public static boolean validateRequiredFields(Map<String, String> fields, List<String> requiredFieldNames) {
        if (fields == null || requiredFieldNames == null) {
            return false;
        }
        
        for (String fieldName : requiredFieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue == null || fieldValue.trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates that specific fields are not empty.
     * 
     * @param fieldValues variable number of field values to validate
     * @return true if all fields are valid, false otherwise
     */
    public static boolean validateRequiredFields(String... fieldValues) {
        if (fieldValues == null) {
            return false;
        }
        
        for (String fieldValue : fieldValues) {
            if (fieldValue == null || fieldValue.trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates email format using a simple regex.
     * 
     * @param email the email to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Empty email is allowed
        }
        
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validates that a string is not null or empty.
     * 
     * @param value the string to validate
     * @return true if the string is not null and not empty, false otherwise
     */
    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates that a string has a minimum length.
     * 
     * @param value the string to validate
     * @param minLength the minimum required length
     * @return true if the string meets the minimum length requirement, false otherwise
     */
    public static boolean hasMinimumLength(String value, int minLength) {
        return isNotNullOrEmpty(value) && value.trim().length() >= minLength;
    }
    
    // ===== INPUT CAPTURE METHODS =====
    
    /**
     * Captures string input from the user with a prompt.
     * 
     * @param prompt the prompt to show to the user
     * @param showMessage consumer to display messages
     * @param getInput supplier to get user input
     * @return the user input trimmed
     */
    public static String captureStringInput(String prompt, Consumer<String> showMessage, Supplier<String> getInput) {
        showMessage.accept(prompt);
        String input = getInput.get();
        return input != null ? input.trim() : "";
    }
    
    /**
     * Captures string input with validation for required fields.
     * 
     * @param prompt the prompt to show to the user
     * @param showMessage consumer to display messages
     * @param getInput supplier to get user input
     * @param fieldName name of the field for error messages
     * @return the user input if valid, null if invalid
     */
    public static String captureRequiredStringInput(String prompt, Consumer<String> showMessage, 
                                                   Supplier<String> getInput, String fieldName) {
        String input = captureStringInput(prompt, showMessage, getInput);
        
        if (!isNotNullOrEmpty(input)) {
            showMessage.accept("Error: " + fieldName + " es obligatorio.");
            return null;
        }
        
        return input;
    }
    
    /**
     * Captures email input with validation.
     * 
     * @param prompt the prompt to show to the user
     * @param showMessage consumer to display messages
     * @param getInput supplier to get user input
     * @return the email if valid, null if invalid
     */
    public static String captureEmailInput(String prompt, Consumer<String> showMessage, Supplier<String> getInput) {
        String email = captureStringInput(prompt, showMessage, getInput);
        
        if (!email.isEmpty() && !isValidEmail(email)) {
            showMessage.accept("Error: Formato de email inválido.");
            return null;
        }
        
        return email;
    }
    

}
