package com.mibanco.dto.mappers;

import com.mibanco.dto.CardDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Card;
import com.mibanco.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper implementation for Card using functional approach
 */
public class CardMapper implements Mapper<Card, CardDTO> {
    private final Mapper<Client, ClientDTO> clientMapper;

    public CardMapper(Mapper<Client, ClientDTO> clientMapper) {
        this.clientMapper = clientMapper;
    }
    /**
     * Converts a Card to CardDTO
     * Strictly functional implementation with Optional
     */
    @Override
    public Optional<CardDTO> toDto(Optional<Card> cardOpt) {
        return cardOpt.map(card -> CardDTO.builder()
            .number(card.getNumber()) // numero
            .holder(card.getHolder() != null ? 
                clientMapper.toDto(Optional.of(card.getHolder())).orElse(null) : null) // titular
            .associatedAccountNumber(card.getAssociatedAccountNumber()) // numeroCuentaAsociada
            .type(card.getType()) // tipo
            .expirationDate(card.getExpirationDate()) // fechaExpiracion
            .active(card.isActive()) // activa
            .build());
    }

    /**
     * Converts a CardDTO to Card
     * Strictly functional implementation with Optional
     * Copies all fields from the DTO, including the number if present
     */
    @Override
    public Optional<Card> toEntity(Optional<CardDTO> dtoOpt) {
        return dtoOpt.map(dto -> Card.builder()
            .number(dto.getNumber()) // numero
            .holder(dto.getHolder() != null ? 
                clientMapper.toEntity(Optional.of(dto.getHolder())).orElse(null) : null) // titular
            .associatedAccountNumber(dto.getAssociatedAccountNumber()) // numeroCuentaAsociada
            .type(dto.getType()) // tipo
            .expirationDate(dto.getExpirationDate()) // fechaExpiracion
            .active(dto.isActive()) // activa
            .build());
    }
   
    /**
     * Overload that accepts Card directly
     * For convenience in contexts not using Optional
     */
    public Optional<CardDTO> toDtoDirect(Card card) {
        return toDto(Optional.ofNullable(card));
    }
    
    /**
     * Overload that accepts CardDTO directly
     * For convenience in contexts not using Optional
     */
    public Optional<Card> toEntityDirect(CardDTO dto) {
        return toEntity(Optional.ofNullable(dto));
    }
    
    /**
     * Converts a list of Card to a list of CardDTO
     * Using functional programming with streams
     * @return Optional with the list of DTOs or Optional.empty() if input is null
     */
    public Optional<List<CardDTO>> toDtoList(Optional<List<Card>> cards) {
        return cards.map(list -> list.stream()
                        .map(card -> toDto(Optional.of(card)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<Card> directly
     */
    public Optional<List<CardDTO>> toDtoList(List<Card> cards) {
        return toDtoList(Optional.ofNullable(cards));
    }
    
    /**
     * Converts a list of CardDTO to a list of Card
     * Using functional programming with streams
     * @return Optional with the list of entities or Optional.empty() if input is null
     */
    public Optional<List<Card>> toEntityList(Optional<List<CardDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> toEntity(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<CardDTO> directly
     */
    public Optional<List<Card>> toEntityList(List<CardDTO> dtos) {
        return toEntityList(Optional.ofNullable(dtos));
    }
} 