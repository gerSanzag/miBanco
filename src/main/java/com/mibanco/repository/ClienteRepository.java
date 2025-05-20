package com.mibanco.repository;

import com.mibanco.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones CRUD de Cliente
 * Siguiendo un enfoque funcional pero con parámetros directos
 */
public interface ClienteRepository {
    
    /**
     * Guarda un cliente en el repositorio
     * @param cliente Cliente a guardar
     * @return Optional con el cliente guardado (con ID generado)
     */
    Optional<Cliente> save(Cliente cliente);
    
    /**
     * Busca un cliente por su ID
     * @param id ID del cliente a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findById(Long id);
    
    /**
     * Busca un cliente por su DNI
     * @param dni DNI del cliente a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByDni(String dni);
    
    /**
     * Obtiene todos los clientes
     * @return Optional con la lista de clientes
     */
    Optional<List<Cliente>> findAll();
    
    /**
     * Elimina un cliente por su ID
     * @param id ID del cliente a eliminar
     * @return Optional con el cliente eliminado o Optional vacío si no existía
     */
    Optional<Cliente> deleteById(Long id);
    
    /**
     * Obtiene el número de clientes en el repositorio
     * @return Número de clientes
     */
    long count();
} 