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
 * Test completo para verificar todas las funcionalidades del cliente
 */
public class TestClienteCompleto {

    public static void main(String[] args) {
        System.out.println("=== TEST COMPLETO DEL CLIENTE ===\n");

        ClienteServicio servicio = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        ClienteControlador controlador = ControladorFactoria.obtenerInstancia().obtenerControladorCliente();

        // Test 1: Crear cliente
        System.out.println("1. TEST CREAR CLIENTE");
        testCrearCliente(servicio);
        System.out.println();

        // Test 2: Buscar por ID
        System.out.println("2. TEST BUSCAR POR ID");
        testBuscarPorId(servicio);
        System.out.println();

        // Test 3: Buscar por DNI
        System.out.println("3. TEST BUSCAR POR DNI");
        testBuscarPorDni(servicio);
        System.out.println();

        // Test 4: Listar todos
        System.out.println("4. TEST LISTAR TODOS");
        testListarTodos(servicio);
        System.out.println();

        // Test 5: Actualizar cliente
        System.out.println("5. TEST ACTUALIZAR CLIENTE");
        testActualizarCliente(servicio);
        System.out.println();

        // Test 6: Eliminar cliente
        System.out.println("6. TEST ELIMINAR CLIENTE");
        testEliminarCliente(servicio);
        System.out.println();

        // Test 7: Mostrar eliminados
        System.out.println("7. TEST MOSTRAR ELIMINADOS");
        testMostrarEliminados(servicio);
        System.out.println();

        // Test 8: Restaurar cliente
        System.out.println("8. TEST RESTAURAR CLIENTE");
        testRestaurarCliente(servicio);
        System.out.println();

        System.out.println("=== TODOS LOS TESTS COMPLETADOS ===");
    }

    private static void testCrearCliente(ClienteServicio servicio) {
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
        
        if (clienteCreado.isPresent()) {
            System.out.println("✅ Cliente creado exitosamente: " + clienteCreado.get().getId());
        } else {
            System.out.println("❌ Error al crear cliente");
        }
    }

    private static void testBuscarPorId(ClienteServicio servicio) {
        // Buscar el primer cliente disponible
        Optional<List<ClienteDTO>> todosLosClientes = servicio.obtenerTodosLosClientes();
        
        todosLosClientes.ifPresent(clientes -> {
            if (!clientes.isEmpty()) {
                Long idCliente = clientes.get(0).getId();
                Optional<ClienteDTO> clienteEncontrado = servicio.obtenerClientePorId(Optional.of(idCliente));
                
                if (clienteEncontrado.isPresent()) {
                    System.out.println("✅ Cliente encontrado por ID: " + clienteEncontrado.get().getNombre());
                } else {
                    System.out.println("❌ Cliente no encontrado por ID");
                }
            } else {
                System.out.println("⚠️ No hay clientes para buscar");
            }
        });
    }

    private static void testBuscarPorDni(ClienteServicio servicio) {
        // Buscar por DNI específico
        Optional<ClienteDTO> clientePorDni = servicio.obtenerClientePorDni(Optional.of("12345678A"));
        
        if (clientePorDni.isPresent()) {
            System.out.println("✅ Cliente encontrado por DNI: " + clientePorDni.get().getNombre());
        } else {
            System.out.println("❌ Cliente no encontrado por DNI");
        }
    }

    private static void testListarTodos(ClienteServicio servicio) {
        Optional<List<ClienteDTO>> todosLosClientes = servicio.obtenerTodosLosClientes();
        
        todosLosClientes.ifPresent(clientes -> {
            System.out.println("✅ Total de clientes: " + clientes.size());
            clientes.forEach(cliente -> 
                System.out.println("   - " + cliente.getNombre() + " " + cliente.getApellido() + " (ID: " + cliente.getId() + ")")
            );
        });
    }

    private static void testActualizarCliente(ClienteServicio servicio) {
        // Buscar un cliente para actualizar
        Optional<List<ClienteDTO>> todosLosClientes = servicio.obtenerTodosLosClientes();
        
        todosLosClientes.ifPresent(clientes -> {
            if (!clientes.isEmpty()) {
                Long idCliente = clientes.get(0).getId();
                
                // Actualizar email
                Optional<ClienteDTO> clienteActualizado = servicio.actualizarEmailCliente(idCliente, Optional.of("nuevo.email@test.com"));
                
                if (clienteActualizado.isPresent()) {
                    System.out.println("✅ Cliente actualizado: " + clienteActualizado.get().getEmail());
                } else {
                    System.out.println("❌ Error al actualizar cliente");
                }
            } else {
                System.out.println("⚠️ No hay clientes para actualizar");
            }
        });
    }

    private static void testEliminarCliente(ClienteServicio servicio) {
        // Buscar un cliente para eliminar
        Optional<List<ClienteDTO>> todosLosClientes = servicio.obtenerTodosLosClientes();
        
        todosLosClientes.ifPresent(clientes -> {
            if (!clientes.isEmpty()) {
                Long idCliente = clientes.get(0).getId();
                
                boolean eliminado = servicio.eliminarCliente(Optional.of(idCliente));
                
                if (eliminado) {
                    System.out.println("✅ Cliente eliminado exitosamente");
                } else {
                    System.out.println("❌ Error al eliminar cliente");
                }
            } else {
                System.out.println("⚠️ No hay clientes para eliminar");
            }
        });
    }

    private static void testMostrarEliminados(ClienteServicio servicio) {
        List<ClienteDTO> clientesEliminados = servicio.obtenerClientesEliminados();
        
        System.out.println("✅ Total de clientes eliminados: " + clientesEliminados.size());
        clientesEliminados.forEach(cliente -> 
            System.out.println("   - " + cliente.getNombre() + " " + cliente.getApellido() + " (ID: " + cliente.getId() + ")")
        );
    }

    private static void testRestaurarCliente(ClienteServicio servicio) {
        // Buscar un cliente eliminado para restaurar
        List<ClienteDTO> clientesEliminados = servicio.obtenerClientesEliminados();
        
        if (!clientesEliminados.isEmpty()) {
            Long idCliente = clientesEliminados.get(0).getId();
            
            Optional<ClienteDTO> clienteRestaurado = servicio.restaurarCliente(Optional.of(idCliente));
            
            if (clienteRestaurado.isPresent()) {
                System.out.println("✅ Cliente restaurado exitosamente: " + clienteRestaurado.get().getNombre());
            } else {
                System.out.println("❌ Error al restaurar cliente");
            }
        } else {
            System.out.println("⚠️ No hay clientes eliminados para restaurar");
        }
    }
} 