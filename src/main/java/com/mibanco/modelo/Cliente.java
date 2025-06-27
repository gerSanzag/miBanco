package com.mibanco.modelo;

import com.mibanco.util.ReflexionUtil.NoSolicitar;
import lombok.Value;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Clase que representa a un cliente del banco
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder(toBuilder = true)
public class Cliente implements Identificable {
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
     * Define los campos requeridos para crear un nuevo cliente
     * @return Lista de nombres de campos que son obligatorios
     */
    public static List<String> obtenerCamposRequeridos() {
        return Arrays.asList(
            "nombre",
            "apellido",
            "dni",
            "email",
            "telefono",
            "direccion",
            "fechaNacimiento"
        );
    }
    
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
    public Cliente conEmail(String nuevoEmail) {
        return this.toBuilder()
                .email(nuevoEmail)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el teléfono
     * @return Una nueva instancia con el teléfono actualizado
     */
    public Cliente conTelefono(String nuevoTelefono) {
        return this.toBuilder()
                .telefono(nuevoTelefono)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar la dirección
     * @return Una nueva instancia con la dirección actualizada
     */
    public Cliente conDireccion(String nuevaDireccion) {
        return this.toBuilder()
                .direccion(nuevaDireccion)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Cliente conDatosContacto(Optional<String> nuevoEmail, 
                                    Optional<String> nuevoTelefono, 
                                    Optional<String> nuevaDireccion) {
        return this.toBuilder()
                .email(nuevoEmail.orElse(this.email))
                .telefono(nuevoTelefono.orElse(this.telefono))
                .direccion(nuevaDireccion.orElse(this.direccion))
                .build();
    }
} 