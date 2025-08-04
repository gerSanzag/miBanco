package com.mibanco.util;

/**
 * Specific exception for business validation errors
 * Thrown when domain rules are violated
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 