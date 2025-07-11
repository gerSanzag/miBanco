package com.mibanco.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Clase que representa a un cliente del banco
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder
public class Cliente implements Identificable {
    
    Long id;
    String nombre;
    String apellido;
    String dni;
    LocalDate fechaNacimiento;
    String email;
    String telefono;
    String direccion;
    
    @JsonCreator
    public Cliente(
        @JsonProperty("id") Long id,
        @JsonProperty("nombre") String nombre,
        @JsonProperty("apellido") String apellido,
        @JsonProperty("dni") String dni,
        @JsonProperty("fechaNacimiento") LocalDate fechaNacimiento,
        @JsonProperty("email") String email,
        @JsonProperty("telefono") String telefono,
        @JsonProperty("direccion") String direccion
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    
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

} 