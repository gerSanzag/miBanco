package com.mibanco.controller.util;

import com.mibanco.util.ReflectionUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Utility class for controller-level validations
 * Centralizes validation logic for required fields across all controllers
 */
public class ControllerValidationUtil {
    
    /**
     * Validates that the data contains all required fields for the given entity class
     * Uses reflection to dynamically determine required fields
     * 
     * @param data Map containing the data to validate
     * @param entityClass The entity class to check required fields for
     * @return true if all required fields are present and valid, false otherwise
     */
    public static boolean hasRequiredFields(Map<String, String> data, Class<?> entityClass) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        
        // Get required fields dynamically using reflection
        List<String> requiredFields = ReflectionUtil.getRequiredFields(entityClass);
        
        // Validate each required field
        for (String fieldName : requiredFields) {
            String fieldValue = data.get(fieldName);
            
            // Check if field is present and not empty
            if (fieldValue == null || fieldValue.trim().isEmpty()) {
                return false;
            }
            
            // Special validation for date fields
            if (isDateField(fieldName)) {
                if (!isValidDate(fieldValue)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a field name represents a date field
     * 
     * @param fieldName The field name to check
     * @return true if it's a date field, false otherwise
     */
    private static boolean isDateField(String fieldName) {
        return "birthDate".equals(fieldName) || 
               "expirationDate".equals(fieldName) || 
               "creationDate".equals(fieldName);
    }
    
    /**
     * Validates if a string represents a valid date in ISO format (YYYY-MM-DD)
     * 
     * @param dateString The date string to validate
     * @return true if it's a valid date, false otherwise
     */
    private static boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
