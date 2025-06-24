package com.mibanco.vista.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.vista.CuentaVista;
import com.mibanco.vista.util.Consola;
import com.mibanco.modelo.enums.TipoCuenta;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Implementación de la vista para la entidad Cuenta.
 * Visibilidad de paquete para que solo pueda ser instanciada a través de VistaFactoria.
 */
class CuentaVistaImpl implements CuentaVista {

    private final Consola consola;

    /**
     * Constructor con visibilidad de paquete.
     * Obtiene la instancia de la consola para la interacción con el usuario.
     */
    CuentaVistaImpl() {
        this.consola = new ConsolaImpl(new Scanner(System.in));
    }

    @Override
    public void mostrarMenuPrincipal() {
        consola.mostrar("\n--- Menú de Gestión de Cuentas ---\n");
        consola.mostrar("1. Crear nueva cuenta\n");
        consola.mostrar("2. Buscar cuenta por número\n");
        consola.mostrar("3. Listar todas las cuentas\n");
        consola.mostrar("4. Actualizar cuenta\n");
        consola.mostrar("5. Eliminar cuenta\n");
        consola.mostrar("0. Volver al menú principal\n");
        consola.mostrar("------------------------------------\n");
    }

    @Override
    public Optional<Integer> obtenerOpcion() {
        consola.mostrar("Seleccione una opción: ");
        try {
            return Optional.of(Integer.parseInt(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> solicitarDatosParaCrear() {
        consola.mostrar("\n--- Creación de Nueva Cuenta ---\n");
        Map<String, String> datos = new HashMap<>();
        
        // Solicitar ID del titular
        Optional<Long> idTitular = solicitarIdTitular();
        idTitular.ifPresent(id -> datos.put("idTitular", id.toString()));
        
        // Solicitar tipo de cuenta
        Optional<String> tipoCuenta = solicitarTipoCuenta();
        tipoCuenta.ifPresent(tipo -> datos.put("tipoCuenta", tipo));
        
        // Solicitar saldo inicial
        Optional<BigDecimal> saldoInicial = solicitarSaldoInicial();
        saldoInicial.ifPresent(saldo -> datos.put("saldoInicial", saldo.toString()));
        
        return datos;
    }

    @Override
    public Optional<String> solicitarNumeroCuenta() {
        consola.mostrar("Introduzca el número de cuenta: ");
        String numeroCuenta = consola.leerLinea();
        return numeroCuenta.isEmpty() ? Optional.empty() : Optional.of(numeroCuenta);
    }

    @Override
    public Map<String, String> solicitarDatosParaActualizar(CuentaDTO cuentaActual) {
        consola.mostrar("\n--- Datos modificables de la Cuenta: " + cuentaActual.getNumeroCuenta() + " ---\n");
        consola.mostrar("1. Saldo: " + cuentaActual.getSaldo() + "\n");
        consola.mostrar("2. Estado: " + (cuentaActual.isActiva() ? "Activa" : "Inactiva") + "\n");
        
        consola.mostrar("\n¿Cómo desea modificar?\n");
        consola.mostrar("1. Solo Saldo\n");
        consola.mostrar("2. Solo Estado\n");
        consola.mostrar("3. Ambos campos\n");
        consola.mostrar("0. Cancelar\n");
        
        Optional<Integer> opcion = obtenerOpcion();
        
        return opcion.map(opt -> {
            Map<String, String> datos = switch (opt) {
                case 1 -> {
                    Optional<BigDecimal> nuevoSaldo = solicitarSaldoInicial();
                    yield nuevoSaldo.map(saldo -> Map.of("saldo", saldo.toString())).orElse(Map.of());
                }
                case 2 -> {
                    consola.mostrar("¿Activar cuenta? (s/n): ");
                    String respuesta = consola.leerLinea().toLowerCase();
                    boolean activar = "s".equals(respuesta);
                    yield Map.of("activa", String.valueOf(activar));
                }
                case 3 -> {
                    Optional<BigDecimal> nuevoSaldo = solicitarSaldoInicial();
                    consola.mostrar("¿Activar cuenta? (s/n): ");
                    String respuesta = consola.leerLinea().toLowerCase();
                    boolean activar = "s".equals(respuesta);
                    
                    Map<String, String> datosCompletos = new HashMap<>();
                    nuevoSaldo.ifPresent(saldo -> datosCompletos.put("saldo", saldo.toString()));
                    datosCompletos.put("activa", String.valueOf(activar));
                    yield datosCompletos;
                }
                case 0 -> {
                    mostrarMensaje("Operación cancelada.");
                    yield Map.of();
                }
                default -> {
                    mostrarMensaje("Opción no válida.");
                    yield Map.of();
                }
            };
            return datos;
        }).orElse(Map.of());
    }

    @Override
    public void mostrarCuenta(Optional<CuentaDTO> cuentaOpt) {
        cuentaOpt.ifPresentOrElse(
            cuenta -> {
                consola.mostrar("\n--- Detalles de la Cuenta ---\n");
                consola.mostrar("Número: " + cuenta.getNumeroCuenta() + "\n");
                consola.mostrar("Titular: " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido() + "\n");
                consola.mostrar("Tipo: " + cuenta.getTipo() + "\n");
                consola.mostrar("Saldo: " + cuenta.getSaldo() + "\n");
                consola.mostrar("Fecha Creación: " + cuenta.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n");
                consola.mostrar("Estado: " + (cuenta.isActiva() ? "Activa" : "Inactiva") + "\n");
                consola.mostrar("----------------------------\n");
            },
            () -> mostrarMensaje("Cuenta no encontrada.")
        );
    }

    @Override
    public void mostrarTodasLasCuentas(List<CuentaDTO> cuentas) {
        consola.mostrar("\n--- Listado de Cuentas ---\n");
        
        Optional.of(cuentas)
            .filter(lista -> !lista.isEmpty())
            .ifPresentOrElse(
                lista -> {
                    lista.forEach(cuenta -> mostrarCuenta(Optional.of(cuenta)));
                    consola.mostrar("---------------------------\n");
                },
                () -> consola.mostrar("No hay cuentas registradas.\n")
            );
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        consola.mostrar(">> " + mensaje + "\n");
    }

    @Override
    public boolean confirmarAccion(CuentaDTO cuenta, String titulo, String mensaje) {
        consola.mostrar("\n--- " + titulo + " ---\n");
        mostrarCuenta(Optional.of(cuenta));
        
        consola.mostrar("\n" + mensaje + " (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        
        return "s".equals(respuesta);
    }

    @Override
    public Optional<Long> solicitarIdTitular() {
        consola.mostrar("Introduzca el ID del titular: ");
        try {
            return Optional.of(Long.parseLong(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> solicitarTipoCuenta() {
        consola.mostrar("\nTipos de cuenta disponibles:\n");
        consola.mostrar("1. AHORRO\n");
        consola.mostrar("2. CORRIENTE\n");
        consola.mostrar("3. PLAZO_FIJO\n");
        consola.mostrar("Seleccione el tipo de cuenta (1-3): ");
        
        try {
            int opcion = Integer.parseInt(consola.leerLinea());
            return switch (opcion) {
                case 1 -> Optional.of("AHORRO");
                case 2 -> Optional.of("CORRIENTE");
                case 3 -> Optional.of("PLAZO_FIJO");
                default -> {
                    mostrarMensaje("Opción no válida. Se usará AHORRO por defecto.");
                    yield Optional.of("AHORRO");
                }
            };
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido. Se usará AHORRO por defecto.");
            return Optional.of("AHORRO");
        }
    }

    @Override
    public Optional<BigDecimal> solicitarSaldoInicial() {
        consola.mostrar("Introduzca el saldo inicial: ");
        try {
            return Optional.of(new BigDecimal(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El saldo debe ser un número válido.");
            return Optional.empty();
        }
    }
} 