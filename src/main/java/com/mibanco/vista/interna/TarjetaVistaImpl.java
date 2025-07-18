package com.mibanco.vista.interna;

import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.vista.TarjetaVista;
import com.mibanco.vista.util.Consola;
import com.mibanco.dto.TarjetaDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Implementación de la vista para la entidad Tarjeta.
 * Visibilidad de paquete para que solo pueda ser instanciada a través de VistaFactoria.
 */
class TarjetaVistaImpl extends BaseVistaImpl<TarjetaDTO> implements TarjetaVista {
    
    /**
     * Constructor con visibilidad de paquete.
     * Obtiene la instancia de la consola para la interacción con el usuario.
     */
    TarjetaVistaImpl() {
        super();
    }
    
    @Override
    public void mostrarMenuPrincipal() {
        consola.mostrar("\n--- Menú de Gestión de Tarjetas ---\n");
        consola.mostrar("1. Crear nueva tarjeta\n");
        consola.mostrar("2. Buscar tarjeta por número\n");
        consola.mostrar("3. Listar todas las tarjetas\n");
        consola.mostrar("4. Actualizar tarjeta\n");
        consola.mostrar("5. Eliminar tarjeta\n");
        consola.mostrar("6. Buscar tarjetas por titular\n");
        consola.mostrar("7. Buscar tarjetas por tipo\n");
        consola.mostrar("8. Buscar tarjetas por cuenta asociada\n");
        consola.mostrar("9. Listar tarjetas activas\n");
        consola.mostrar("10. Mostrar tarjetas eliminadas\n");
        consola.mostrar("11. Restaurar tarjeta eliminada\n");
        consola.mostrar("12. Contar total de tarjetas\n");
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
        consola.mostrar("\n--- Creación de Nueva Tarjeta ---\n");
        return solicitarDatosGenerico(Tarjeta.class);
    }

    // Implementación del método abstracto de BaseVista
    @Override
    public Map<String, String> solicitarDatosParaCrear(String mensaje) {
        consola.mostrar("\n--- " + mensaje + " ---\n");
        return solicitarDatosGenerico(Tarjeta.class);
    }
    
    @Override
    public Optional<String> solicitarNumeroTarjeta() {
        consola.mostrar("Introduzca el número de tarjeta: ");
        String numeroTarjeta = consola.leerLinea();
        return numeroTarjeta.isEmpty() ? Optional.empty() : Optional.of(numeroTarjeta);
    }
    
