package com.mibanco.modelEnglish;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

import com.mibanco.modelEnglish.enums.CardType;

import lombok.Builder;
import lombok.Value;


/**
 * Class that represents a bank card
 * Implements a completely functional approach with total immutability
 */
@Value
@Builder
public class Card implements Identifiable {

    Long number;             // numero
    Client holder;           // titular
    String associatedAccountNumber;  // numeroCuentaAsociada
    CardType type;           // tipo
    String cvv;
    LocalDate expirationDate;    // fechaExpiracion
    boolean active;          // activa
    
    @JsonCreator
    public Card(
        @JsonProperty("number") Long number,
        @JsonProperty("holder") Client holder,
        @JsonProperty("associatedAccountNumber") String associatedAccountNumber,
        @JsonProperty("type") CardType type,
        @JsonProperty("cvv") String cvv,
        @JsonProperty("expirationDate") LocalDate expirationDate,
        @JsonProperty("active") boolean active
    ) {
        this.number = number;
        this.holder = holder;
        this.associatedAccountNumber = associatedAccountNumber;
        this.type = type;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
        this.active = active;
    }
    
    /**
     * Implementation of getId() for the Identifiable interface
     * The card number is directly the identifier
     * @return The card number as Long
     */
    @Override
    public Long getId() {
        return number;
    }
    
    /**
     * Factory method to facilitate instance creation
     */
    public static Card of(Long number, Client holder, String associatedAccountNumber, 
                            CardType type, LocalDate expirationDate, String cvv, boolean active) {
        return Card.builder()
                .number(number)
                .holder(holder)
                .associatedAccountNumber(associatedAccountNumber)
                .type(type)
                .expirationDate(expirationDate)
                .cvv(cvv)
                .active(active)
                .build();
    }
    
} 