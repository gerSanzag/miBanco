package com.mibanco.controlador.interna;

import com.mibanco.controlador.TarjetaControlador;
import com.mibanco.dto.TarjetaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.servicio.TarjetaServicio;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.interna.ServicioFactoria;
import com.mibanco.vista.TarjetaVista;
import com.mibanco.vista.interna.factoriaVista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del controlador de Tarjetas
 * Coordina entre la vista y el servicio de tarjetas
 */
class TarjetaControladorImpl implements TarjetaControlador {
    
    private final TarjetaServicio servicioTarjeta;
    private final ClienteServicio servicioCliente;
    private final TarjetaVista vista;
    
    public TarjetaControladorImpl() {
        this.servicioTarjeta = ServicioFactoria.obtenerInstancia().obtenerServicioTarjeta();
        this.servicioCliente = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        this.vista = factoriaVista.obtenerInstancia().obtenerVistaTarjeta();
    }
    
    @Override
    public void crearTarjeta() {
        // 1. Solicitar datos a la vista
        Map<String, String> datosTarjeta = vista.solicitarDatosParaCrear();
        
        // 2. Verificar que la vista devolvió datos válidos
        if (datosTarjeta.isEmpty()) {
            return; // La vista ya mostró el mensaje de error
        }
        
        // 3. Obtener el cliente titular
        Optional<ClienteDTO> titular = servicioCliente.obtenerClientePorId(
            Optional.of(Long.parseLong(datosTarjeta.get("idTitular")))
        );
        
        if (titular.isEmpty()) {
            vista.mostrarMensaje("Error: No se encontró el cliente titular.");
            return;
        }
        
        // 4. Crear tarjeta en el servicio
        TarjetaDTO nuevaTarjeta = TarjetaDTO.builder()
            .titular(titular.get())
            .numeroCuentaAsociada(datosTarjeta.get("numeroCuentaAsociada"))
            .tipo(TipoTarjeta.valueOf(datosTarjeta.get("tipoTarjeta")))
            .activa(true)
            .build();
        
        Optional<TarjetaDTO> tarjetaCreada = servicioTarjeta.crearTarjeta(Optional.of(nuevaTarjeta));
        
        tarjetaCreada.ifPresentOrElse(
            tarjeta -> vista.mostrarMensaje("Tarjeta creada exitosamente: " + tarjeta.getNumero()),
            () -> vista.mostrarMensaje("Error al crear la tarjeta.")
        );
    }
    
    @Override
    public void buscarTarjetaPorNumero() {
        // 1. Solicitar número a la vista
        Optional<String> numeroTarjeta = vista.solicitarNumeroTarjeta();
        
        // 2. Buscar en el servicio
        numeroTarjeta.ifPresent(numero -> {
            Optional<TarjetaDTO> tarjeta = servicioTarjeta.obtenerTarjetaPorNumero(Optional.of(numero));
            
            tarjeta.ifPresentOrElse(
                t -> vista.mostrarTarjeta(Optional.of(t)),
                () -> vista.mostrarMensaje("Tarjeta no encontrada.")
            );
        });
    }
    
    @Override
    public void listarTodasLasTarjetas() {
        Optional<List<TarjetaDTO>> tarjetas = servicioTarjeta.obtenerTodasLasTarjetas();
        
        tarjetas.ifPresentOrElse(
            lista -> {
                if (lista.isEmpty()) {
                    vista.mostrarMensaje("No hay tarjetas registradas.");
                } else {
                    vista.mostrarListaTarjetas(lista);
                }
            },
            () -> vista.mostrarMensaje("Error al obtener la lista de tarjetas.")
        );
    }
    
