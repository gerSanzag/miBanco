package com.mibanco;

import com.mibanco.controlador.CuentaControlador;
import com.mibanco.controlador.interna.ControladorFactoria;
import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.interna.ServicioFactoria;
import com.mibanco.modelo.enums.TipoCuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Test específico para probar todas las funcionalidades de la cuenta
 */
public class TestCuenta {

    private static Long numeroCuentaCreada; // Variable para almacenar el número de cuenta creada

    public static void main(String[] args) {
        System.out.println("=== TEST DE FUNCIONALIDADES DE LA CUENTA ===\n");

        // Obtener instancias
        CuentaServicio servicioCuenta = ServicioFactoria.obtenerInstancia().obtenerServicioCuenta();
        ClienteServicio servicioCliente = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        CuentaControlador controlador = ControladorFactoria.obtenerInstancia().obtenerControladorCuenta();

        // Test 1: Crear cuenta
        testCrearCuenta(servicioCuenta, servicioCliente);

        // Test 2: Buscar cuenta por número
        testBuscarCuentaPorNumero(servicioCuenta);

        // Test 3: Listar todas las cuentas
        testListarTodasLasCuentas(servicioCuenta);

        // Test 4: Actualizar cuenta
        testActualizarCuenta(servicioCuenta);

        // Test 5: Eliminar cuenta
        testEliminarCuenta(servicioCuenta);

        // Test 6: Buscar cuentas por titular
        testBuscarCuentasPorTitular(servicioCuenta);

        // Test 7: Buscar cuentas por tipo
        testBuscarCuentasPorTipo(servicioCuenta);

        // Test 8: Listar cuentas activas
        testListarCuentasActivas(servicioCuenta);

        // Test 9: Mostrar cuentas eliminadas
        testMostrarCuentasEliminadas(servicioCuenta);

        // Test 10: Restaurar cuenta
        testRestaurarCuenta(servicioCuenta);

        // Test 11: Contar cuentas
        testContarCuentas(servicioCuenta);

        System.out.println("\n=== FIN DE TESTS ===");
    }

    private static void testCrearCuenta(CuentaServicio servicioCuenta, ClienteServicio servicioCliente) {
        System.out.println("--- Test 1: Crear Cuenta ---");
        
        // Primero necesitamos un cliente existente
        Optional<ClienteDTO> cliente = servicioCliente.obtenerClientePorId(Optional.of(1L));
        
        if (cliente.isEmpty()) {
            System.out.println("❌ No se encontró el cliente con ID 1. Creando uno primero...");
            // Crear un cliente de prueba
            cliente = servicioCliente.crearClienteDto(java.util.Map.of(
                "nombre", "María",
                "apellido", "García",
                "dni", "87654321B",
                "email", "maria.garcia@email.com",
                "telefono", "987654321",
                "direccion", "Avenida Central 456",
                "fechaNacimiento", "1985-05-20"
            ));
        }
        
        cliente.ifPresent(titular -> {
            // Crear cuenta
            CuentaDTO nuevaCuenta = CuentaDTO.builder()
                .titular(titular)
                .tipo(TipoCuenta.AHORRO)
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();

            Optional<CuentaDTO> cuentaCreada = servicioCuenta.crearCuenta(Optional.of(nuevaCuenta));
            
            cuentaCreada.ifPresentOrElse(
                cuenta -> {
                    numeroCuentaCreada = cuenta.getNumeroCuenta(); // Guardar el número de cuenta
                    System.out.println("✅ Cuenta creada exitosamente: " + cuenta.getNumeroCuenta() + " - " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido());
                },
                () -> System.out.println("❌ Error al crear cuenta")
            );
        });
        System.out.println();
    }

    private static void testBuscarCuentaPorNumero(CuentaServicio servicio) {
        System.out.println("--- Test 2: Buscar Cuenta por Número ---");
        
        if (numeroCuentaCreada == null) {
            System.out.println("❌ No hay cuenta creada para buscar");
            System.out.println();
            return;
        }
        
        // Buscar la cuenta creada usando el número real
        Optional<CuentaDTO> cuenta = servicio.obtenerCuentaPorNumero(Optional.of(numeroCuentaCreada));
        
        cuenta.ifPresentOrElse(
            c -> System.out.println("✅ Cuenta encontrada: " + c.getNumeroCuenta() + " - " + c.getTitular().getNombre() + " " + c.getTitular().getApellido() + " (Saldo: " + c.getSaldo() + ")"),
            () -> System.out.println("❌ Cuenta no encontrada")
        );
        System.out.println();
    }

    private static void testListarTodasLasCuentas(CuentaServicio servicio) {
        System.out.println("--- Test 3: Listar Todas las Cuentas ---");
        
        Optional<List<CuentaDTO>> cuentas = servicio.obtenerTodasLasCuentas();
        
        cuentas.ifPresentOrElse(
            lista -> {
                System.out.println("✅ Total de cuentas: " + lista.size());
                lista.forEach(cuenta -> 
                    System.out.println("  - " + cuenta.getNumeroCuenta() + " - " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido() + " (Saldo: " + cuenta.getSaldo() + ")")
                );
            },
            () -> System.out.println("❌ Error al obtener lista de cuentas")
        );
        System.out.println();
    }

