package com.mibanco.util;

import com.mibanco.model.Cliente;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase de ejemplo que demuestra el enfoque inmutable aplicado
 * a los modelos
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
        
        // 1. ENFOQUE INMUTABLE (con métodos with)
        System.out.println("\n1. ENFOQUE INMUTABLE (con métodos with):");
        
        // Usando withEmail (crea una nueva instancia)
        Cliente clienteConNuevoEmail = cliente.withEmail("antonio.g@nuevocorreo.com");
        System.out.println("Después de withEmail: " + clienteConNuevoEmail);
        
        // Usando métodos with encadenados (cada uno crea una nueva instancia)
        Cliente clienteConMultiplesCambios = cliente
                .withTelefono("666777888")
                .withDireccion("Avenida Principal 123");
        
        System.out.println("Después de withX encadenados: " + clienteConMultiplesCambios);
        
        // Mostrar que el cliente original permanece sin cambios
        System.out.println("\nCliente original (sin cambios): " + cliente);
        
        // 2. Otro ejemplo con withDatosContacto
        System.out.println("\n2. USANDO withDatosContacto CON OPTIONAL:");
        
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
        
        // Usando withDatosContacto (actualización múltiple e inmutable)
        Cliente cliente3 = cliente2.withDatosContacto(
                Optional.of("e.martinez@gmail.com"),
                Optional.empty(), // No cambia el teléfono
                Optional.of("Urbanización Las Flores 78")
        );
        
        System.out.println("\nDespués de withDatosContacto:");
        System.out.println("Cliente original: " + cliente2); // No cambia
        System.out.println("Cliente con nuevos datos: " + cliente3);
    }
} 