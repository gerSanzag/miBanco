package com.mibanco.controlador.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.controlador.CuentaControlador;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.interna.ServicioFactoria;
import com.mibanco.vista.CuentaVista;
import com.mibanco.vista.interna.factoriaVista;
import com.mibanco.modelo.enums.TipoCuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del controlador para la entidad Cuenta.
 * Visibilidad de paquete para que solo pueda ser instanciada a través de ControladorFactoria.
 * Maneja la lógica de coordinación entre la vista y el servicio.
 */
class CuentaControladorImpl implements CuentaControlador {

    private final CuentaServicio servicioCuenta;
    private final ClienteServicio servicioCliente;
    private final CuentaVista vista;

    /**
     * Constructor con visibilidad de paquete.
     * Obtiene las instancias del servicio y la vista a través de sus respectivas fábricas.
     */
    CuentaControladorImpl() {
        this.servicioCuenta = ServicioFactoria.obtenerInstancia().obtenerServicioCuenta();
        this.servicioCliente = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        this.vista = factoriaVista.obtenerInstancia().obtenerVistaCuenta();
    }

    @Override
    public void crearCuenta() {
        vista.mostrarMensaje("--- Creación de Nueva Cuenta ---");
        
        // 1. Solicitar datos a la vista
        Map<String, String> datosCuenta = vista.solicitarDatosParaCrear();
        
        // 2. Validar que tenemos todos los datos necesarios
        if (!datosCuenta.containsKey("idTitular") || !datosCuenta.containsKey("tipoCuenta") || !datosCuenta.containsKey("saldoInicial")) {
            vista.mostrarMensaje("Error: Faltan datos requeridos para crear la cuenta.");
            return;
        }
        
        // 3. Obtener el titular
        Optional<ClienteDTO> titular = servicioCliente.obtenerClientePorId(Optional.of(Long.parseLong(datosCuenta.get("idTitular"))));
        
        if (titular.isEmpty()) {
            vista.mostrarMensaje("Error: El titular especificado no existe.");
            return;
        }
        
        // 4. Crear el DTO de la cuenta
        CuentaDTO nuevaCuenta = CuentaDTO.builder()
            .titular(titular.get())
            .tipo(TipoCuenta.valueOf(datosCuenta.get("tipoCuenta")))
            .saldo(new BigDecimal(datosCuenta.get("saldoInicial")))
            .activa(true)
            .build();
        
        // 5. Guardar la cuenta
        Optional<CuentaDTO> cuentaCreada = servicioCuenta.crearCuenta(Optional.of(nuevaCuenta));
        
        // 6. Mostrar resultado
        cuentaCreada.ifPresentOrElse(
            cuenta -> vista.mostrarMensaje("Cuenta creada exitosamente con número: " + cuenta.getNumeroCuenta()),
            () -> vista.mostrarMensaje("Error al crear la cuenta.")
        );
    }

    @Override
    public void buscarCuentaPorNumero() {
        // 1. Solicitar número a la vista
        Optional<Long> numeroCuenta = vista.solicitarNumeroCuenta();
        
        // 2. Buscar en el servicio
        numeroCuenta.ifPresent(numero -> {
            Optional<CuentaDTO> cuenta = servicioCuenta.obtenerCuentaPorNumero(Optional.of(numero));
            
            // 3. Mostrar resultado
            vista.mostrarCuenta(cuenta);
        });
    }

    @Override
    public void listarTodasLasCuentas() {
        // 1. Obtener lista del servicio
        Optional<List<CuentaDTO>> cuentas = servicioCuenta.obtenerTodasLasCuentas();
        
        // 2. Mostrar resultado
        cuentas.ifPresentOrElse(
            listaCuentas -> vista.mostrarTodasLasCuentas(listaCuentas),
            () -> vista.mostrarMensaje("Error al obtener la lista de cuentas.")
        );
    }