    @Override
    public void actualizarTarjeta() {
        // 1. Solicitar número
        Optional<String> numeroTarjeta = vista.solicitarNumeroTarjeta();
        
        numeroTarjeta.ifPresent(numero -> {
            // 2. Buscar tarjeta actual
            Optional<TarjetaDTO> tarjetaActual = servicioTarjeta.obtenerTarjetaPorNumero(Optional.of(numero));
            
            if (tarjetaActual.isEmpty()) {
                vista.mostrarMensaje("Tarjeta no encontrada.");
                return;
            }
            
            // 3. Solicitar datos de modificación
            Map<String, String> datosModificacion = vista.solicitarDatosParaActualizar(tarjetaActual.get());
            
            if (datosModificacion.isEmpty()) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }
            
            // 4. Confirmar acción
            if (!vista.confirmarAccion(tarjetaActual.get(), "Confirmar Actualización", "¿Está seguro de que desea actualizar la tarjeta?")) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }
            
            // 5. Procesar actualización
            procesarActualizacionTarjeta(tarjetaActual.get(), datosModificacion);
        });
    }
    
    @Override
    public void eliminarTarjeta() {
        // 1. Solicitar número
        Optional<String> numeroTarjeta = vista.solicitarNumeroTarjeta();
        
        numeroTarjeta.ifPresent(numero -> {
            // 2. Buscar tarjeta para mostrar información
            Optional<TarjetaDTO> tarjetaActual = servicioTarjeta.obtenerTarjetaPorNumero(Optional.of(numero));
            
            if (tarjetaActual.isEmpty()) {
                vista.mostrarMensaje("Tarjeta no encontrada.");
                return;
            }
            
            // 3. Confirmar eliminación
            if (!vista.confirmarAccion(tarjetaActual.get(), "Confirmar Eliminación", "¿Está seguro de que desea eliminar la tarjeta?")) {
                vista.mostrarMensaje("Operación cancelada.");
                return;
            }
            
            // 4. Eliminar tarjeta
            boolean eliminada = servicioTarjeta.eliminarTarjeta(Optional.of(numero));
            
            if (eliminada) {
                vista.mostrarMensaje("Tarjeta eliminada exitosamente.");
            } else {
                vista.mostrarMensaje("Error al eliminar la tarjeta.");
            }
        });
    }
    
    @Override
    public void restaurarTarjeta() {
        // 1. Solicitar número de tarjeta a la vista
        Optional<String> numeroTarjeta = vista.solicitarNumeroTarjeta();
        
        // 2. Restaurar tarjeta en el servicio
        numeroTarjeta.ifPresent(numero -> {
            Optional<TarjetaDTO> tarjetaRestaurada = servicioTarjeta.restaurarTarjeta(Optional.of(numero));
            
            tarjetaRestaurada.ifPresentOrElse(
                tarjeta -> vista.mostrarMensaje("Tarjeta restaurada exitosamente: " + tarjeta.getNumero()),
                () -> vista.mostrarMensaje("Error al restaurar la tarjeta.")
            );
        });
    }
    
    @Override
    public void buscarTarjetasPorTitular() {
        Optional<Long> idTitular = vista.solicitarIdTitular();
        
        if (idTitular.isEmpty()) {
            vista.mostrarMensaje("Búsqueda cancelada.");
            return;
        }
        
        Optional<List<TarjetaDTO>> tarjetas = servicioTarjeta.buscarPorTitularId(idTitular);
        
        tarjetas.ifPresentOrElse(
            lista -> {
                if (lista.isEmpty()) {
                    vista.mostrarMensaje("No hay tarjetas para este titular.");
                } else {
                    vista.mostrarMensaje("Tarjetas del titular encontradas: " + lista.size());
                    vista.mostrarListaTarjetas(lista);
                }
            },
            () -> vista.mostrarMensaje("Error al buscar tarjetas por titular.")
        );
    }
    
    @Override
    public void buscarTarjetasPorTipo() {
        Optional<String> tipoStr = vista.solicitarTipoTarjeta();
        
        if (tipoStr.isEmpty()) {
            vista.mostrarMensaje("Búsqueda cancelada.");
            return;
        }
        
        try {
            TipoTarjeta tipo = TipoTarjeta.valueOf(tipoStr.get());
            Optional<List<TarjetaDTO>> tarjetas = servicioTarjeta.buscarPorTipo(Optional.of(tipo));
            
            tarjetas.ifPresentOrElse(
                lista -> {
                    if (lista.isEmpty()) {
                        vista.mostrarMensaje("No hay tarjetas de este tipo.");
                    } else {
                        vista.mostrarMensaje("Tarjetas de tipo " + tipo + " encontradas: " + lista.size());
                        vista.mostrarListaTarjetas(lista);
                    }
                },
                () -> vista.mostrarMensaje("Error al buscar tarjetas por tipo.")
            );
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje("Tipo de tarjeta no válido.");
        }
    }
    
    @Override
    public void buscarTarjetasPorCuentaAsociada() {
        Optional<String> numeroCuenta = vista.solicitarNumeroCuentaAsociada();
        
        if (numeroCuenta.isEmpty()) {
            vista.mostrarMensaje("Búsqueda cancelada.");
            return;
        }
        
        Optional<List<TarjetaDTO>> tarjetas = servicioTarjeta.buscarPorCuentaAsociada(numeroCuenta);
        
        tarjetas.ifPresentOrElse(
            lista -> {
                if (lista.isEmpty()) {
                    vista.mostrarMensaje("No hay tarjetas asociadas a esta cuenta.");
                } else {
                    vista.mostrarMensaje("Tarjetas asociadas a la cuenta encontradas: " + lista.size());
                    vista.mostrarListaTarjetas(lista);
                }
            },
            () -> vista.mostrarMensaje("Error al buscar tarjetas por cuenta asociada.")
        );
    }
    
    @Override
    public void listarTarjetasActivas() {
        Optional<List<TarjetaDTO>> tarjetas = servicioTarjeta.buscarActivas();
        
        tarjetas.ifPresentOrElse(
            lista -> {
                if (lista.isEmpty()) {
                    vista.mostrarMensaje("No hay tarjetas activas.");
                } else {
                    vista.mostrarMensaje("Tarjetas activas encontradas: " + lista.size());
                    vista.mostrarListaTarjetas(lista);
                }
            },
            () -> vista.mostrarMensaje("Error al obtener tarjetas activas.")
        );
    }
    
    @Override
    public void mostrarTarjetasEliminadas() {
        List<TarjetaDTO> tarjetasEliminadas = servicioTarjeta.obtenerTarjetasEliminadas();
        
        if (tarjetasEliminadas.isEmpty()) {
            vista.mostrarMensaje("No hay tarjetas eliminadas.");
        } else {
            vista.mostrarMensaje("Tarjetas eliminadas encontradas: " + tarjetasEliminadas.size());
            vista.mostrarListaTarjetas(tarjetasEliminadas);
        }
    }
    
    @Override
    public void contarTarjetas() {
        long total = servicioTarjeta.contarTarjetas();
        vista.mostrarMensaje("Total de tarjetas registradas: " + total);
    }
    
    @Override
    public void mostrarMenuPrincipal() {
        boolean continuar = true;
        
        while (continuar) {
            vista.mostrarMensaje("\n=== MENÚ DE TARJETAS ===");
            vista.mostrarMensaje("1. Crear tarjeta");
            vista.mostrarMensaje("2. Buscar tarjeta por número");
            vista.mostrarMensaje("3. Listar todas las tarjetas");
            vista.mostrarMensaje("4. Actualizar tarjeta");
            vista.mostrarMensaje("5. Eliminar tarjeta");
            vista.mostrarMensaje("6. Restaurar tarjeta");
            vista.mostrarMensaje("7. Buscar tarjetas por titular");
            vista.mostrarMensaje("8. Buscar tarjetas por tipo");
            vista.mostrarMensaje("9. Buscar tarjetas por cuenta asociada");
            vista.mostrarMensaje("10. Listar tarjetas activas");
            vista.mostrarMensaje("11. Mostrar tarjetas eliminadas");
            vista.mostrarMensaje("12. Contar tarjetas");
            vista.mostrarMensaje("0. Salir");
            vista.mostrarMensaje("Seleccione una opción: ");
            
            try {
                String opcion = vista.solicitarNumeroTarjeta().orElse("0");
                int opcionInt = Integer.parseInt(opcion);
                
                switch (opcionInt) {
                    case 0 -> continuar = false;
                    case 1 -> crearTarjeta();
                    case 2 -> buscarTarjetaPorNumero();
                    case 3 -> listarTodasLasTarjetas();
                    case 4 -> actualizarTarjeta();
                    case 5 -> eliminarTarjeta();
                    case 6 -> restaurarTarjeta();
                    case 7 -> buscarTarjetasPorTitular();
                    case 8 -> buscarTarjetasPorTipo();
                    case 9 -> buscarTarjetasPorCuentaAsociada();
                    case 10 -> listarTarjetasActivas();
                    case 11 -> mostrarTarjetasEliminadas();
                    case 12 -> contarTarjetas();
                    default -> vista.mostrarMensaje("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                vista.mostrarMensaje("Por favor, introduzca un número válido.");
            }
        }
    }
    
    /**
     * Procesa la actualización de una tarjeta con los datos proporcionados
     * @param tarjetaActual La tarjeta actual
     * @param datosModificacion Los datos a modificar
     */
    private void procesarActualizacionTarjeta(TarjetaDTO tarjetaActual, Map<String, String> datosModificacion) {
        String numeroTarjeta = tarjetaActual.getNumero();
        
        // Si no hay datos para modificar, no hacer nada
        if (datosModificacion.isEmpty()) {
            vista.mostrarMensaje("No se especificaron datos para modificar.");
            return;
        }
        
        // Procesar cada campo modificado
        datosModificacion.forEach((campo, valor) -> {
            switch (campo) {
                case "activa" -> {
                    boolean nuevaActiva = Boolean.parseBoolean(valor);
                    Optional<TarjetaDTO> tarjetaActualizada = servicioTarjeta.actualizarEstadoTarjeta(
                        numeroTarjeta, Optional.of(nuevaActiva)
                    );
                    tarjetaActualizada.ifPresentOrElse(
                        t -> vista.mostrarMensaje("Estado actualizado: " + (t.isActiva() ? "Activa" : "Inactiva")),
                        () -> vista.mostrarMensaje("Error al actualizar el estado.")
                    );
                }
                case "fechaExpiracion" -> {
                    try {
                        LocalDate nuevaFecha = LocalDate.parse(valor, DateTimeFormatter.ISO_LOCAL_DATE);
                        Optional<TarjetaDTO> tarjetaActualizada = servicioTarjeta.actualizarFechaExpiracion(
                            numeroTarjeta, Optional.of(nuevaFecha)
                        );
                        tarjetaActualizada.ifPresentOrElse(
                            t -> vista.mostrarMensaje("Fecha de expiración actualizada: " + t.getFechaExpiracion()),
                            () -> vista.mostrarMensaje("Error al actualizar la fecha de expiración.")
                        );
                    } catch (DateTimeParseException e) {
                        vista.mostrarMensaje("Formato de fecha inválido.");
                    }
                }
                default -> vista.mostrarMensaje("Campo no reconocido: " + campo);
            }
        });
    }
} 