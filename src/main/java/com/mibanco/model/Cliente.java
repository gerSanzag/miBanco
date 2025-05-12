package com.mibanco.model;

import lombok.Value;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase que representa a un cliente del banco
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder(toBuilder = true)
public class Cliente implements Identificable {
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
     */
    public static Cliente of(Long id, String nombre, String apellido, String dni,
                            LocalDate fechaNacimiento, String email, String telefono, String direccion) {
        return Cliente.builder()
                .id(id)
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .fechaNacimiento(fechaNacimiento)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el email
     * @return Una nueva instancia con el email actualizado
     */
    public Cliente withEmail(String nuevoEmail) {
        return this.toBuilder()
                .email(nuevoEmail)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el teléfono
     * @return Una nueva instancia con el teléfono actualizado
     */
    public Cliente withTelefono(String nuevoTelefono) {
        return this.toBuilder()
                .telefono(nuevoTelefono)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar la dirección
     * @return Una nueva instancia con la dirección actualizada
     */
    public Cliente withDireccion(String nuevaDireccion) {
        return this.toBuilder()
                .direccion(nuevaDireccion)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
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