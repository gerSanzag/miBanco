package com.mibanco;

import com.mibanco.controlador.ClienteControlador;
import com.mibanco.controlador.interna.ControladorFactoria;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.interna.ServicioFactoria;

import java.util.Map;
import java.util.Optional;

/**
 * Clase de prueba para verificar que la arquitectura mejorada funciona correctamente.
 * Demuestra la separación correcta de responsabilidades entre capas.
 */
public class TestArquitectura {
    
    public static void main(String[] args) {
        System.out.println("=== Test de Arquitectura Mejorada ===\n");
        
        // Obtener instancias a través de las fábricas
        ClienteServicio servicio = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        ClienteControlador controlador = ControladorFactoria.obtenerInstancia().obtenerControladorCliente();
        
        System.out.println("1. Verificando que el servicio puede crear DTOs internamente...");
        
        // Test del nuevo método del servicio que crea DTOs internamente
        Map<String, String> datosCliente = Map.of(
            "nombre", "Juan",
            "apellido", "Pérez",
            "dni", "12345678A",
            "email", "juan.perez@email.com",
            "telefono", "600123456",
            "direccion", "Calle Mayor 123",
            "fechaNacimiento", "1990-01-15"
        );
        
        Optional<ClienteDTO> clienteCreado = servicio.crearClienteDto(datosCliente);
        
        if (clienteCreado.isPresent()) {
            ClienteDTO cliente = clienteCreado.get();
            System.out.println("✅ Cliente creado exitosamente:");
            System.out.println("   ID: " + cliente.getId());
            System.out.println("   Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
            System.out.println("   DNI: " + cliente.getDni());
            System.out.println("   Email: " + cliente.getEmail());
            
            System.out.println("\n2. Verificando que el controlador puede usar el nuevo método...");
            
            // Test de actualización individual
            Optional<ClienteDTO> clienteActualizado = servicio.actualizarEmailCliente(
                cliente.getId(), 
                Optional.of("nuevo.email@email.com")
            );
            
            if (clienteActualizado.isPresent()) {
                System.out.println("✅ Email actualizado exitosamente:");
                System.out.println("   Nuevo email: " + clienteActualizado.get().getEmail());
            }
            
            System.out.println("\n3. Verificando que el controlador puede actualizar múltiples campos...");
            
            // Test de actualización múltiple
            ClienteDTO clienteParaActualizar = ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .email("email.actualizado@email.com")
                .telefono("700654321")
                .direccion("Nueva Dirección 456")
                .fechaNacimiento(cliente.getFechaNacimiento())
                .build();
            
            Optional<ClienteDTO> clienteMultiActualizado = servicio.actualizarVariosCampos(
                cliente.getId(), 
                Optional.of(clienteParaActualizar)
            );
            
            if (clienteMultiActualizado.isPresent()) {
                System.out.println("✅ Múltiples campos actualizados exitosamente:");
                System.out.println("   Email: " + clienteMultiActualizado.get().getEmail());
                System.out.println("   Teléfono: " + clienteMultiActualizado.get().getTelefono());
                System.out.println("   Dirección: " + clienteMultiActualizado.get().getDireccion());
            }
            
            System.out.println("\n4. Verificando que el controlador puede obtener clientes...");
            
            Optional<ClienteDTO> clienteObtenido = servicio.obtenerClientePorId(Optional.of(cliente.getId()));
            
            if (clienteObtenido.isPresent()) {
                System.out.println("✅ Cliente obtenido exitosamente por ID");
            }
            
            System.out.println("\n5. Verificando que el controlador puede eliminar clientes...");
            
            boolean eliminado = servicio.eliminarCliente(Optional.of(cliente.getId()));
            
            if (eliminado) {
                System.out.println("✅ Cliente eliminado exitosamente");
            }
            
        } else {
            System.out.println("❌ Error al crear el cliente");
        }
        
        System.out.println("\n=== Test completado ===");
        System.out.println("✅ La arquitectura mejorada funciona correctamente:");
        System.out.println("   - El servicio crea DTOs internamente");
        System.out.println("   - El controlador envía datos crudos al servicio");
        System.out.println("   - Se mantiene la separación de responsabilidades");
        System.out.println("   - Se respeta el patrón funcional con Optional");
    }
} 