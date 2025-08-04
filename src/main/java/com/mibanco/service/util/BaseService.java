package com.mibanco.service.util;

import java.util.Optional;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mibanco.model.Identifiable;

/**
 * Base interface for services
 * @param <T> DTO type
 * @param <E> Entity type that must implement Identifiable
 * @param <ID> Identifier type
 * @param <O> Enum type for operations
 */
public interface BaseService<T, E extends Identifiable, ID, O extends Enum<O>> {
    
    /**
     * Common method to save an entity (create or update)
     * @param operationType Type of operation to perform
     * @param dto Optional with data to save
     * @return Optional with the saved DTO
     */
    Optional<T> saveEntity(O operationType, Optional<T> dto);

    /**
     * Generic method to update a specific field of an entity
     * @param id ID of the entity to update
     * @param newValue Optional with the new value
     * @param currentValue Function to get the current value
     * @param updater Function to update the value
     * @return Optional with the updated DTO
     */
    <V> Optional<T> updateField(
            ID id,
            Optional<V> newValue,
            Function<T, V> currentValue,
            BiFunction<T, V, T> updater);

    /**
     * Generic method to update an existing entity
     * @param id ID of the entity to update
     * @param dto Optional with new data
     * @param operationType Type of operation for auditing
     * @param updater Function that updates the existing entity with new data
     * @return Optional with the updated DTO
     */
    Optional<T> update(
            ID id,
            Optional<T> dto,
            O operationType,
            BiFunction<T, E, T> updater);

    /**
     * Generic method to get an entity by its ID
     * @param id Optional with the ID of the entity to search
     * @return Optional with the DTO of the found entity
     */
    Optional<T> findById(Optional<ID> id);

    /**
     * Generic method to get all entities
     * @return Optional with the list of found DTOs
     */
    Optional<List<T>> findAll();

    /**
     * Generic method to delete an entity by its ID
     * @param id Optional with the ID of the entity to delete
     * @param operationType Type of operation for auditing
     * @return true if the entity was deleted, false otherwise
     */
    boolean deleteById(Optional<ID> id, O operationType);

    /**
     * Generic method to count the total number of records
     * @return total number of records
     */
    long countRecords();

    /**
     * Generic method to set the current user in the repository
     * @param user name of the current user
     */
    void setCurrentUser(String user);

    /**
     * Generic method to get deleted entities
     * @return List of DTOs of deleted entities
     */
    List<T> getDeleted();

    /**
     * Generic method to restore a previously deleted entity
     * @param id Optional with the ID of the entity to restore
     * @param operationType Type of operation for auditing
     * @return Optional with the DTO of the restored entity
     */
    Optional<T> restore(Optional<ID> id, O operationType);
} 