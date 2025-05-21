package com.mibanco.service.impl;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapper.ClienteMapper;
import com.mibanco.model.Cliente;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.service.ClienteService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación de la interfaz de servicio para Clientes
 * Utilizando estrictamente un enfoque funcional con Optional y streams
 */
public class ClienteServiceImpl implements ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    
    /**
     * Constructor para inyección de dependencias
     * @param clienteRepository Repositorio de clientes
     * @param clienteMapper Mapper para conversión entre entidad y DTO
     */
    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }
    
    /**
     * Método privado que implementa el flujo común de actualización de clientes
     * @param id ID del cliente a actualizar
     * @param actualizarDatos Función que define cómo actualizar los datos del cliente
     * @return Optional con el DTO del cliente actualizado
     */
    private Optional<ClienteDTO> actualizarClienteGenerico(Long id, Function<Cliente, Cliente> actualizarDatos) {
        return Optional.ofNullable(id)
                .flatMap(clienteRepository::findById)
                .map(actualizarDatos)
                .flatMap(clienteRepository::save)
                .flatMap(clienteMapper::toDtoDirecto);
    }
    
    /**
     * Crea un nuevo cliente en el sistema
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO) {
        return clienteDTO
            .map(dto -> {
                // Aquí puedes incluir lógica más compleja o validaciones
                Optional<Cliente> entidad = clienteMapper.toEntity(Optional.of(dto));
                if (!entidad.isPresent()) {
                    return Optional.<ClienteDTO>empty(); 
                }
                
                Optional<Cliente> guardado = clienteRepository.save(entidad.get());
                return clienteMapper.toDto(guardado);
            })
            .orElse(Optional.empty());
    }
    
    /**
     * Obtiene un cliente por su ID
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id) {
        return id.map(idValue->{
            Optional<Cliente> cliente = clienteRepository.findById(idValue);
            return cliente.map(clienteMapper::toDtoDirecto).orElse(Optional.empty());
        })
        .orElse(Optional.empty());
    }
    
    /**
     * Obtiene un cliente por su DNI
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni) {
        return dni.map(dniValue -> {
            Optional<Cliente> cliente = clienteRepository.findByDni(dniValue);
            return cliente.map(clienteMapper::toDtoDirecto).orElse(Optional.empty());
        })
        .orElse(Optional.empty());
    }
    
    /**
     * Obtiene todos los clientes
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<ClienteDTO>> obtenerTodosLosClientes() {
        return clienteRepository.findAll()
                .flatMap(clienteMapper::toDtoList);
    }
    
    /**
     * Actualiza la información de un cliente existente
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<ClienteDTO> actualizarCliente(Long id, Optional<ClienteDTO> clienteDTO) {
        Function<Cliente, Cliente> actualizarDatos = clienteExistente -> 
            clienteDTO.flatMap(clienteMapper::toEntityDirecto)
                .map(clienteNuevo -> clienteExistente.withDatosContacto(
                    Optional.ofNullable(clienteNuevo.getEmail()),
                    Optional.ofNullable(clienteNuevo.getTelefono()),
                    Optional.ofNullable(clienteNuevo.getDireccion())
                ))
                .orElse(clienteExistente);
        
        return actualizarClienteGenerico(id, actualizarDatos);
    }
    
    /**
     * Actualiza el email de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail) {
        return actualizarClienteGenerico(
            id, 
            cliente -> cliente.withEmail(nuevoEmail.orElse(cliente.getEmail()))
        );
    }
    
    /**
     * Actualiza el teléfono de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono) {
        return actualizarClienteGenerico(
            id,
            cliente -> cliente.withTelefono(nuevoTelefono.orElse(cliente.getTelefono()))
        );
    }
    
    /**
     * Actualiza la dirección de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion) {
        return actualizarClienteGenerico(
            id,
            cliente -> cliente.withDireccion(nuevaDireccion.orElse(cliente.getDireccion()))
        );
    }
    
    /**
     * Elimina un cliente por su ID
     * Utilizando programación funcional sin if explícitos
     */
    @Override
    public boolean eliminarCliente(Optional<Long> id) {
        return id
                .flatMap(clienteRepository::deleteById)
                .isPresent();
    }
} 