    @Override
    public void actualizarCuenta() {
        // 1. Solicitar número
        Optional<Long> numeroCuenta = vista.solicitarNumeroCuenta();
        
        numeroCuenta.ifPresent(numero -> {
            // 2. Buscar cuenta actual
            Optional<CuentaDTO> cuentaActual = servicioCuenta.obtenerCuentaPorNumero(Optional.of(numero));
            
            cuentaActual.ifPresent(cuenta -> {
                // 3. Solicitar datos de modificación
                Map<String, String> datosModificacion = vista.solicitarDatosParaActualizar(cuenta);
                
                // 4. Mostrar y confirmar antes de actualizar
                if (vista.confirmarAccion(cuenta, "Datos a Modificar", "¿Confirma la actualización?")) {
                    // 5. Actualizar cuenta
                    procesarActualizacionCuenta(cuenta, datosModificacion);
                    vista.mostrarMensaje("Cuenta actualizada exitosamente.");
                } else {
                    vista.mostrarMensaje("Actualización cancelada.");
                }
            });
        });
    }

    @Override
    public void eliminarCuenta() {
        // 1. Solicitar número
        Optional<Long> numeroCuenta = vista.solicitarNumeroCuenta();
        
        numeroCuenta.ifPresent(numero -> {
            // 2. Buscar cuenta
            Optional<CuentaDTO> cuenta = servicioCuenta.obtenerCuentaPorNumero(Optional.of(numero));
            
            cuenta.ifPresent(c -> {
                // 3. Confirmar eliminación
                if (vista.confirmarAccion(c, "Cuenta a Eliminar", "¿Está seguro de que desea eliminar esta cuenta?")) {
                    // 4. Eliminar
                    boolean eliminada = servicioCuenta.eliminarCuenta(Optional.of(numero));
                    
                    if (eliminada) {
                        vista.mostrarMensaje("Cuenta eliminada exitosamente.");
                    } else {
                        vista.mostrarMensaje("Error al eliminar la cuenta.");
                    }
                } else {
                    vista.mostrarMensaje("Eliminación cancelada.");
                }
            });
        });
    }

    @Override
    public void mostrarMenuPrincipal() {
        boolean continuar = true;
        
        while (continuar) {
            vista.mostrarMenuPrincipal();
            Optional<Integer> opcion = vista.obtenerOpcion();
            
            opcion.ifPresent(opt -> {
                switch (opt) {
                    case 1 -> crearCuenta();
                    case 2 -> buscarCuentaPorNumero();
                    case 3 -> listarTodasLasCuentas();
                    case 4 -> actualizarCuenta();
                    case 5 -> eliminarCuenta();
                    case 6 -> buscarCuentasPorTitular();
                    case 7 -> buscarCuentasPorTipo();
                    case 8 -> listarCuentasActivas();
                    case 9 -> mostrarCuentasEliminadas();
                    case 10 -> restaurarCuenta();
                    case 11 -> contarCuentas();
                    case 12 -> consultarSaldoCuenta();
                    case 0 -> vista.mostrarMensaje("Volviendo al menú principal...");
                    default -> vista.mostrarMensaje("Opción no válida.");
                }
            });
            
            // Si la opción es 0, salir del bucle
            if (opcion.isPresent() && opcion.get() == 0) {
                continuar = false;
            }
        }
    }

    /**
     * Actualiza el estado de una cuenta.
     * Solo permite modificar el estado activo/inactivo de la cuenta.
     * 
     * @param cuentaActual la cuenta actual
     * @param datosModificacion los datos a modificar (solo estado)
     */
    private void procesarActualizacionCuenta(CuentaDTO cuentaActual, Map<String, String> datosModificacion) {
        Long numeroCuenta = cuentaActual.getNumeroCuenta();
        
        // Si no hay datos para modificar, no hacer nada
        if (datosModificacion.isEmpty()) {
            vista.mostrarMensaje("No se realizaron modificaciones.");
            return;
        }
        
        // Solo procesar actualización de estado
        Optional.of(datosModificacion)
            .filter(datos -> datos.containsKey("activa"))
            .ifPresentOrElse(
                datos -> {
                    boolean nuevoEstado = Boolean.parseBoolean(datos.get("activa"));
                    servicioCuenta.actualizarEstadoCuenta(numeroCuenta, Optional.of(nuevoEstado));
                },
                () -> vista.mostrarMensaje("No se especificó un cambio de estado válido.")
            );
    }

