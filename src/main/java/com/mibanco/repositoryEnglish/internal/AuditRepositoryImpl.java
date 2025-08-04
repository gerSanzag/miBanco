package com.mibanco.repositoryEnglish.internal;

import com.mibanco.modelEnglish.Identifiable;
import com.mibanco.modelEnglish.AuditRecord;
import com.mibanco.repositoryEnglish.AuditRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the audit repository
 * Visibility restricted to internal package
 */
class AuditRepositoryImpl implements AuditRepository {
    
    // List to store records
    private final List<AuditRecord<?, ?>> records = new ArrayList<>(); // registros
    
    /**
     * Constructor with package visibility
     */
    AuditRepositoryImpl() {
        // Empty constructor with package visibility
    }
    
    @Override
    public <T extends Identifiable, E extends Enum<E>> Optional<AuditRecord<T, E>> register(
            Optional<AuditRecord<T, E>> recordOpt) {
        return recordOpt.map(record -> {
            records.add(record);
            return record;
        });
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identifiable, E extends Enum<E>> Optional<AuditRecord<T, E>> findById(Optional<UUID> idOpt) {
        return idOpt.flatMap(id -> 
            records.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(r -> (AuditRecord<T, E>) r)
        );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> getHistory(
            Optional<Class<T>> entityType, 
            Optional<Long> entityId) {
        return entityType.flatMap(type -> 
            entityId.map(id -> 
                records.stream()
                    .filter(r -> type.isInstance(r.getEntity()) && 
                               r.getEntity().getId().equals(id))
                    .map(r -> (AuditRecord<T, E>) r)
                    .collect(Collectors.toList())
            ).map(Optional::of)
        ).orElse(Optional.of(new ArrayList<>()));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByDates(
            Optional<LocalDateTime> fromOpt, 
            Optional<LocalDateTime> toOpt) {
        
        return fromOpt.flatMap(from -> 
            toOpt.map(to -> 
                records.stream()
                    .filter(r -> r.getDateTime() != null && !r.getDateTime().isBefore(from) && !r.getDateTime().isAfter(to))
                    .map(r -> (AuditRecord<T, E>) r)
                    .collect(Collectors.toList())
            )
        ).map(Optional::of).orElse(Optional.of(new ArrayList<>()));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByUser(
            Optional<String> user) {
        return user.map(u -> 
            records.stream()
                .filter(r -> r.getUser().equals(u))
                .map(r -> (AuditRecord<T, E>) r)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identifiable, E extends Enum<E>> Optional<List<AuditRecord<T, E>>> findByOperationType(
            Optional<E> operationType,
            Optional<Class<E>> enumType) {
        return operationType.flatMap(type ->
            enumType.map(enumClass ->
                records.stream()
                    .filter(r -> r.getOperationType().equals(type))
                    .map(r -> (AuditRecord<T, E>) r)
                    .collect(Collectors.toList())
            )
        );
    }
} 