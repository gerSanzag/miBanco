package com.mibanco.repository;

import com.mibanco.model.Identificable;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz base para operaciones CRUD genéricas
 * @param <T> Tipo de entidad
 * @param <ID> Tipo del identificador
 * @param <E> Tipo del enum para operaciones
 */
public interface BaseRepository<T extends Identificable, ID, E extends Enum<E>> {
    
    /**
     * Crea una nueva entidad
     * @param entity Optional con la entidad a crear
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad creada
     */
    Optional<T> crear(Optional<T> entity, E tipoOperacion);
    
    /**
     * Actualiza una entidad existente
     * @param entity Optional con la entidad a actualizar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad actualizada
     */
    Optional<T> actualizar(Optional<T> entity, E tipoOperacion);
    
    /**
     * Busca una entidad por su ID
     * @param id Optional con el ID de la entidad
     * @return Optional con la entidad si existe
     */
    Optional<T> findById(Optional<ID> id);
    
    /**
     * Obtiene todas las entidades
     * @return Optional con la lista de entidades
     */
    Optional<List<T>> findAll();
    
    /**
     * Elimina una entidad por su ID
     * @param id Optional con el ID de la entidad a eliminar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad eliminada
     */
    Optional<T> deleteById(Optional<ID> id, E tipoOperacion);
    
    /**
     * Restaura una entidad eliminada
     * @param id Optional con el ID de la entidad a restaurar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad restaurada
     */
    Optional<T> restaurar(Optional<ID> id, E tipoOperacion);
    
    /**
     * Obtiene el número de entidades en el repositorio
     * @return Número de entidades
     */
    long count();
} 