package com.mibanco.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mibanco.model.AuditRecord;
import com.mibanco.model.Identifiable;

/**
 * Service interface for Audit query operations
 * Audit recording is handled through AuditUtil
 */
public interface AuditService {
    
    /**
     * Searches for a record by its unique ID
     * @param id Optional with the unique ID of the record
     * @return Optional with the record if it exists
     */
    <T extends Identifiable, E extends Enum<E>> Optional<AuditRecord<T, E>> findById(Optional<UUID> id);
    
    /**
     * Gets the history of an entity by its type and ID
     * @param entityType Optional with the entity class
     * @param entityId Optional with the entity ID
     * @return Optional with list of audit records related to that entity
     */
    <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> getHistory(
            Optional<Class<T>> entityType, 
            Optional<Long> entityId);
    
    /**
     * Gets records in a date range
     * @param from Optional with the start date
     * @param to Optional with the end date
     * @return Optional with list of records in that date range
     */
    <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByDates(
            Optional<LocalDateTime> from, 
            Optional<LocalDateTime> to);
    
    /**
     * Gets records created by a specific user
     * @param user Optional with the user who performed the operations
     * @return Optional with list of user records
     */
    <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByUser(Optional<String> user);
    
    /**
     * Searches records by operation type
     * @param operationType Optional with the operation type
     * @param enumType Optional with the enum class
     * @return Optional with list of records that correspond to that operation
     */
    <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByOperationType(
            Optional<E> operationType, 
            Optional<Class<E>> enumType);
} 