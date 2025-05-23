package com.mibanco.repository;

import com.mibanco.model.Cliente;
import java.util.Optional;

/**
 * Interfaz para operaciones específicas de Cliente
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface ClienteRepository extends BaseRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su DNI
     * @param dni Optional con el DNI del cliente a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByDni(Optional<String> dni);
    
    /**
     * Restaura un cliente previamente eliminado
     * @param id Optional con el ID del cliente a restaurar
     * @return Optional con el cliente restaurado o Optional vacío si no se encuentra
     */
    Optional<Cliente> restoreDeletedClient(Optional<Long> id);
} 