package com.mibanco.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Generic DTO to standardize responses in the application
 * Follows functional principles with immutable objects and factory methods
 * @param <T> Type of data contained in the response
 */
@Value
@Builder
public class ResponseDTO<T> {
    boolean success;         // exito
    String message;          // mensaje
    T data;                  // datos
    List<String> errors;     // errores
    LocalDateTime timestamp; // marcaTiempo

    /**
     * Factory method to create a successful response
     * @param data Data to include in the response
     * @param message Optional descriptive message
     * @return ResponseDTO with success
     */
    public static <T> ResponseDTO<T> success(T data, Optional<String> message) {
        return ResponseDTO.<T>builder()
                .success(true)
                .message(message.orElse("Operation completed successfully"))
                .data(data)
                .errors(Collections.emptyList())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Simplified factory method to create a successful response
     */
    public static <T> ResponseDTO<T> success(T data) {
        return success(data, Optional.empty());
    }

    /**
     * Factory method to create an error response
     * @param message Error message
     * @param errors List of detailed errors (optional)
     * @return ResponseDTO with error
     */
    public static <T> ResponseDTO<T> error(String message, Optional<List<String>> errors) {
        return ResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(errors.orElse(Collections.emptyList()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Simplified factory method to create an error response
     */
    public static <T> ResponseDTO<T> error(String message) {
        return error(message, Optional.empty());
    }
} 