    @Override
    public void buscarCuentasPorTitular() {
        // 1. Solicitar ID del titular a la vista
        Optional<Long> idTitular = vista.solicitarIdTitularParaBuscar();
        
        // 2. Buscar cuentas en el servicio
        idTitular.ifPresent(id -> {
            Optional<List<CuentaDTO>> cuentas = servicioCuenta.buscarPorTitularId(Optional.of(id));
            
            // 3. Mostrar resultado
            cuentas.ifPresentOrElse(
                listaCuentas -> {
                    if (listaCuentas.isEmpty()) {
                        vista.mostrarMensaje("No se encontraron cuentas para el titular especificado.");
                    } else {
                        vista.mostrarTodasLasCuentas(listaCuentas);
                    }
                },
                () -> vista.mostrarMensaje("Error al buscar cuentas del titular.")
            );
        });
    }

    @Override
    public void buscarCuentasPorTipo() {
        // 1. Solicitar tipo de cuenta a la vista
        Optional<String> tipoCuenta = vista.solicitarTipoCuentaParaBuscar();
        
        // 2. Buscar cuentas en el servicio
        tipoCuenta.ifPresent(tipo -> {
            Optional<List<CuentaDTO>> cuentas = servicioCuenta.buscarPorTipo(Optional.of(TipoCuenta.valueOf(tipo)));
            
            // 3. Mostrar resultado
            cuentas.ifPresentOrElse(
                listaCuentas -> {
                    if (listaCuentas.isEmpty()) {
                        vista.mostrarMensaje("No se encontraron cuentas del tipo especificado.");
                    } else {
                        vista.mostrarTodasLasCuentas(listaCuentas);
                    }
                },
                () -> vista.mostrarMensaje("Error al buscar cuentas por tipo.")
            );
        });
    }

    @Override
    public void listarCuentasActivas() {
        // 1. Obtener cuentas activas del servicio
        Optional<List<CuentaDTO>> cuentas = servicioCuenta.buscarActivas();
        
        // 2. Mostrar resultado
        cuentas.ifPresentOrElse(
            listaCuentas -> {
                if (listaCuentas.isEmpty()) {
                    vista.mostrarMensaje("No hay cuentas activas.");
                } else {
                    vista.mostrarTodasLasCuentas(listaCuentas);
                }
            },
            () -> vista.mostrarMensaje("Error al obtener cuentas activas.")
        );
    }

    @Override
    public void mostrarCuentasEliminadas() {
        // 1. Obtener cuentas eliminadas del servicio
        List<CuentaDTO> cuentasEliminadas = servicioCuenta.obtenerCuentasEliminadas();
        
        // 2. Mostrar resultado
        vista.mostrarCuentasEliminadas(cuentasEliminadas);
    }

    @Override
    public void restaurarCuenta() {
        // 1. Solicitar número de cuenta a la vista
        Optional<Long> numeroCuenta = vista.solicitarNumeroCuentaParaRestaurar();
        
        // 2. Restaurar cuenta en el servicio
        numeroCuenta.ifPresent(numero -> {
            Optional<CuentaDTO> cuentaRestaurada = servicioCuenta.restaurarCuenta(Optional.of(numero));
            
            // 3. Mostrar resultado
            cuentaRestaurada.ifPresentOrElse(
                cuenta -> vista.mostrarMensaje("Cuenta restaurada exitosamente: " + cuenta.getNumeroCuenta()),
                () -> vista.mostrarMensaje("No se pudo restaurar la cuenta. Verifique que exista en la lista de eliminadas.")
            );
        });
    }

    @Override
    public void contarCuentas() {
        // 1. Obtener total de cuentas del servicio
        long total = servicioCuenta.contarCuentas();
        
        // 2. Mostrar resultado
        vista.mostrarTotalCuentas(total);
    }

    @Override
    public void consultarSaldoCuenta() {
        // 1. Solicitar número de cuenta
        Optional<Long> numeroCuenta = vista.solicitarNumeroCuenta();
        
        // 2. Buscar cuenta en el servicio
        numeroCuenta.ifPresent(numero -> {
            Optional<CuentaDTO> cuenta = servicioCuenta.obtenerCuentaPorNumero(Optional.of(numero));
            
            // 3. Mostrar saldo
            cuenta.ifPresentOrElse(
                cuentaEncontrada -> vista.mostrarSaldoCuenta(cuentaEncontrada),
                () -> vista.mostrarMensaje("Cuenta no encontrada.")
            );
        });
    }
} 