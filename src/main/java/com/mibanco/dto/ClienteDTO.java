package com.mibanco.dto;

import java.time.LocalDate;
import java.util.Optional;

import com.mibanco.util.ReflexionUtil.NoSolicitar;

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
    @NoSolicitar(razon = "Se genera automáticamente")
    Long id;
    String nombre;
    String apellido;
    String dni;
    LocalDate fechaNacimiento;
    String email;
    String telefono;
    String direccion;

    /**
     * Método estático que construye un ClienteDTO con valores opcionales
     * Ejemplo de uso del enfoque funcional y Optionals
     */
    public static ClienteDTO of(Long id, Optional<String> nombre, Optional<String> apellido, Optional<String> dni, 
                                Optional<String> email, Optional<String> telefono,
                                Optional<LocalDate> fechaNacimiento, Optional<String> direccion) {
        return ClienteDTO.builder()
                .id(id)
                .nombre(nombre.orElse(null))
                .apellido(apellido.orElse(null))
                .dni(dni.orElse(null))
                .email(email.orElse(null))
                .telefono(telefono.orElse(null))
                .fechaNacimiento(fechaNacimiento.orElse(null))
                .direccion(direccion.orElse(null))
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el email
     * @return Una nueva instancia con el email actualizado
     */
    public ClienteDTO conEmail(String nuevoEmail) {
        return this.toBuilder()
                .email(nuevoEmail)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el teléfono
     * @return Una nueva instancia con el teléfono actualizado
     */
    public ClienteDTO conTelefono(String nuevoTelefono) {
        return this.toBuilder()
                .telefono(nuevoTelefono)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar la dirección
     * @return Una nueva instancia con la dirección actualizada
     */
    public ClienteDTO conDireccion(String nuevaDireccion) {
        return this.toBuilder()
                .direccion(nuevaDireccion)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public ClienteDTO conDatosContacto(Optional<String> nuevoEmail, 
                                      Optional<String> nuevoTelefono, 
                                      Optional<String> nuevaDireccion) {
        return this.toBuilder()
                .email(nuevoEmail.orElse(this.email))
                .telefono(nuevoTelefono.orElse(this.telefono))
                .direccion(nuevaDireccion.orElse(this.direccion))
                .build();
    }
} 