package com.mibanco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mibanco.model.enums.TransactionType;
import com.mibanco.util.ReflectionUtil.NoRequest;

/**
 * DTO to transfer Transaction information between layers
 * We use immutable approach with @Value to promote functional programming
 */
@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class TransactionDTO {
    @NoRequest(reason = "Set automatically in repository") // Se establece automáticamente en el repositorio
    Long id; // id
    String accountNumber; // numeroCuenta
    String destinationAccountNumber; // numeroCuentaDestino
    TransactionType type; // tipo
    BigDecimal amount; // monto
    LocalDateTime date; // fecha
    String description; // descripcion

    /**
     * Static method that builds a TransactionDTO with optional values
     * Functional approach with Optional to handle null values
     */
    public static TransactionDTO of(Long id, String accountNumber, 
                                   Optional<String> destinationAccountNumber,
                                   Optional<TransactionType> type, Optional<BigDecimal> amount,
                                   Optional<LocalDateTime> date,
                                   Optional<String> description) {
        return TransactionDTO.builder()
                .id(id)
                .accountNumber(accountNumber)
                .destinationAccountNumber(destinationAccountNumber.orElse(null))
                .type(type.orElse(null))
                .amount(amount.orElse(null))
                .date(date.orElse(LocalDateTime.now()))
                .description(description.orElse(""))
                .build();
    }
} 