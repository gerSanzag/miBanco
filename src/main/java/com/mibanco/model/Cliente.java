package com.mibanco.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase que representa a un cliente del banco
 * Utilizamos enfoque completamente inmutable con @Value para mayor seguridad
 */
@Value
@Builder(toBuilder = true)
public class Cliente {
    Long id;
    String nombre;
    String apellido;
    String dni;
    LocalDate fechaNacimiento;
    String email;
    String telefono;
    String direccion;
    
    /**
     * Método factory que facilita la creación de instancias
     * Utilizamos Optional para los campos que pueden ser opcionales
     */
    public static Cliente of(Long id, String nombre, String apellido, String dni,
                            Optional<LocalDate> fechaNacimiento, Optional<String> email, 
                            Optional<String> telefono, Optional<String> direccion) {
        return Cliente.builder()
                .id(id)
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .fechaNacimiento(fechaNacimiento.orElse(null))
                .email(email.orElse(null))
                .telefono(telefono.orElse(null))
                .direccion(direccion.orElse(null))
                .build();
    }
    
    /**
     * Crea una nueva instancia con email actualizado
     * @param nuevoEmail El nuevo email (opcional)
     * @return Una nueva instancia con el email actualizado
     */
    public Cliente withEmail(Optional<String> nuevoEmail) {
        return this.toBuilder()
                .email(nuevoEmail.orElse(this.email))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Cliente withEmail(String nuevoEmail) {
        return withEmail(Optional.ofNullable(nuevoEmail));
    }
    
    /**
     * Crea una nueva instancia con teléfono actualizado
     * @param nuevoTelefono El nuevo teléfono (opcional)
     * @return Una nueva instancia con el teléfono actualizado
     */
    public Cliente withTelefono(Optional<String> nuevoTelefono) {
        return this.toBuilder()
                .telefono(nuevoTelefono.orElse(this.telefono))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Cliente withTelefono(String nuevoTelefono) {
        return withTelefono(Optional.ofNullable(nuevoTelefono));
    }
    
    /**
     * Crea una nueva instancia con dirección actualizada
     * @param nuevaDireccion La nueva dirección (opcional)
     * @return Una nueva instancia con la dirección actualizada
     */
    public Cliente withDireccion(Optional<String> nuevaDireccion) {
        return this.toBuilder()
                .direccion(nuevaDireccion.orElse(this.direccion))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Cliente withDireccion(String nuevaDireccion) {
        return withDireccion(Optional.ofNullable(nuevaDireccion));
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Cliente withDatosContacto(Optional<String> nuevoEmail, 
                                    Optional<String> nuevoTelefono, 
                                    Optional<String> nuevaDireccion) {
        return this.toBuilder()
                .email(nuevoEmail.orElse(this.email))
                .telefono(nuevoTelefono.orElse(this.telefono))
                .direccion(nuevaDireccion.orElse(this.direccion))
                .build();
    }
} 