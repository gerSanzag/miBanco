package com.mibanco.repository;

import com.mibanco.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones CRUD de Cliente
 * Siguiendo enfoque estrictamente funcional con Optional
 * tanto para parámetros como para resultados
 */
public interface ClienteRepository {
    
    /**
     * Guarda un cliente en el repositorio
     * @param cliente Optional con el cliente a guardar
     * @return Optional con el cliente guardado (posiblemente con ID generado)
     * o Optional vacío si el cliente era null
     */
    Optional<Cliente> save(Optional<Cliente> cliente);
    
    /**
     * Busca un cliente por su ID
     * @param id Optional con el ID del cliente a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findById(Optional<Long> id);
    
    /**
     * Busca un cliente por su DNI
     * @param dni Optional con el DNI del cliente a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByDni(Optional<String> dni);
    
    /**
     * Obtiene todos los clientes
     * @return Lista de clientes
     */
    Optional<List<Cliente>> findAll();
    
    /**
     * Elimina un cliente por su ID
     * @param id Optional con el ID del cliente a eliminar
     * @return true si se eliminó el cliente, false si no existía
     */
    boolean deleteById(Optional<Long> id);
    
    /**
     * Obtiene el número de clientes en el repositorio
     * @return Número de clientes
     */
    long count();
} 