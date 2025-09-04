package com.mibanco.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mibanco.model.enums.AccountType;
import com.mibanco.util.ReflectionUtil.NoRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

/**
 * Class that represents a bank account
 * Implements a completely functional approach with total immutability
 * Implements Identifiable to be compatible with the base repository
 */
@Value
@Builder
public class Account implements Identifiable {
    
    @NoRequest(reason = "Set automatically in repository")
    String accountNumber;    // numeroCuenta
    Client holder;           // titular
    AccountType type;        // tipo
    @NoRequest(reason = "Set automatically when creating")
    LocalDateTime creationDate;  // fechaCreacion
    BigDecimal initialBalance;   // saldoInicial
    @NoRequest(reason = "Initialized equal to initialBalance and only changes through transactions")
    BigDecimal balance;      // saldo
    @NoRequest(reason = "Set by default as active")
    boolean active;          // activa
    
    @JsonCreator
    public Account(
        @JsonProperty("accountNumber") String accountNumber,
        @JsonProperty("holder") Client holder,
        @JsonProperty("type") AccountType type,
        @JsonProperty("creationDate") LocalDateTime creationDate,
        @JsonProperty("initialBalance") BigDecimal initialBalance,
        @JsonProperty("balance") BigDecimal balance,
        @JsonProperty("active") boolean active
    ) {
        this.accountNumber = accountNumber;
        this.holder = holder;
        this.type = type;
        this.creationDate = creationDate;
        this.initialBalance = initialBalance;
        this.balance = balance;
        this.active = active;
    }
    
    /**
     * Implementation of getId() for the Identifiable interface
     * Extracts the numeric part of the IBAN (accountNumber) to use as ID
     * Uses only the first 18 digits to fit in Long
     * Example: "ES3400000000000000001002" → 340000000000000000L
     * @return The numeric part of accountNumber as Long
     */
    @Override
    public Long getId() {
        // Handle null accountNumber case
        if (accountNumber == null) {
            return null;
        }
        
        // Extract only the numeric part of the IBAN
        String numericPart = accountNumber.replaceAll("[^0-9]", "");
        
        try {
            // Use only the first 18 digits to fit in Long
            if (numericPart.length() > 18) {
                numericPart = numericPart.substring(0, 18);
            }
            return Long.parseLong(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid identifier, does not exist.");
        }
    }
    
    /**
     * Factory method to facilitate instance creation
     */
    public static Account of(String accountNumber, Client holder, AccountType type, 
                           BigDecimal initialBalance, LocalDateTime creationDate, boolean active) {
        return Account.builder()
                .accountNumber(accountNumber)
                .holder(holder)
                .type(type)
                .initialBalance(initialBalance)
                .balance(initialBalance)
                .creationDate(creationDate)
                .active(active)
                .build();
    }
    
} 