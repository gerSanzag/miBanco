package com.mibanco.dtoEnglish;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mibanco.modelEnglish.enums.AccountType;
import com.mibanco.utilEnglish.ReflectionUtil.NoRequest;

import lombok.Builder;
import lombok.Value;

/**
 * DTO to transfer Account information between layers
 * We use immutable and functional approach with @Value
 */
@Value
@Builder(toBuilder = true) // We enable toBuilder to facilitate "with" methods
public class AccountDTO {
    @NoRequest(reason = "Set automatically in repository") // Se establece automáticamente en el repositorio
    String accountNumber;        // numeroCuenta
    ClientDTO holder;            // titular
    AccountType type;            // tipo
    @NoRequest(reason = "Set automatically when creating") // Se establece automáticamente al crear
    LocalDateTime creationDate;  // fechaCreacion
    @NoRequest(reason = "Set automatically when creating and never changes") // Se establece automáticamente al crear y nunca cambia
    BigDecimal initialBalance;   // saldoInicial
    @NoRequest(reason = "Initialized equal to initialBalance and only changes through transactions") // Se inicializa igual a saldoInicial y solo cambia por transacciones
    BigDecimal balance;          // saldo
    @NoRequest(reason = "Set by default as active") // Se establece por defecto como activa
    boolean active;              // activa

    /**
     * Static method that builds an AccountDTO with optional values
     * Example of functional approach and Optionals usage
     */
        public static AccountDTO of(String accountNumber, Optional<ClientDTO> holder, Optional<AccountType> type,
                               BigDecimal initialBalance, Optional<LocalDateTime> creationDate, 
                               Optional<Boolean> active) {
        return AccountDTO.builder()
                .accountNumber(accountNumber)
                .holder(holder.orElse(null))
                .type(type.orElse(null))
                .initialBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO)
                .balance(initialBalance != null ? initialBalance : BigDecimal.ZERO)
                .creationDate(creationDate.orElse(LocalDateTime.now()))
                .active(active.orElse(true))
                .build();
    }
    
    /**
     * Creates a new instance with updated balance
     * "With" method for immutable transformation
     */
    public AccountDTO withBalance(BigDecimal newBalance) {
        return this.toBuilder()
                .balance(newBalance)
                .build();
    }
    
    /**
     * Creates a new instance with updated active state
     * "With" method for immutable transformation
     */
    public AccountDTO withActive(boolean newActive) {
        return this.toBuilder()
                .active(newActive)
                .build();
    }
    
    /**
     * Creates a new instance with updated holder
     * "With" method for immutable transformation
     */
    public AccountDTO withHolder(ClientDTO newHolder) {
        return this.toBuilder()
                .holder(newHolder)
                .build();
    }
    
    /**
     * Creates a new instance updating multiple fields at once
     * Method for multiple immutable updates
     */
        public AccountDTO withUpdates(
            Optional<BigDecimal> newBalance,
            Optional<Boolean> newActive) {
        
        return this.toBuilder()
                .balance(newBalance.orElse(this.balance))
                .active(newActive.orElse(this.active))
                .build();
    }
} 