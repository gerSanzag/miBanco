package com.mibanco.repository;

import com.mibanco.model.Cliente;
import com.mibanco.model.enums.TipoOperacionCliente;
import com.mibanco.repository.util.BaseRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Clientes
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface ClienteRepository extends BaseRepository<Cliente, Long, TipoOperacionCliente> {
    
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
    
    ArrayList<Cliente> getRecentlyDeletedClients();
} 