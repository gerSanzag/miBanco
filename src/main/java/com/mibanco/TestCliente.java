package com.mibanco;

import com.mibanco.controlador.ClienteControlador;
import com.mibanco.controlador.interna.ControladorFactoria;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.interna.ServicioFactoria;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test específico para probar todas las funcionalidades del cliente
 */
public class TestCliente {

    public static void main(String[] args) {
        System.out.println("=== TEST DE FUNCIONALIDADES DEL CLIENTE ===\n");

        // Obtener instancias
        ClienteServicio servicio = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        ClienteControlador controlador = ControladorFactoria.obtenerInstancia().obtenerControladorCliente();

        // Test 1: Crear cliente
        testCrearCliente(servicio);

        // Test 2: Buscar cliente por ID
        testBuscarClientePorId(servicio);

        // Test 3: Buscar cliente por DNI
        testBuscarClientePorDni(servicio);

        // Test 4: Listar todos los clientes
        testListarTodosLosClientes(servicio);

        // Test 5: Actualizar cliente
        testActualizarCliente(servicio);

        // Test 6: Eliminar cliente
        testEliminarCliente(servicio);

        // Test 7: Mostrar clientes eliminados
        testMostrarClientesEliminados(servicio);

        // Test 8: Restaurar cliente
        testRestaurarCliente(servicio);

        System.out.println("\n=== FIN DE TESTS ===");
    }

    private static void testCrearCliente(ClienteServicio servicio) {
        System.out.println("--- Test 1: Crear Cliente ---");
        
        Map<String, String> datosCliente = Map.of(
            "nombre", "Juan",
            "apellido", "Pérez",
            "dni", "12345678A",
            "email", "juan.perez@email.com",
            "telefono", "123456789",
            "direccion", "Calle Principal 123",
            "fechaNacimiento", "1990-01-15"
        );

        Optional<ClienteDTO> clienteCreado = servicio.crearClienteDto(datosCliente);
        
        clienteCreado.ifPresentOrElse(
            cliente -> System.out.println("✅ Cliente creado exitosamente: " + cliente.getNombre() + " " + cliente.getApellido() + " (ID: " + cliente.getId() + ")"),
            () -> System.out.println("❌ Error al crear cliente")
        );
        System.out.println();
    }

    private static void testBuscarClientePorId(ClienteServicio servicio) {
        System.out.println("--- Test 2: Buscar Cliente por ID ---");
        
        // Buscar el primer cliente (ID 1)
        Optional<ClienteDTO> cliente = servicio.obtenerClientePorId(Optional.of(1L));
        
        cliente.ifPresentOrElse(
            c -> System.out.println("✅ Cliente encontrado: " + c.getNombre() + " " + c.getApellido() + " (DNI: " + c.getDni() + ")"),
            () -> System.out.println("❌ Cliente no encontrado")
        );
        System.out.println();
    }

    private static void testBuscarClientePorDni(ClienteServicio servicio) {
        System.out.println("--- Test 3: Buscar Cliente por DNI ---");
        
        Optional<ClienteDTO> cliente = servicio.obtenerClientePorDni(Optional.of("12345678A"));
        
        cliente.ifPresentOrElse(
            c -> System.out.println("✅ Cliente encontrado por DNI: " + c.getNombre() + " " + c.getApellido() + " (ID: " + c.getId() + ")"),
            () -> System.out.println("❌ Cliente no encontrado por DNI")
        );
        System.out.println();
    }

    private static void testListarTodosLosClientes(ClienteServicio servicio) {
        System.out.println("--- Test 4: Listar Todos los Clientes ---");
        
        Optional<List<ClienteDTO>> clientes = servicio.obtenerTodosLosClientes();
        
        clientes.ifPresentOrElse(
            lista -> {
                System.out.println("✅ Total de clientes: " + lista.size());
                lista.forEach(cliente -> 
                    System.out.println("  - " + cliente.getNombre() + " " + cliente.getApellido() + " (ID: " + cliente.getId() + ")")
                );
            },
            () -> System.out.println("❌ Error al obtener lista de clientes")
        );
        System.out.println();
    }

    private static void testActualizarCliente(ClienteServicio servicio) {
        System.out.println("--- Test 5: Actualizar Cliente ---");
        
        // Actualizar email del cliente con ID 1
        Optional<ClienteDTO> clienteActualizado = servicio.actualizarEmailCliente(1L, Optional.of("nuevo.email@test.com"));
        
        clienteActualizado.ifPresentOrElse(
            cliente -> System.out.println("✅ Cliente actualizado: " + cliente.getEmail()),
            () -> System.out.println("❌ Error al actualizar cliente")
        );
        System.out.println();
    }

    private static void testEliminarCliente(ClienteServicio servicio) {
        System.out.println("--- Test 6: Eliminar Cliente ---");
        
        // Eliminar cliente con ID 1
        boolean eliminado = servicio.eliminarCliente(Optional.of(1L));
        
        if (eliminado) {
            System.out.println("✅ Cliente eliminado exitosamente");
        } else {
            System.out.println("❌ Error al eliminar cliente");
        }
        System.out.println();
    }

    private static void testMostrarClientesEliminados(ClienteServicio servicio) {
        System.out.println("--- Test 7: Mostrar Clientes Eliminados ---");
        
        List<ClienteDTO> clientesEliminados = servicio.obtenerClientesEliminados();
        
        if (!clientesEliminados.isEmpty()) {
            System.out.println("✅ Clientes eliminados encontrados: " + clientesEliminados.size());
            clientesEliminados.forEach(cliente -> 
                System.out.println("  - " + cliente.getNombre() + " " + cliente.getApellido() + " (ID: " + cliente.getId() + ")")
            );
        } else {
            System.out.println("ℹ️ No hay clientes eliminados");
        }
        System.out.println();
    }

    private static void testRestaurarCliente(ClienteServicio servicio) {
        System.out.println("--- Test 8: Restaurar Cliente ---");
        
        // Restaurar cliente con ID 1
        Optional<ClienteDTO> clienteRestaurado = servicio.restaurarCliente(Optional.of(1L));
        
        clienteRestaurado.ifPresentOrElse(
            cliente -> System.out.println("✅ Cliente restaurado exitosamente: " + cliente.getNombre() + " " + cliente.getApellido()),
            () -> System.out.println("❌ Error al restaurar cliente")
        );
        System.out.println();
    }
} 