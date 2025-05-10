package com.mibanco.util;

import com.mibanco.model.Cliente;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase de ejemplo que demuestra el enfoque mixto de inmutabilidad y mutabilidad
 * aplicado al modelo Cliente
 */
public class EjemploModeloMixto {

    public static void main(String[] args) {
        // Crear un cliente con datos básicos
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Antonio")
                .apellido("García")
                .dni("11223344B")
                .fechaNacimiento(LocalDate.of(1975, 6, 20))
                .email("antonio.garcia@example.com")
                .build();
        
        System.out.println("Cliente original: " + cliente);
        
        // 1. Enfoque mutable (usando setters)
        System.out.println("\n1. ENFOQUE MUTABLE (con setters):");
        
        cliente.setEmail("antonio.g@nuevocorreo.com");
        System.out.println("Después de setEmail: " + cliente);
        
        cliente.setTelefono("666777888")
               .setDireccion("Avenida Principal 123"); // Encadenamiento de setters
        
        System.out.println("Después de setters encadenados: " + cliente);
        
        // 2. Enfoque inmutable (usando métodos with)
        System.out.println("\n2. ENFOQUE INMUTABLE (con métodos with):");
        
        // Creamos un nuevo cliente base para las demostraciones
        Cliente cliente2 = Cliente.of(
                2L, 
                "Elena", 
                "Martínez", 
                "22334455C", 
                LocalDate.of(1982, 3, 15),
                "elena@example.com",
                "611222333",
                "Calle del Sol 45"
        );
        
        System.out.println("Cliente original: " + cliente2);
        
        // Usando withEmail (devuelve una nueva instancia)
        Cliente cliente3 = cliente2.withEmail("elena.martinez@empresa.com");
        
        System.out.println("\nDespués de withEmail:");
        System.out.println("Cliente original: " + cliente2); // No cambia
        System.out.println("Cliente nuevo: " + cliente3);    // Nueva instancia
        
        // Usando withDatosContacto (actualización múltiple e inmutable)
        Cliente cliente4 = cliente2.withDatosContacto(
                Optional.of("e.martinez@gmail.com"),
                Optional.empty(), // No cambia el teléfono
                Optional.of("Urbanización Las Flores 78")
        );
        
        System.out.println("\nDespués de withDatosContacto:");
        System.out.println("Cliente original: " + cliente2); // No cambia
        System.out.println("Cliente con nuevos datos: " + cliente4);
        
        // 3. Intentando modificar atributos inmutables (causará error)
        System.out.println("\n3. INTENTO DE MODIFICAR ATRIBUTOS INMUTABLES:");
        
        try {
            // Esto causará un error de compilación y está comentado para que compile
            // cliente2.setNombre("Nuevo nombre"); // Error: no existe setNombre()
            
            System.out.println("No se puede modificar directamente el nombre, apellido, DNI, etc.");
            System.out.println("Estos campos son finales (inmutables por diseño).");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 