package com.mibanco.servicio.interna;

import com.mibanco.modelo.Cliente;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.repositorio.ClienteRepositorio;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.interna.RepositorioFactoria;         
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.util.ValidacionException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ClienteServicioImpl extends BaseServicioImpl<ClienteDTO, Cliente, Long, TipoOperacionCliente, ClienteRepositorio> implements ClienteServicio {
    
    private static final ClienteRepositorio repositorioCliente;
    private static final ClienteMapeador mapeador;
    private static final ClienteDtoProcesadorServicio clienteDtoProcesador;
    private final TipoOperacionCliente tipoActualizar = TipoOperacionCliente.ACTUALIZAR;
    
    static {
        repositorioCliente = RepositorioFactoria.obtenerInstancia().obtenerRepositorioCliente();
        mapeador = new ClienteMapeador();
        clienteDtoProcesador = new ClienteDtoProcesadorServicio();
    }
    
    public ClienteServicioImpl() {
        super(repositorioCliente, mapeador);
    }

    @Override
    public Optional<ClienteDTO> crearClienteDto(Map<String, String> datosCliente) {
        try {
            // Usar el procesador especializado para crear el DTO con validaciones
            return clienteDtoProcesador.procesarClienteDto(datosCliente)
                .flatMap(clienteDto -> {
                    // Validar DNI único antes de guardar
                    validarDniUnico(clienteDto);
                    return guardarEntidad(TipoOperacionCliente.CREAR, Optional.of(clienteDto));
                });
        } catch (ValidacionException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClienteDTO> guardarCliente(Optional<ClienteDTO> clienteDTO) {
        // NO validar aquí - el DTO ya debería estar validado
        return guardarEntidad(TipoOperacionCliente.CREAR, clienteDTO);
    }
    
    /**
     * Método auxiliar para validar DNI único
     * @param dto DTO del cliente a validar
     * @throws ValidacionException si el DNI ya existe
     */
    private void validarDniUnico(ClienteDTO dto) {
        // Solo validar si el DNI no es null (validación básica ya hecha en vista)
        if (dto.getDni() != null) {
            Optional<Cliente> clienteExistente = repositorioCliente.buscarPorPredicado(
                cliente -> dto.getDni().equals(cliente.getDni())
            );
            
            if (clienteExistente.isPresent()) {
                throw new ValidacionException("Ya existe un cliente con el DNI: " + dto.getDni());
            }
        }
    }

    @Override
    public Optional<ClienteDTO> actualizarVariosCampos(Long id, Optional<ClienteDTO> clienteDTO) {
        return clienteDTO.flatMap(nuevoCliente -> {
            // Obtener el cliente existente
            Optional<ClienteDTO> clienteExistenteOpt = obtenerPorId(Optional.of(id));
            
            return clienteExistenteOpt.map(clienteExistente -> {
                // Actualizar usando métodos del DTO
                ClienteDTO clienteActualizado = clienteExistente.conDatosContacto(
                    Optional.ofNullable(nuevoCliente.getEmail()),
                    Optional.ofNullable(nuevoCliente.getTelefono()),
                    Optional.ofNullable(nuevoCliente.getDireccion())
                );
                
                // Guardar y retornar
                return guardarEntidad(tipoActualizar, Optional.of(clienteActualizado)).orElse(clienteActualizado);
            });
        });
    }

    @Override
    public Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail) {
        return nuevoEmail.flatMap(email -> {
            Optional<ClienteDTO> clienteExistenteOpt = obtenerPorId(Optional.of(id));
            
            return clienteExistenteOpt.map(clienteExistente -> {
                ClienteDTO clienteActualizado = clienteExistente.conEmail(email);
                return guardarEntidad(tipoActualizar, Optional.of(clienteActualizado)).orElse(clienteActualizado);
            });
        });
    }

    @Override
    public Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono) {
        return nuevoTelefono.flatMap(telefono -> {
            Optional<ClienteDTO> clienteExistenteOpt = obtenerPorId(Optional.of(id));
            
            return clienteExistenteOpt.map(clienteExistente -> {
                ClienteDTO clienteActualizado = clienteExistente.conTelefono(telefono);
                return guardarEntidad(tipoActualizar, Optional.of(clienteActualizado)).orElse(clienteActualizado);
            });
        });
    }

    @Override
    public Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion) {
        return nuevaDireccion.flatMap(direccion -> {
            Optional<ClienteDTO> clienteExistenteOpt = obtenerPorId(Optional.of(id));
            
            return clienteExistenteOpt.map(clienteExistente -> {
                ClienteDTO clienteActualizado = clienteExistente.conDireccion(direccion);
                return guardarEntidad(tipoActualizar, Optional.of(clienteActualizado)).orElse(clienteActualizado);
            });
        });
    }

    @Override
    public Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id) {
        return obtenerPorId(id);
    }

    @Override
    public Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni) {
        return dni.flatMap(dniValue -> repositorioCliente.buscarPorId(Optional.of(Long.parseLong(dniValue)))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad))));
    }

    @Override
    public Optional<List<ClienteDTO>> obtenerTodosLosClientes() {
        return obtenerTodos();
    }

    @Override
    public boolean eliminarCliente(Optional<Long> id) {
        return eliminarPorId(id, TipoOperacionCliente.ELIMINAR);
    }

    @Override
    public Optional<ClienteDTO> restaurarCliente(Optional<Long> id) {
        return restaurar(id, TipoOperacionCliente.RESTAURAR);
    }

    @Override
    public void establecerUsuarioActual(String usuario) {
        establecerUsuarioActual(usuario);
    }

    @Override
    public long contarClientes() {
        return contarRegistros();
    }
} 