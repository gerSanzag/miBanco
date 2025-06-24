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
        Optional<String> numeroCuenta = vista.solicitarNumeroCuenta();
        
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
        Optional<String> numeroCuenta = vista.solicitarNumeroCuenta();
        
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
        Optional<String> numeroCuenta = vista.solicitarNumeroCuenta();
        
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
     * Actualiza una cuenta usando los métodos del servicio según la cantidad de campos a modificar.
     * Si se modifica 1 campo, usa métodos individuales; si se modifican múltiples campos, usa método múltiple.
     * 
     * @param cuentaActual la cuenta actual
     * @param datosModificacion los datos a modificar
     */
    private void procesarActualizacionCuenta(CuentaDTO cuentaActual, Map<String, String> datosModificacion) {
        String numeroCuenta = cuentaActual.getNumeroCuenta();
        
        // Si no hay datos para modificar, no hacer nada
        if (datosModificacion.isEmpty()) {
            vista.mostrarMensaje("No se realizaron modificaciones.");
            return;
        }
        
        // Si tiene 1 campo, método individual; si tiene más, método múltiple
        Optional.of(datosModificacion)
            .filter(datos -> datos.size() == 1)
            .ifPresentOrElse(
                datos -> datosModificacion.forEach((campo, valor) -> {
                    switch (campo) {
                        case "saldo" -> servicioCuenta.actualizarSaldoCuenta(numeroCuenta, Optional.of(new BigDecimal(valor)));
                        case "activa" -> servicioCuenta.actualizarEstadoCuenta(numeroCuenta, Optional.of(Boolean.parseBoolean(valor)));
                    }
                }),
                () -> {
                    // Para múltiples campos, crear un DTO temporal solo con los campos a modificar
                    CuentaDTO cuentaModificada = CuentaDTO.builder()
                        .saldo(datosModificacion.containsKey("saldo") ? new BigDecimal(datosModificacion.get("saldo")) : cuentaActual.getSaldo())
                        .activa(datosModificacion.containsKey("activa") ? Boolean.parseBoolean(datosModificacion.get("activa")) : cuentaActual.isActiva())
                        .build();
                    
                    servicioCuenta.actualizarVariosCampos(numeroCuenta, Optional.of(cuentaModificada));
                }
            );
    }
} 