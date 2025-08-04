package com.mibanco.serviceEnglish.internal;

import com.mibanco.modelEnglish.Identifiable;
import com.mibanco.modelEnglish.AuditRecord;
import com.mibanco.repositoryEnglish.AuditRepository;
import com.mibanco.repositoryEnglish.internal.RepositoryFactory;
import com.mibanco.serviceEnglish.AuditService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the Audit query service
 * Audit logging is handled through AuditUtil
 */
class AuditServiceImpl implements AuditService {
    
    private static final AuditRepository auditRepository;
    
    static {
        auditRepository = RepositoryFactory.getInstance().getAuditRepository();
    }
    
    /**
     * Constructor with package visibility
     */
    AuditServiceImpl() {
        // Empty constructor with package visibility
    }
    
    @Override
    public <T extends Identifiable, E extends Enum<E>> Optional<AuditRecord<T, E>> findById(
            Optional<UUID> id) {
        return auditRepository.findById(id);
    }
    
    @Override
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> getHistory(
            Optional<Class<T>> entityType, 
            Optional<Long> entityId) {
        return auditRepository.getHistory(entityType, entityId);
    }
    
    @Override
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByDates(
            Optional<LocalDateTime> from, 
            Optional<LocalDateTime> to) {
        return auditRepository.findByDates(from, to);
    }
    
    @Override
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByUser(
            Optional<String> user) {
        return auditRepository.findByUser(user);
    }
    
    @Override
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByOperationType(
            Optional<E> operationType, 
            Optional<Class<E>> enumType) {
        return auditRepository.findByOperationType(operationType, enumType);
    }
} 