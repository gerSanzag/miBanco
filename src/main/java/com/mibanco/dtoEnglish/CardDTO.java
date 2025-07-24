package com.mibanco.dtoEnglish;

import java.time.LocalDate;
import java.util.Optional;

import com.mibanco.modelEnglish.enums.CardType;
import com.mibanco.utilEnglish.ReflectionUtil.NoRequest;

import lombok.Builder;
import lombok.Value;

/**
 * DTO to transfer Card information between layers
 * We use immutable approach with @Value to maintain data integrity
 * CVV is omitted for security reasons in layer transfers
 */
@Value
@Builder(toBuilder = true) // We enable toBuilder to facilitate "with" methods
public class CardDTO {
    @NoRequest(reason = "Set automatically in repository") // Se establece automáticamente en el repositorio
    Long number; // numero
    ClientDTO holder; // titular
    String associatedAccountNumber; // numeroCuentaAsociada
    CardType type; // tipo
    LocalDate expirationDate; // fechaExpiracion
    boolean active; // activa

    /**
     * Static method that builds a CardDTO with optional values
     * Functional approach with Optional to handle null values
     */
    public static CardDTO of(Long number, Optional<ClientDTO> holder, Optional<String> associatedAccountNumber,
                               Optional<CardType> type, Optional<LocalDate> expirationDate,
                               Optional<Boolean> active) {
        LocalDate expDate = expirationDate.orElse(LocalDate.now().plusYears(3));
        return CardDTO.builder()
                .number(number)
                .holder(holder.orElse(null))
                .associatedAccountNumber(associatedAccountNumber.orElse(null))
                .type(type.orElse(null))
                .expirationDate(expDate)
                .active(active.orElse(true))
                .build();
    }
    
    /**
     * Creates a new instance with updated expiration date
     */
    public CardDTO withExpirationDate(LocalDate newDate) {
        return this.toBuilder()
                .expirationDate(newDate)
                .build();
    }
    
    /**
     * Creates a new instance with updated active state
     */
    public CardDTO withActive(boolean newActive) {
        return this.toBuilder()
                .active(newActive)
                .build();
    }
    
    /**
     * Creates a new instance with updated holder
     */
    public CardDTO withHolder(ClientDTO newHolder) {
        return this.toBuilder()
                .holder(newHolder)
                .build();
    }
    
    /**
     * Creates a new instance updating multiple fields at once
     */
    public CardDTO withUpdates(
            Optional<LocalDate> newDate,
            Optional<Boolean> newActive) {
        LocalDate finalDate = newDate.orElse(this.expirationDate);
        return this.toBuilder()
                .expirationDate(finalDate)
                .active(newActive.orElse(this.active))
                .build();
    }
} 