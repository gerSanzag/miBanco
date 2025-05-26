package com.mibanco.service.impl;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapper.ClienteMapper;
import com.mibanco.model.enums.TipoOperacionCliente;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.service.ClienteService;
import com.mibanco.repository.internal.RepositoryFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz de servicio para Clientes
 * Utilizando estrictamente un enfoque funcional con Optional y streams
 */
public class ClienteServiceImpl implements ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final RepositoryFactory repositoryFactory;
    
    /**
     * Constructor para inyección de dependencias
     * @param clienteMapper Mapper para conversión entre entidad y DTO
     * @param repositoryFactory Factory para obtener instancias de repositorios
     */
    public ClienteServiceImpl(ClienteMapper clienteMapper, RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        this.clienteRepository = repositoryFactory.obtenerRepositorioCliente();
        this.clienteMapper = clienteMapper;
    }

    /**
     * Método privado para guardar un cliente (crear o actualizar)
     */
    private Optional<ClienteDTO> guardarCliente(Optional<ClienteDTO> clienteDTO) {
        return clienteDTO
            .flatMap(dto -> clienteMapper.aEntidad(Optional.of(dto)))
            .flatMap(entidad -> {
                if (entidad.getId() == null) {
                    return clienteRepository.crear(Optional.of(entidad), TipoOperacionCliente.CREAR);
                } else {
                    return clienteRepository.actualizar(Optional.of(entidad), TipoOperacionCliente.MODIFICAR);
                }
            })
            .flatMap(clienteMapper::aDtoDirecto);
    }

    @Override
    public Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO) {
        return clienteDTO
            .map(dto -> dto.toBuilder().id(null).build())
            .map(Optional::of)
            .flatMap(this::guardarCliente);
    }
    
    
    /**
     * Obtiene un cliente por su ID
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id) {
        return id.flatMap(idValue -> 
            clienteRepository.buscarPorId(Optional.of(idValue))
                .flatMap(clienteMapper::aDtoDirecto)
        );
    }
    
    /**
     * Obtiene un cliente por su DNI
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni) {
        return dni.flatMap(dniValue -> 
            clienteRepository.buscarPorDni(Optional.of(dniValue))
                .flatMap(clienteMapper::aDtoDirecto)
        );
    }
    
    /**
     * Obtiene todos los clientes
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<ClienteDTO>> obtenerTodosLosClientes() {
        return clienteRepository.buscarTodos()
                .flatMap(clienteMapper::aListaDto);
    }
    
    /**
     * Actualiza la información de un cliente existente
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<ClienteDTO> actualizarCliente(Long id, Optional<ClienteDTO> clienteDTO) {
        return Optional.ofNullable(id)
            .flatMap(idValue -> clienteRepository.buscarPorId(Optional.of(idValue)))
            .map(clienteExistente -> 
                clienteDTO.flatMap(clienteMapper::aEntidadDirecta)
                    .map(clienteNuevo -> clienteExistente.conDatosContacto(
                        Optional.ofNullable(clienteNuevo.getEmail()),
                        Optional.ofNullable(clienteNuevo.getTelefono()),
                        Optional.ofNullable(clienteNuevo.getDireccion())
                    ))
                    .orElse(clienteExistente)
            )
            .flatMap(clienteMapper::aDtoDirecto)
            .flatMap(dto -> guardarCliente(Optional.of(dto)));
    }
    
    /**
     * Actualiza el email de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail) {
        return Optional.ofNullable(id)
            .flatMap(idValue -> clienteRepository.buscarPorId(Optional.of(idValue)))
            .map(cliente -> cliente.conEmail(nuevoEmail.orElse(cliente.getEmail())))
            .flatMap(clienteMapper::aDtoDirecto)
            .flatMap(dto -> guardarCliente(Optional.of(dto)));
    }
    
    /**
     * Actualiza el teléfono de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono) {
        return Optional.ofNullable(id)
            .flatMap(idValue -> clienteRepository.buscarPorId(Optional.of(idValue)))
            .map(cliente -> cliente.conTelefono(nuevoTelefono.orElse(cliente.getTelefono())))
            .flatMap(clienteMapper::aDtoDirecto)
            .flatMap(dto -> guardarCliente(Optional.of(dto)));
    }
    
    /**
     * Actualiza la dirección de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion) {
        return Optional.ofNullable(id)
            .flatMap(idValue -> clienteRepository.buscarPorId(Optional.of(idValue)))
            .map(cliente -> cliente.conDireccion(nuevaDireccion.orElse(cliente.getDireccion())))
            .flatMap(clienteMapper::aDtoDirecto)
            .flatMap(dto -> guardarCliente(Optional.of(dto)));
    }
    
    /**
     * Elimina un cliente por su ID
     * Utilizando programación funcional sin if explícitos
     */
    @Override
    public boolean eliminarCliente(Optional<Long> id) {
        return id
                .flatMap(idValue -> clienteRepository.eliminarPorId(Optional.of(idValue), TipoOperacionCliente.ELIMINAR))
                .isPresent();
    }

    @Override
    public Optional<ClienteDTO> restaurarCliente(Optional<Long> id) {
        return id
            .flatMap(idValue -> clienteRepository.restaurarClienteEliminado(Optional.of(idValue)))
            .flatMap(clienteMapper::aDtoDirecto);
    }

    @Override
    public List<ClienteDTO> obtenerClientesEliminados() {
        return clienteMapper.aListaDto(clienteRepository.obtenerClientesEliminados())
            .orElse(List.of());
    }

    @Override
    public long contarClientes() {
        return clienteRepository.buscarTodos()
            .map(List::size)
            .orElse(0);
    }

    @Override
    public void establecerUsuarioActual(String usuario) {
        clienteRepository.setUsuarioActual(usuario);
    }
} 