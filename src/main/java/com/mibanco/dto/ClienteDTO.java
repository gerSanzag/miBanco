package com.mibanco.dto;

import java.time.LocalDate;
import java.util.Optional;

import lombok.Builder;
import lombok.Value;

/**
 * DTO para transferir información de Cliente entre capas
 * Utilizamos @Value para crear una clase inmutable (enfoque funcional)
 * y Optional para manejar posibles valores nulos
 */
@Value
@Builder(toBuilder = true) // Habilitamos toBuilder para facilitar la creación de nuevas instancias
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
    
    /**
     * Crea una nueva instancia de ClienteDTO con un nombre diferente
     * Ejemplo de método "with" para transformación inmutable
     * @param nuevoNombre El nuevo nombre para el cliente
     * @return Un nuevo ClienteDTO con el nombre actualizado
     */
    public ClienteDTO withNombre(String nuevoNombre) {
        return this.toBuilder()
                .nombre(nuevoNombre)
                .build();
    }
    
    /**
     * Crea una nueva instancia de ClienteDTO con un apellido diferente
     * @param nuevoApellido El nuevo apellido para el cliente
     * @return Un nuevo ClienteDTO con el apellido actualizado
     */
    public ClienteDTO withApellido(String nuevoApellido) {
        return this.toBuilder()
                .apellido(nuevoApellido)
                .build();
    }
    
    /**
     * Crea una nueva instancia de ClienteDTO con un email diferente
     * Demuestra manejo de Optional en métodos "with"
     * @param nuevoEmail El nuevo email (opcional)
     * @return Un nuevo ClienteDTO con el email actualizado
     */
    public ClienteDTO withEmail(Optional<String> nuevoEmail) {
        return this.toBuilder()
                .email(nuevoEmail.orElse(null))
                .build();
    }
    
    /**
     * Versión simplificada del método withEmail
     * @param nuevoEmail El nuevo email (puede ser null)
     * @return Un nuevo ClienteDTO con el email actualizado
     */
    public ClienteDTO withEmail(String nuevoEmail) {
        return withEmail(Optional.ofNullable(nuevoEmail));
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * Útil cuando se necesitan actualizar varios campos en una sola operación
     * @param nuevoNombre Nuevo nombre (opcional)
     * @param nuevoApellido Nuevo apellido (opcional)
     * @param nuevoEmail Nuevo email (opcional)
     * @return Un nuevo ClienteDTO con los campos actualizados
     */
    public ClienteDTO withActualizaciones(
            Optional<String> nuevoNombre,
            Optional<String> nuevoApellido,
            Optional<String> nuevoEmail) {
        
        return this.toBuilder()
                .nombre(nuevoNombre.orElse(this.nombre))
                .apellido(nuevoApellido.orElse(this.apellido))
                .email(nuevoEmail.orElse(this.email))
                .build();
    }
} 