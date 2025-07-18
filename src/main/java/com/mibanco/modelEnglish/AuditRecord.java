package com.mibanco.modelEnglish;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


/**
 * Immutable audit record for any entity
 * Uses two generic types:
 * - T for the entity (must implement Identifiable)
 * - E for the operation type (must be an enum)
 */
@Value
@Builder
public class AuditRecord<T extends Identifiable, E extends Enum<E>> {
    // Unique ID of the record
    UUID id;
    
    // Operation type (any enum)
    E operationType;         // tipoOperacion
    
    // Exact date and time of the event
    LocalDateTime dateTime;  // fechaHora
    
    // The involved entity (must implement Identifiable)
    T entity;               // entidad
    
    // User who performed the operation
    String user;            // usuario
    
    // Amount of the operation (useful for financial operations)
    Double amount;          // monto
    
    // Additional information about the operation
    String details;         // detalles
    
    @JsonCreator
    public AuditRecord(
        @JsonProperty("id") UUID id,
        @JsonProperty("operationType") E operationType,
        @JsonProperty("dateTime") LocalDateTime dateTime,
        @JsonProperty("entity") T entity,
        @JsonProperty("user") String user,
        @JsonProperty("amount") Double amount,
        @JsonProperty("details") String details
    ) {
        this.id = id;
        this.operationType = operationType;
        this.dateTime = dateTime;
        this.entity = entity;
        this.user = user;
        this.amount = amount;
        this.details = details;
    }
    
    /**
     * Factory method to create basic records
     * @param operationType Operation type (enum)
     * @param entity Affected entity
     * @param user User who performed the operation
     * @return Immutable audit record
     */
    public static <T extends Identifiable, E extends Enum<E>> AuditRecord<T, E> of(
            Optional<E> operationType, 
            Optional<T> entity, 
            Optional<String> user) {
        
        return AuditRecord.<T, E>builder()
                .id(UUID.randomUUID())
                .operationType(operationType.orElse(null))
                .dateTime(LocalDateTime.now())
                .entity(entity.orElse(null))
                .user(user.orElse(null))
                .build();
    }
    
    /**
     * Factory method to create records with financial information
     * @param operationType Operation type (enum)
     * @param entity Affected entity
     * @param user User who performed the operation
     * @param amount Amount of the financial operation
     * @param details Additional details
     * @return Immutable audit record
     */
    public static <T extends Identifiable, E extends Enum<E>> AuditRecord<T, E> ofDetailed(
            Optional<E> operationType, 
            Optional<T> entity, 
            Optional<String> user,
            Optional<Double> amount,
            Optional<String> details) {
        
        return AuditRecord.<T, E>builder()
                .id(UUID.randomUUID())
                .operationType(operationType.orElse(null))       
                .dateTime(LocalDateTime.now())
                .entity(entity.orElse(null))
                .user(user.orElse(null))
                .amount(amount.orElse(null))
                .details(details.orElse(null))
                .build();
    }
} 