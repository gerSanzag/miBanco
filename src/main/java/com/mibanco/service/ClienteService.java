package com.mibanco.service;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.model.Cliente;
import com.mibanco.model.enums.TipoOperacionCliente;
import com.mibanco.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para operaciones relacionadas con Clientes
 * Sigue un enfoque estrictamente funcional con uso de Optional
 */
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Guarda un cliente, decidiendo si crear uno nuevo o actualizar uno existente
     */
    public Optional<Cliente> guardar(Optional<Cliente> clienteOpt) {
        return clienteOpt.flatMap(cliente -> 
            Optional.ofNullable(cliente.getId()).isPresent()
                ? clienteRepository.actualizar(Optional.of(cliente), TipoOperacionCliente.MODIFICAR)
                : clienteRepository.crear(Optional.of(cliente), TipoOperacionCliente.CREAR)
        );
    }
    
    /**
     * Elimina un cliente por su ID
     */
    public Optional<Cliente> eliminar(Optional<Long> idOpt) {
        return clienteRepository.deleteById(idOpt, TipoOperacionCliente.ELIMINAR);
    }
    
    /**
     * Restaura un cliente eliminado
     */
    public Optional<Cliente> restaurar(Optional<Long> idOpt) {
        return clienteRepository.restaurar(idOpt, TipoOperacionCliente.RESTAURAR);
    }
    
    // Aquí irían los otros métodos del servicio que trabajan con DTOs...
} 