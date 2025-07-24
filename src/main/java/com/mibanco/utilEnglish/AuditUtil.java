package com.mibanco.utilEnglish;

import com.mibanco.modelo.Identificable;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.repositorio.AuditoriaRepositorio;

import java.util.Optional;

/**
 * Utility class to handle auditing in a centralized way
 * Simple and direct approach without intermediate functions
 */
public class AuditUtil {
    
    /**
     * Records an audit operation directly
     * @param auditRepository Repository where to save the record
     * @param operationType The type of operation performed
     * @param entity The entity on which the operation was performed
     * @param user The user who performed the operation
     * @return The created and saved audit record
     */
    public static <T extends Identificable, E extends Enum<E>> 
           RegistroAuditoria<T, E> recordOperation(
               AuditoriaRepositorio auditRepository,
               Optional<E> operationType, 
               Optional<T> entity, 
               Optional<String> user) {
        
        // Create audit record
        RegistroAuditoria<T, E> record = RegistroAuditoria.of(operationType, entity, user);
        
        // Save and return
        Optional<RegistroAuditoria<T, E>> savedRecord = auditRepository.registrar(Optional.of(record));
        return savedRecord.orElse(record);
    }
} 