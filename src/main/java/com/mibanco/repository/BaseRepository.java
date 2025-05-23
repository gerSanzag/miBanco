package com.mibanco.repository;

import com.mibanco.model.Identificable;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz base para operaciones CRUD genéricas
 * Siguiendo enfoque estrictamente funcional con Optional
 * @param <T> Tipo de entidad
 * @param <ID> Tipo del identificador
 */
public interface BaseRepository<T extends Identificable, ID> {
    
    /**
     * Guarda una entidad en el repositorio
     * @param entity Optional con la entidad a guardar
     * @return Optional con la entidad guardada
     */
    Optional<T> save(Optional<T> entity);
    
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
     * @return Optional con la entidad eliminada o Optional vacío si no existía
     */
    Optional<T> deleteById(Optional<ID> id);
    
    /**
     * Obtiene el número de entidades en el repositorio
     * @return Número de entidades
     */
    long count();
} 