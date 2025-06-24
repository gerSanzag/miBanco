package com.mibanco.controlador.interna;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.controlador.ClienteControlador;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.interna.ServicioFactoria;
import com.mibanco.vista.ClienteVista;
import com.mibanco.vista.interna.factoriaVista;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del controlador para la entidad Cliente.
 * Visibilidad de paquete para que solo pueda ser instanciada a través de ControladorFactoria.
 * Maneja la lógica de coordinación entre la vista y el servicio.
 */
class ClienteControladorImpl implements ClienteControlador {

    private final ClienteServicio servicio;
    private final ClienteVista vista;

    /**
     * Constructor con visibilidad de paquete.
     * Obtiene las instancias del servicio y la vista a través de sus respectivas fábricas.
     */
    ClienteControladorImpl() {
        this.servicio = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        this.vista = factoriaVista.obtenerInstancia().obtenerVistaCliente();
    }

    @Override
    public void crearCliente() {
        vista.mostrarMensaje("--- Creación de Nuevo Cliente ---");
        
        // 1. Solicitar datos a la vista
        Map<String, String> datosCliente = vista.solicitarDatosParaCrear();
        
        // 2. Enviar datos crudos al servicio (el servicio crea el DTO internamente)
        Optional<ClienteDTO> clienteCreado = servicio.crearClienteDto(datosCliente);
        
        // 3. Mostrar resultado
        clienteCreado.ifPresentOrElse(
            cliente -> vista.mostrarMensaje("Cliente creado exitosamente con ID: " + cliente.getId()),
            () -> vista.mostrarMensaje("Error al crear el cliente.")
        );
    }

    @Override
    public void buscarClientePorId() {
        // 1. Solicitar ID a la vista
        Optional<Long> idCliente = vista.solicitarIdCliente();
        
        // 2. Buscar en el servicio
        idCliente.ifPresent(id -> {
            Optional<ClienteDTO> cliente = servicio.obtenerClientePorId(Optional.of(id));
            
            // 3. Mostrar resultado
            vista.mostrarCliente(cliente);
        });
    }

    @Override
    public void listarTodosLosClientes() {
        // 1. Obtener lista del servicio
        Optional<List<ClienteDTO>> clientes = servicio.obtenerTodosLosClientes();
        
        // 2. Mostrar resultado
        clientes.ifPresentOrElse(
            listaClientes -> vista.mostrarTodosLosClientes(listaClientes),
            () -> vista.mostrarMensaje("Error al obtener la lista de clientes.")
        );
    }

    @Override
    public void actualizarCliente() {
        // 1. Solicitar ID
        Optional<Long> idCliente = vista.solicitarIdCliente();
        
        idCliente.ifPresent(id -> {
            // 2. Buscar cliente actual
            Optional<ClienteDTO> clienteActual = servicio.obtenerClientePorId(Optional.of(id));
            
            clienteActual.ifPresent(cliente -> {
                // 3. Solicitar datos de modificación
                Map<String, String> datosModificacion = vista.solicitarDatosParaActualizar(cliente);
                
                // 4. Mostrar y confirmar antes de actualizar
                if (vista.confirmarAccion(cliente, "Datos a Modificar", "¿Confirma la actualización?")) {
                    // 5. Actualizar cliente individualmente o en masa
                    procesarActualizacionCliente(cliente, datosModificacion);
                    vista.mostrarMensaje("Cliente actualizado exitosamente.");
                } else {
                    vista.mostrarMensaje("Actualización cancelada.");
                }
            });
        });
    }

