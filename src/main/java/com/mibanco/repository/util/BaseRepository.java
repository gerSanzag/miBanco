
package com.mibanco.repository.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mibanco.model.Identifiable;

/**
 * Base interface for repositories
 * Defines generic CRUD operations
 * Access to implementations is restricted by package structure
 * @param <T> Entity type that must implement Identifiable
 * @param <ID> Identifier type
 * @param <E> Enum type for operations
 */
public interface BaseRepository<T extends Identifiable, ID, E extends Enum<E>> {
    
    /**
     * Creates a new entity
     * @param entity Entity to create
     * @param operationType Operation type for audit
     * @return Optional with the created entity
     */
    Optional<T> createRecord(Optional<T> entity, E operationType);
    
    /**
     * Updates an existing entity
     * @param entity Entity to update
     * @param operationType Operation type for audit
     * @return Optional with the updated entity
     */
    Optional<T> updateRecord(Optional<T> entity, E operationType);
    
    /**
     * Searches for an entity by its ID
     * @param id Entity ID
     * @return Optional with the entity if it exists
     */
    Optional<T> findById(Optional<ID> id);
   

    /**
     * Searches for an entity by a predicate
     * @param predicate Predicate to search for the entity
     * @return Optional with the entity if it exists
     */
    Optional<T> findByPredicate(Predicate<T> predicate);
   
    /**
     * Searches for all entities by a predicate
     * @param predicate Predicate to search for entities
     * @return Optional with the list of entities if they exist
     */
    Optional<List<T>> findAllByPredicate(Predicate<T> predicate);
    
    /**
     * Gets all entities
     * @return Optional with the list of entities
     */
    Optional<List<T>> findAll();
    
    /**
     * Deletes an entity
     * @param id ID of the entity to delete
     * @param operationType Operation type for audit
     * @return Optional with the deleted entity
     */
    Optional<T> deleteById(Optional<ID> id, E operationType);
    
    /**
     * Restores a deleted entity
     * @param id ID of the entity to restore
     * @param operationType Operation type for audit
     * @return Optional with the restored entity
     */
    Optional<T> restore(Optional<ID> id, E operationType);

   
    
    /**
     * Counts the number of records
     * @return Number of records
     */
    long countRecords();
    
    /**
     * Sets the current user for audit operations
     * @param user Current user
     */
    void setCurrentUser(String user);

    /**
     * Gets deleted entities
     * @return List of deleted entities
     */
    List<T> getDeleted();
    
    /**
     * Public method for forced data saving
     * Useful for application shutdown or manual saving
     */
    void saveData();
} 