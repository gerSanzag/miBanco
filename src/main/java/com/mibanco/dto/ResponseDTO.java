package com.mibanco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * DTO genérico para estandarizar respuestas en la aplicación
 * Sigue principios funcionales con objetos inmutables y métodos factory
 * @param <T> Tipo de datos que contiene la respuesta
 */
@Value
@Builder
@AllArgsConstructor
public class ResponseDTO<T> {
    boolean success;
    String message;
    T data;
    List<String> errors;
    LocalDateTime timestamp;

    /**
     * Método factory para crear una respuesta exitosa
     * @param data Datos a incluir en la respuesta
     * @param message Mensaje descriptivo opcional
     * @return ResponseDTO con éxito
     */
    public static <T> ResponseDTO<T> success(T data, Optional<String> message) {
        return ResponseDTO.<T>builder()
                .success(true)
                .message(message.orElse("Operación realizada con éxito"))
                .data(data)
                .errors(Collections.emptyList())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Método factory simplificado para crear una respuesta exitosa
     */
    public static <T> ResponseDTO<T> success(T data) {
        return success(data, Optional.empty());
    }

    /**
     * Método factory para crear una respuesta de error
     * @param message Mensaje de error
     * @param errors Lista de errores detallados (opcional)
     * @return ResponseDTO con error
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
     * Método factory simplificado para crear una respuesta de error
     */
    public static <T> ResponseDTO<T> error(String message) {
        return error(message, Optional.empty());
    }
} 