    @Override
    public void eliminarCliente() {
        // 1. Solicitar ID
        Optional<Long> idCliente = vista.solicitarIdCliente();
        
        idCliente.ifPresent(id -> {
            // 2. Buscar cliente
            Optional<ClienteDTO> cliente = servicio.obtenerClientePorId(Optional.of(id));
            
            cliente.ifPresent(c -> {
                // 3. Confirmar eliminación y procesar
                Optional.of(c)
                    .filter(clienteAEliminar -> vista.confirmarAccion(clienteAEliminar, "Cliente a Eliminar", "¿Está seguro de que desea eliminar este cliente?"))
                    .ifPresentOrElse(
                        clienteConfirmado -> {
                            // 4. Eliminar
                            boolean eliminado = servicio.eliminarCliente(Optional.of(id));
                            
                            Optional.of(eliminado)
                                .filter(resultado -> resultado)
                                .ifPresentOrElse(
                                    resultado -> vista.mostrarMensaje("Cliente eliminado exitosamente."),
                                    () -> vista.mostrarMensaje("Error al eliminar el cliente.")
                                );
                        },
                        () -> vista.mostrarMensaje("Eliminación cancelada.")
                    );
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
                    case 1 -> crearCliente();
                    case 2 -> buscarClientePorId();
                    case 3 -> buscarClientePorDni();
                    case 4 -> listarTodosLosClientes();
                    case 5 -> actualizarCliente();
                    case 6 -> eliminarCliente();
                    case 7 -> restaurarCliente();
                    case 8 -> listarClientesEliminados();
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
     * Actualiza un cliente usando los métodos del servicio según la cantidad de campos a modificar.
     * Si se modifica 1 campo, usa métodos individuales; si se modifican múltiples campos, usa método múltiple.
     * 
     * @param clienteActual el cliente actual
     * @param datosModificacion los datos a modificar
     */
    private void procesarActualizacionCliente(ClienteDTO clienteActual, Map<String, String> datosModificacion) {
        Long id = clienteActual.getId();
        
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
                        case "email" -> servicio.actualizarEmailCliente(id, Optional.of(valor));
                        case "telefono" -> servicio.actualizarTelefonoCliente(id, Optional.of(valor));
                        case "direccion" -> servicio.actualizarDireccionCliente(id, Optional.of(valor));
                    }
                }),
                () -> {
                    // Para múltiples campos, crear un DTO temporal solo con los campos a modificar
                    servicio.actualizarVariosCampos(id, Optional.of(ClienteDTO.builder()
                        .email(datosModificacion.get("email"))
                        .telefono(datosModificacion.get("telefono"))
                        .direccion(datosModificacion.get("direccion"))
                        .build()));
                }
            );
    }

    @Override
    public void buscarClientePorDni() {
        // 1. Solicitar DNI a la vista
        Optional<String> dniCliente = vista.solicitarDni();
        
        // 2. Buscar en el servicio
        dniCliente.ifPresent(dni -> {
            Optional<ClienteDTO> cliente = servicio.obtenerClientePorDni(Optional.of(dni));
            
            // 3. Mostrar resultado
            vista.mostrarCliente(cliente);
        });
    }

    @Override
    public void restaurarCliente() {
        // 1. Solicitar ID
        Optional<Long> idCliente = vista.solicitarIdCliente();
        
        idCliente.ifPresent(id -> {
            // 2. Obtener lista de clientes eliminados
            List<ClienteDTO> clientesEliminados = servicio.obtenerClientesEliminados();
            
            // 3. Buscar el cliente en la lista de eliminados
            Optional<ClienteDTO> clienteAEliminado = clientesEliminados.stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst();
            
            // 4. Si lo encuentra, pedir confirmación y restaurarlo
            clienteAEliminado.ifPresentOrElse(
                cliente -> {
                    // 5. Pedir confirmación antes de restaurar
                    Optional.of(cliente)
                        .filter(clienteConfirmar -> vista.confirmarRestauracion(clienteConfirmar))
                        .ifPresentOrElse(
                            clienteConfirmado -> {
                                // 6. Restaurar el cliente
                                Optional<ClienteDTO> clienteRestaurado = servicio.restaurarCliente(Optional.of(id));
                                clienteRestaurado.ifPresentOrElse(
                                    restaurado -> {
                                        vista.mostrarMensaje("Cliente restaurado exitosamente.");
                                    },
                                    () -> vista.mostrarMensaje("Error al restaurar el cliente.")
                                );
                            },
                            () -> vista.mostrarMensaje("Restauración cancelada.")
                        );
                },
                () -> vista.mostrarMensaje("Cliente no encontrado en la lista de eliminados.")
            );
        });
    }

    @Override
    public void listarClientesEliminados() {
        // 1. Obtener lista de clientes eliminados del servicio
        List<ClienteDTO> clientesEliminados = servicio.obtenerClientesEliminados();
        
        // 2. Mostrar resultado
        vista.mostrarClientesEliminados(clientesEliminados);
    }
} 