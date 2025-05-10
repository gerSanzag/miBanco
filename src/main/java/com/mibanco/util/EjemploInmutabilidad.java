package com.mibanco.util;

import com.mibanco.dto.ClienteDTO;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase de ejemplo que demuestra el uso de objetos inmutables
 * y transformaciones funcionales con métodos "with"
 */
public class EjemploInmutabilidad {

    public static void main(String[] args) {
        // Crear un cliente usando el constructor de Lombok (generado por @Value)
        ClienteDTO cliente1 = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678X")
                .build();
        
        System.out.println("Cliente original: " + cliente1);
        
        // Usando el método withNombre para crear una nueva instancia con nombre actualizado
        ClienteDTO cliente2 = cliente1.withNombre("Pedro");
        
        // El cliente original no cambia (inmutabilidad)
        System.out.println("\nDespués de withNombre:");
        System.out.println("Cliente original: " + cliente1);
        System.out.println("Nuevo cliente: " + cliente2);
        
        // Encadenando múltiples transformaciones
        ClienteDTO cliente3 = cliente1
                .withNombre("Ana")
                .withApellido("Gómez")
                .withEmail("ana.gomez@example.com");
        
        System.out.println("\nTransformaciones encadenadas:");
        System.out.println("Cliente original: " + cliente1);
        System.out.println("Cliente transformado: " + cliente3);
        
        // Utilizando Optional en las transformaciones
        ClienteDTO cliente4 = cliente1.withActualizaciones(
                Optional.of("María"),
                Optional.empty(), // No cambia el apellido
                Optional.of("maria@example.com")
        );
        
        System.out.println("\nUsando Optional para actualizaciones selectivas:");
        System.out.println("Cliente original: " + cliente1);
        System.out.println("Cliente actualizado: " + cliente4);
        
        // Usando el método factory "of" con Optional
        ClienteDTO cliente5 = ClienteDTO.of(
                5L,
                "Carlos",
                "Rodríguez",
                "87654321Y",
                Optional.of("carlos@example.com"),
                Optional.empty(),
                Optional.of(LocalDate.of(1985, 5, 15)),
                Optional.of("Calle Principal 123")
        );
        
        System.out.println("\nCliente creado con método factory 'of':");
        System.out.println(cliente5);
    }
} 