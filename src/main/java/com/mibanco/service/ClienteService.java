package com.mibanco.service;

import com.mibanco.dto.ClienteDTO;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones con Clientes
 */
public interface ClienteService {
    
    /**
     * Crea un nuevo cliente
     * @param clienteDTO Optional con los datos del cliente a crear
     * @return Optional con el DTO del cliente creado
     */
    Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO);
    
    /**
     * Obtiene un cliente por su ID
     * @param id Optional con el ID del cliente
     * @return Optional con el DTO del cliente
     */
    Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id);
    
    /**
     * Obtiene un cliente por su DNI
     * @param dni Optional con el DNI del cliente
     * @return Optional con el DTO del cliente
     */
    Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni);
    
    /**
     * Obtiene todos los clientes
     * @return Optional con la lista de DTOs de clientes
     */
    Optional<List<ClienteDTO>> obtenerTodosLosClientes();
    
    /**
     * Actualiza los datos de un cliente
     * @param id ID del cliente a actualizar
     * @param clienteDTO Optional con los nuevos datos del cliente
     * @return Optional con el DTO del cliente actualizado
     */
    Optional<ClienteDTO> actualizarCliente(Long id, Optional<ClienteDTO> clienteDTO);
    
    /**
     * Actualiza el email de un cliente
     * @param id ID del cliente
     * @param nuevoEmail Optional con el nuevo email
     * @return Optional con el DTO del cliente actualizado
     */
    Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail);
    
    /**
     * Actualiza el teléfono de un cliente
     * @param id ID del cliente
     * @param nuevoTelefono Optional con el nuevo teléfono
     * @return Optional con el DTO del cliente actualizado
     */
    Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono);
    
    /**
     * Actualiza la dirección de un cliente
     * @param id ID del cliente
     * @param nuevaDireccion Optional con la nueva dirección
     * @return Optional con el DTO del cliente actualizado
     */
    Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion);
    
    /**
     * Elimina un cliente por su ID
     * @param id Optional con el ID del cliente a eliminar
     * @return true si se eliminó correctamente, false si no
     */
    boolean eliminarCliente(Optional<Long> id);
} 