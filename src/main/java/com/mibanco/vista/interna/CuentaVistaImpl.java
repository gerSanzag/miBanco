package com.mibanco.vista.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.vista.CuentaVista;
import com.mibanco.vista.util.Consola;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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
        consola.mostrar("6. Buscar cuentas por titular\n");
        consola.mostrar("7. Buscar cuentas por tipo\n");
        consola.mostrar("8. Listar cuentas activas\n");
        consola.mostrar("9. Mostrar cuentas eliminadas\n");
        consola.mostrar("10. Restaurar cuenta eliminada\n");
        consola.mostrar("11. Contar total de cuentas\n");
        consola.mostrar("12. Consultar saldo de cuenta\n");
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
        
        // Función anidada para validar campos obligatorios
        Function<Supplier<Optional<?>>, String> solicitarCampo = supplier -> {
            Optional<?> valor;
            do {
                valor = supplier.get();
                if (valor.isEmpty()) {
                    mostrarMensaje("Error: Campo obligatorio. Por favor, inténtelo de nuevo.");
                }
            } while (valor.isEmpty());
            return valor.get().toString();
        };
        
        // Usar la función anidada para cada campo
        datos.put("idTitular", solicitarCampo.apply(this::solicitarIdTitular));
        datos.put("tipoCuenta", solicitarCampo.apply(this::solicitarTipoCuenta));
        datos.put("saldoInicial", solicitarCampo.apply(this::solicitarSaldoInicial));
        
        return datos;
    }

    @Override
    public Optional<Long> solicitarNumeroCuenta() {
        consola.mostrar("Introduzca el número de cuenta: ");
        try {
            return Optional.of(Long.parseLong(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El número de cuenta debe ser un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> solicitarDatosParaActualizar(CuentaDTO cuentaActual) {
        consola.mostrar("\n--- Datos modificables de la Cuenta: " + cuentaActual.getNumeroCuenta() + " ---\n");
        consola.mostrar("Estado actual: " + (cuentaActual.isActiva() ? "Activa" : "Inactiva") + "\n");
        
        consola.mostrar("\n¿Desea cambiar el estado de la cuenta?\n");
        consola.mostrar("1. Cambiar estado (Activar/Desactivar)\n");
        consola.mostrar("0. Cancelar\n");
        
        Optional<Integer> opcion = obtenerOpcion();
        
        return opcion.map(opt -> {
            Map<String, String> datos = switch (opt) {
                case 1 -> {
                    Optional<Boolean> nuevoEstado = solicitarNuevoEstado();
                    yield nuevoEstado.map(estado -> Map.of("activa", String.valueOf(estado))).orElse(Map.of());
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
        
        // Recorrer el enum dinámicamente usando Stream
        TipoCuenta[] tipos = TipoCuenta.values();
        IntStream.range(0, tipos.length)
            .forEach(i -> consola.mostrar((i + 1) + ". " + tipos[i].name() + "\n"));
        
        consola.mostrar("Seleccione el tipo de cuenta (1-" + tipos.length + "): ");
        
        try {
            int opcion = Integer.parseInt(consola.leerLinea());
            if (opcion >= 1 && opcion <= tipos.length) {
                return Optional.of(tipos[opcion - 1].name());
            } else {
                mostrarMensaje("Error: Opción no válida.");
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
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

    @Override
    public Optional<Long> solicitarIdTitularParaBuscar() {
        consola.mostrar("Introduzca el ID del titular para buscar sus cuentas: ");
        try {
            return Optional.of(Long.parseLong(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> solicitarTipoCuentaParaBuscar() {
        consola.mostrar("\nTipos de cuenta disponibles:\n");
        
        // Recorrer el enum dinámicamente usando Stream
        TipoCuenta[] tipos = TipoCuenta.values();
        IntStream.range(0, tipos.length)
            .forEach(i -> consola.mostrar((i + 1) + ". " + tipos[i].name() + "\n"));
        
        consola.mostrar("Seleccione el tipo de cuenta para buscar (1-" + tipos.length + "): ");
        
        try {
            int opcion = Integer.parseInt(consola.leerLinea());
            if (opcion >= 1 && opcion <= tipos.length) {
                return Optional.of(tipos[opcion - 1].name());
            } else {
                mostrarMensaje("Error: Opción no válida.");
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> solicitarNumeroCuentaParaRestaurar() {
        consola.mostrar("Introduzca el número de cuenta a restaurar: ");
        try {
            return Optional.of(Long.parseLong(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El número de cuenta debe ser un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> solicitarNuevoEstado() {
        consola.mostrar("¿Activar cuenta? (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        return "s".equals(respuesta) ? Optional.of(true) : 
               "n".equals(respuesta) ? Optional.of(false) : Optional.empty();
    }

    @Override
    public void mostrarCuentasEliminadas(List<CuentaDTO> cuentas) {
        consola.mostrar("\n--- Listado de Cuentas Eliminadas ---\n");
        
        Optional.of(cuentas)
            .filter(lista -> !lista.isEmpty())
            .ifPresentOrElse(
                lista -> {
                    lista.forEach(cuenta -> mostrarCuenta(Optional.of(cuenta)));
                    consola.mostrar("-----------------------------------\n");
                },
                () -> consola.mostrar("No hay cuentas eliminadas.\n")
            );
    }

    @Override
    public void mostrarTotalCuentas(long total) {
        consola.mostrar("\n--- Total de Cuentas ---\n");
        consola.mostrar("Total de cuentas registradas: " + total + "\n");
        consola.mostrar("---------------------------\n");
    }

    @Override
    public void mostrarSaldoCuenta(CuentaDTO cuenta) {
        consola.mostrar("\n--- Consulta de Saldo ---\n");
        consola.mostrar("Número de cuenta: " + cuenta.getNumeroCuenta() + "\n");
        consola.mostrar("Titular: " + cuenta.getTitular().getNombre() + " " + cuenta.getTitular().getApellido() + "\n");
        consola.mostrar("Tipo de cuenta: " + cuenta.getTipo() + "\n");
        consola.mostrar("Saldo actual: " + cuenta.getSaldo() + "\n");
        consola.mostrar("Estado: " + (cuenta.isActiva() ? "Activa" : "Inactiva") + "\n");
        consola.mostrar("---------------------------\n");
    }
} 