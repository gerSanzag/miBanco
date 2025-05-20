package com.mibanco.service;

import com.mibanco.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones relacionadas con Clientes
 * Sigue un enfoque estrictamente funcional con uso de Optional
 */
public interface ClienteService {
    
    /**
     * Crea un nuevo cliente en el sistema
     * @param clienteDTO Optional con el DTO del cliente a crear
     * @return Optional con el DTO del cliente creado (con ID generado)
     * o Optional vacío si no se pudo crear
     */
    Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO);
    
    /**
     * Obtiene un cliente por su ID
     * @param id Optional con el ID del cliente a buscar
     * @return Optional con el DTO del cliente si existe
     */
    Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id);
    
    /**
     * Obtiene un cliente por su DNI
     * @param dni Optional con el DNI del cliente a buscar
     * @return Optional con el DTO del cliente si existe
     */
    Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni);
    
    /**
     * Obtiene todos los clientes
     * @return Optional con la lista de DTOs de clientes
     */
    Optional<List<ClienteDTO>> obtenerTodosLosClientes();
    
    /**
     * Actualiza la información de un cliente existente
     * @param id ID del cliente a actualizar
     * @param clienteDTO Optional con el DTO del cliente con los datos actualizados
     * @return Optional con el DTO del cliente actualizado
     * o Optional vacío si no se encontró el cliente
     */
    Optional<ClienteDTO> actualizarCliente(Long id, Optional<ClienteDTO> clienteDTO);
    
    /**
     * Actualiza el email de un cliente
     * @param id ID del cliente
     * @param nuevoEmail Optional con el nuevo email
     * @return Optional con el cliente actualizado o vacío si no se encontró
     */
    Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail);
    
    /**
     * Actualiza el teléfono de un cliente
     * @param id ID del cliente
     * @param nuevoTelefono Optional con el nuevo teléfono
     * @return Optional con el cliente actualizado o vacío si no se encontró
     */
    Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono);
    
    /**
     * Actualiza la dirección de un cliente
     * @param id ID del cliente
     * @param nuevaDireccion Optional con la nueva dirección
     * @return Optional con el cliente actualizado o vacío si no se encontró
     */
    Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion);
    
    /**
     * Elimina un cliente por su ID
     * @param id Optional con el ID del cliente a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    boolean eliminarCliente(Optional<Long> id);
} 