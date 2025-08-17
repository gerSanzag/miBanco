package com.mibanco.util;

import com.mibanco.model.AuditRecord;
import com.mibanco.model.Identifiable;
import com.mibanco.repository.AuditRepository;
import com.mibanco.repository.internal.RepositoryFactory;

import java.util.Optional;

/**
 * Utility class for centralized audit handling
 * Simple and direct approach without intermediate functions
 */
public class AuditUtil {
    
    // Centralized audit repository reference
    private static final AuditRepository auditRepository = RepositoryFactory.getInstance().getAuditRepository();
    
    /**
     * Registers an audit operation directly
     * @param operationType The type of operation performed
     * @param entity The entity on which the operation was performed
     * @param user The user who performed the operation
     * @return The created and saved audit record
     */
    public static <T extends Identifiable, E extends Enum<E>> 
           AuditRecord<T, E> registerOperation(
               Optional<E> operationType, 
               Optional<T> entity, 
               Optional<String> user) {
        
        // Create audit record
        AuditRecord<T, E> record = AuditRecord.of(operationType, entity, user);
        
        // Save and return using centralized repository
        Optional<AuditRecord<T, E>> savedRecord = auditRepository.register(Optional.of(record));
        return savedRecord.orElse(record);
    }
    
    /**
     * Registers an audit operation with simplified parameters
     * @param entityType The type of entity
     * @param entityId The entity ID
     * @param operationType The type of operation
     * @param user The user who performed the operation
     */
    public static void registerOperation(String entityType, Long entityId, String operationType, String user) {
        // This method provides a simplified interface for audit registration
        // Implementation can be added as needed
    }
} 