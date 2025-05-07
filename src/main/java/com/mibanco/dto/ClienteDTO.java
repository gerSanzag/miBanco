package com.mibanco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Optional;

/**
 * DTO para transferir información de Cliente entre capas
 * Utilizamos @Value para crear una clase inmutable (enfoque funcional)
 * y Optional para manejar posibles valores nulos
 */
@Value
@Builder
@AllArgsConstructor
public class ClienteDTO {
    Long id;
    String nombre;
    String apellido;
    String dni;
    String email;
    String telefono;
    LocalDate fechaNacimiento;
    String direccion;

    /**
     * Método estático que construye un ClienteDTO con valores opcionales
     * Ejemplo de uso del enfoque funcional y Optionals
     */
    public static ClienteDTO of(Long id, String nombre, String apellido, String dni, 
                                Optional<String> email, Optional<String> telefono,
                                Optional<LocalDate> fechaNacimiento, Optional<String> direccion) {
        return ClienteDTO.builder()
                .id(id)
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .email(email.orElse(null))
                .telefono(telefono.orElse(null))
                .fechaNacimiento(fechaNacimiento.orElse(null))
                .direccion(direccion.orElse(null))
                .build();
    }
} 