    private static void testActualizarCuenta(CuentaServicio servicio) {
        System.out.println("--- Test 4: Actualizar Cuenta ---");
        
        if (numeroCuentaCreada == null) {
            System.out.println("❌ No hay cuenta creada para actualizar");
            System.out.println();
            return;
        }
        
        // Actualizar saldo de la cuenta usando el número real
        Optional<CuentaDTO> cuentaActualizada = servicio.actualizarSaldoCuenta(numeroCuentaCreada, Optional.of(new BigDecimal("1500.00")));
        
        cuentaActualizada.ifPresentOrElse(
            cuenta -> System.out.println("✅ Cuenta actualizada: Nuevo saldo " + cuenta.getSaldo()),
            () -> System.out.println("❌ Error al actualizar cuenta")
        );
        System.out.println();
    }

    private static void testEliminarCuenta(CuentaServicio servicio) {
        System.out.println("--- Test 5: Eliminar Cuenta ---");
        
        if (numeroCuentaCreada == null) {
            System.out.println("❌ No hay cuenta creada para eliminar");
            System.out.println();
            return;
        }
        
        // Eliminar cuenta usando el número real
        boolean eliminada = servicio.eliminarCuenta(Optional.of(numeroCuentaCreada));
        
        if (eliminada) {
            System.out.println("✅ Cuenta eliminada exitosamente");
        } else {
            System.out.println("❌ Error al eliminar cuenta");
        }
        System.out.println();
    }

    private static void testBuscarCuentasPorTitular(CuentaServicio servicio) {
        System.out.println("--- Test 6: Buscar Cuentas por Titular ---");
        
        Optional<List<CuentaDTO>> cuentas = servicio.buscarPorTitularId(Optional.of(1L));
        
        cuentas.ifPresentOrElse(
            lista -> {
                if (!lista.isEmpty()) {
                    System.out.println("✅ Cuentas del titular encontradas: " + lista.size());
                    lista.forEach(cuenta -> 
                        System.out.println("  - " + cuenta.getNumeroCuenta() + " - " + cuenta.getTipo() + " (Saldo: " + cuenta.getSaldo() + ")")
                    );
                } else {
                    System.out.println("ℹ️ No hay cuentas para este titular");
                }
            },
            () -> System.out.println("❌ Error al buscar cuentas por titular")
        );
        System.out.println();
    }

    private static void testBuscarCuentasPorTipo(CuentaServicio servicio) {
        System.out.println("--- Test 7: Buscar Cuentas por Tipo ---");
        
        Optional<List<CuentaDTO>> cuentas = servicio.buscarPorTipo(Optional.of(TipoCuenta.AHORRO));
        
        cuentas.ifPresentOrElse(
            lista -> {
                if (!lista.isEmpty()) {
                    System.out.println("✅ Cuentas de ahorro encontradas: " + lista.size());
                    lista.forEach(cuenta -> 
                        System.out.println("  - " + cuenta.getNumeroCuenta() + " - " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido())
                    );
                } else {
                    System.out.println("ℹ️ No hay cuentas de ahorro");
                }
            },
            () -> System.out.println("❌ Error al buscar cuentas por tipo")
        );
        System.out.println();
    }

    private static void testListarCuentasActivas(CuentaServicio servicio) {
        System.out.println("--- Test 8: Listar Cuentas Activas ---");
        
        Optional<List<CuentaDTO>> cuentas = servicio.buscarActivas();
        
        cuentas.ifPresentOrElse(
            lista -> {
                if (!lista.isEmpty()) {
                    System.out.println("✅ Cuentas activas encontradas: " + lista.size());
                    lista.forEach(cuenta -> 
                        System.out.println("  - " + cuenta.getNumeroCuenta() + " - " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido())
                    );
                } else {
                    System.out.println("ℹ️ No hay cuentas activas");
                }
            },
            () -> System.out.println("❌ Error al obtener cuentas activas")
        );
        System.out.println();
    }

    private static void testMostrarCuentasEliminadas(CuentaServicio servicio) {
        System.out.println("--- Test 9: Mostrar Cuentas Eliminadas ---");
        
        List<CuentaDTO> cuentasEliminadas = servicio.obtenerCuentasEliminadas();
        
        if (!cuentasEliminadas.isEmpty()) {
            System.out.println("✅ Cuentas eliminadas encontradas: " + cuentasEliminadas.size());
            cuentasEliminadas.forEach(cuenta -> 
                System.out.println("  - " + cuenta.getNumeroCuenta() + " - " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido())
            );
        } else {
            System.out.println("ℹ️ No hay cuentas eliminadas");
        }
        System.out.println();
    }

    private static void testRestaurarCuenta(CuentaServicio servicio) {
        System.out.println("--- Test 10: Restaurar Cuenta ---");
        
        if (numeroCuentaCreada == null) {
            System.out.println("❌ No hay cuenta eliminada para restaurar");
            System.out.println();
            return;
        }
        
        // Restaurar cuenta eliminada usando el número real
        Optional<CuentaDTO> cuentaRestaurada = servicio.restaurarCuenta(Optional.of(numeroCuentaCreada));
        
        cuentaRestaurada.ifPresentOrElse(
            cuenta -> System.out.println("✅ Cuenta restaurada exitosamente: " + cuenta.getNumeroCuenta() + " - " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido()),
            () -> System.out.println("❌ Error al restaurar cuenta")
        );
        System.out.println();
    }

    private static void testContarCuentas(CuentaServicio servicio) {
        System.out.println("--- Test 11: Contar Cuentas ---");
        
        long totalCuentas = servicio.contarCuentas();
        
        System.out.println("✅ Total de cuentas registradas: " + totalCuentas);
        System.out.println();
    }
} 