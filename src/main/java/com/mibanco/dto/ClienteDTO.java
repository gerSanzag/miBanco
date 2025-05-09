package com.mibanco.dto;

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
     * Crea una nueva instancia con email actualizado
     * @param nuevoEmail El nuevo email (opcional)
     * @return Una nueva instancia con el email actualizado
     */
    public ClienteDTO withEmail(Optional<String> nuevoEmail) {
        return this.toBuilder()
                .email(nuevoEmail.orElse(this.email))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public ClienteDTO withEmail(String nuevoEmail) {
        return withEmail(Optional.ofNullable(nuevoEmail));
    }
    
    /**
     * Crea una nueva instancia con teléfono actualizado
     * @param nuevoTelefono El nuevo teléfono (opcional)
     * @return Una nueva instancia con el teléfono actualizado
     */
    public ClienteDTO withTelefono(Optional<String> nuevoTelefono) {
        return this.toBuilder()
                .telefono(nuevoTelefono.orElse(this.telefono))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public ClienteDTO withTelefono(String nuevoTelefono) {
        return withTelefono(Optional.ofNullable(nuevoTelefono));
    }
    
    /**
     * Crea una nueva instancia con dirección actualizada
     * @param nuevaDireccion La nueva dirección (opcional)
     * @return Una nueva instancia con la dirección actualizada
     */
    public ClienteDTO withDireccion(Optional<String> nuevaDireccion) {
        return this.toBuilder()
                .direccion(nuevaDireccion.orElse(this.direccion))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public ClienteDTO withDireccion(String nuevaDireccion) {
        return withDireccion(Optional.ofNullable(nuevaDireccion));
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * Útil cuando se necesitan actualizar varios campos en una sola operación
     * @param nuevoEmail Nuevo email (opcional)
     * @param nuevoTelefono Nuevo teléfono (opcional)
     * @param nuevaDireccion Nueva dirección (opcional)
     * @return Un nuevo ClienteDTO con los campos actualizados
     */
    public ClienteDTO withActualizaciones(
            Optional<String> nuevoEmail,
            Optional<String> nuevoTelefono,
            Optional<String> nuevaDireccion) {
        
        return this.toBuilder()
                .email(nuevoEmail.orElse(this.email))
                .telefono(nuevoTelefono.orElse(this.telefono))
                .direccion(nuevaDireccion.orElse(this.direccion))
                .build();
    }
} 