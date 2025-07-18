package com.mibanco.modelEnglish;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mibanco.modelEnglish.enums.TransactionType;

/**
 * Class that represents a bank transaction
 * A transaction is an event that should not be modified once created
 * so we implement a completely immutable approach
 */
@Value
@Builder
public class Transaction implements Identifiable {
    Long id;
    String accountNumber;        // numeroCuenta
    String destinationAccountNumber;  // numeroCuentaDestino
    TransactionType type;        // tipo
    BigDecimal amount;           // monto
    LocalDateTime date;          // fecha
    String description;          // descripcion
    
    @JsonCreator
    public Transaction(
        @JsonProperty("id") Long id,
        @JsonProperty("accountNumber") String accountNumber,
        @JsonProperty("destinationAccountNumber") String destinationAccountNumber,
        @JsonProperty("type") TransactionType type,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("date") LocalDateTime date,
        @JsonProperty("description") String description
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
    
    /**
     * Factory method to create transactions
     */
    public static Transaction of(Long id, String accountNumber, String destinationAccountNumber,
                               TransactionType type, BigDecimal amount, 
                               LocalDateTime date, String description) {
        return Transaction.builder()
                .id(id)
                .accountNumber(accountNumber)
                .destinationAccountNumber(destinationAccountNumber)
                .type(type)
                .amount(amount)
                .date(date != null ? date : LocalDateTime.now())
                .description(description != null ? description : "")
                .build();
    }
} 