    @Override
    public Map<String, String> solicitarDatosParaActualizar(TarjetaDTO tarjetaActual) {
        consola.mostrar("\n--- Datos modificables de la Tarjeta: " + tarjetaActual.getNumero() + " ---\n");
        consola.mostrar("Estado actual: " + (tarjetaActual.isActiva() ? "Activa" : "Inactiva") + "\n");
        consola.mostrar("Fecha de expiración actual: " + tarjetaActual.getFechaExpiracion() + "\n");
        
        consola.mostrar("\n¿Qué desea modificar?\n");
        consola.mostrar("1. Cambiar estado (Activar/Desactivar)\n");
        consola.mostrar("2. Cambiar fecha de expiración\n");
        consola.mostrar("0. Cancelar\n");
        
        Optional<Integer> opcion = obtenerOpcion();
        
        return opcion.map(opt -> {
            Map<String, String> datos = switch (opt) {
                case 1 -> {
                    Optional<Boolean> nuevoEstado = solicitarNuevoEstado();
                    yield nuevoEstado.map(estado -> Map.of("activa", String.valueOf(estado))).orElse(Map.of());
                }
                case 2 -> {
                    Optional<LocalDate> nuevaFecha = solicitarFecha();
                    yield nuevaFecha.map(fecha -> Map.of("fechaExpiracion", fecha.toString())).orElse(Map.of());
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
    public void mostrarTarjeta(Optional<TarjetaDTO> tarjetaOpt) {
        tarjetaOpt.ifPresentOrElse(
            tarjeta -> {
                consola.mostrar("\n--- Detalles de la Tarjeta ---\n");
                consola.mostrar("Número: " + tarjeta.getNumero() + "\n");
                consola.mostrar("Titular: " + tarjeta.getTitular().getNombre() + " " + tarjeta.getTitular().getApellido() + "\n");
                consola.mostrar("Tipo: " + tarjeta.getTipo() + "\n");
                consola.mostrar("Cuenta asociada: " + tarjeta.getNumeroCuentaAsociada() + "\n");
                consola.mostrar("Fecha de expiración: " + tarjeta.getFechaExpiracion() + "\n");
                consola.mostrar("Estado: " + (tarjeta.isActiva() ? "Activa" : "Inactiva") + "\n");
                consola.mostrar("----------------------------\n");
            },
            () -> mostrarMensaje("Tarjeta no encontrada.")
        );
    }
    
    @Override
    public void mostrarListaTarjetas(List<TarjetaDTO> tarjetas) {
        consola.mostrar("\n--- Listado de Tarjetas ---\n");
        
        Optional.of(tarjetas)
            .filter(lista -> !lista.isEmpty())
            .ifPresentOrElse(
                lista -> {
                    lista.forEach(tarjeta -> mostrarTarjeta(Optional.of(tarjeta)));
                    consola.mostrar("---------------------------\n");
                },
                () -> consola.mostrar("No hay tarjetas registradas.\n")
            );
    }
    
    @Override
    public void mostrarMensaje(String mensaje) {
        consola.mostrar(">> " + mensaje + "\n");
    }
    
    @Override
    public boolean confirmarAccion(TarjetaDTO tarjeta, String titulo, String mensaje) {
        consola.mostrar("\n--- " + titulo + " ---\n");
        mostrarTarjeta(Optional.of(tarjeta));
        
        consola.mostrar("\n" + mensaje + " (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        
        return "s".equals(respuesta);
    }

    // Implementación del método abstracto de BaseVistaImpl
    @Override
    public void mostrarEntidad(Optional<TarjetaDTO> entidad) {
        mostrarTarjeta(entidad);
    }
    
    // Métodos específicos para Tarjeta que no se pueden generalizar
    @Override
    public Optional<Long> solicitarIdTitular() {
        consola.mostrar("Introduzca el ID del titular: ");
        try {
            String input = consola.leerLinea();
            return input.isEmpty() ? Optional.empty() : Optional.of(Long.parseLong(input));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<String> solicitarTipoTarjeta() {
        consola.mostrar("\nTipos de tarjeta disponibles:\n");
        
        // Recorrer el enum dinámicamente usando Stream
        TipoTarjeta[] tipos = TipoTarjeta.values();
        IntStream.range(0, tipos.length)
            .forEach(i -> consola.mostrar((i + 1) + ". " + tipos[i].name() + "\n"));
        
        consola.mostrar("Seleccione el tipo de tarjeta (1-" + tipos.length + "): ");
        
        try {
            int seleccion = Integer.parseInt(consola.leerLinea());
            if (seleccion >= 1 && seleccion <= tipos.length) {
                return Optional.of(tipos[seleccion - 1].name());
            } else {
                mostrarMensaje("Error: Selección fuera de rango.");
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<String> solicitarCvv() {
        consola.mostrar("Introduzca el CVV (3 dígitos): ");
        String cvv = consola.leerLinea();
        if (cvv.length() == 3 && cvv.matches("\\d{3}")) {
            return Optional.of(cvv);
        } else {
            mostrarMensaje("Error: El CVV debe tener exactamente 3 dígitos.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<String> solicitarNumeroCuentaAsociada() {
        consola.mostrar("Introduzca el número de cuenta asociada: ");
        String numeroCuenta = consola.leerLinea();
        return numeroCuenta.isEmpty() ? Optional.empty() : Optional.of(numeroCuenta);
    }
    
    @Override
    public Optional<LocalDate> solicitarFecha() {
        consola.mostrar("Introduzca la fecha de expiración (yyyy-MM-dd): ");
        try {
            String fechaStr = consola.leerLinea();
            if (fechaStr.isEmpty()) {
                return Optional.empty();
            }
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return Optional.of(fecha);
        } catch (DateTimeParseException e) {
            mostrarMensaje("Error: Formato de fecha incorrecto. Use yyyy-MM-dd.");
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Boolean> solicitarNuevoEstado() {
        consola.mostrar("¿Desea activar la tarjeta? (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        return "s".equals(respuesta) ? Optional.of(true) : 
               "n".equals(respuesta) ? Optional.of(false) : Optional.empty();
    }
    
    @Override
    public void mostrarTarjetasEliminadas(List<TarjetaDTO> tarjetas) {
        consola.mostrar("\n--- Tarjetas Eliminadas ---\n");
        
        Optional.of(tarjetas)
            .filter(lista -> !lista.isEmpty())
            .ifPresentOrElse(
                lista -> {
                    lista.forEach(tarjeta -> mostrarTarjeta(Optional.of(tarjeta)));
                    consola.mostrar("---------------------------\n");
                },
                () -> consola.mostrar("No hay tarjetas eliminadas.\n")
            );
    }
    
    @Override
    public void mostrarTotalTarjetas(long total) {
        consola.mostrar("\n--- Total de Tarjetas ---\n");
        consola.mostrar("Total de tarjetas registradas: " + total + "\n");
        consola.mostrar("---------------------------\n");
    }
} 