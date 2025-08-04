package com.mibanco.service.internal;

import com.mibanco.dto.CardDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.service.ClientService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Specialized service for processing CardDTO creation
 * Applies the Single Responsibility Principle (SRP)
 * Responsible solely for transforming raw data into valid CardDTOs
 * and processing card-related operations
 */
public class CardDtoProcessorService {
    
    private final ClientService clientService;
    
    public CardDtoProcessorService(ClientService clientService) {
        this.clientService = clientService;
    }
    
    /**
     * Processes raw data and creates a valid CardDTO
     * @param rawData Map with card data
     * @return Optional with processed CardDTO or empty if there are errors
     */
    public Optional<CardDTO> processCardDto(Map<String, String> rawData) {
        return Optional.of(rawData)
            .flatMap(this::getHolderById)
            .flatMap(holder -> buildCardDTO(rawData, holder));
    }
    
    /**
     * Extracts holder ID and gets client in a single functional method
     */
    private Optional<ClientDTO> getHolderById(Map<String, String> rawData) {
        return Optional.ofNullable(rawData.get("holderId"))
            .map(Long::parseLong)
            .flatMap(holderId -> clientService.getClientById(Optional.of(holderId)));
    }
    
    /**
     * Builds the CardDTO from raw data and holder
     */
    private Optional<CardDTO> buildCardDTO(Map<String, String> rawData, ClientDTO holder) {
        try {
            CardDTO.CardDTOBuilder builder = CardDTO.builder()
                .holder(holder);

            // Apply functional transformations with validations
            Optional.ofNullable(rawData.get("associatedAccountNumber"))
                .ifPresent(builder::associatedAccountNumber);

            Optional.ofNullable(rawData.get("type"))
                .map(com.mibanco.model.enums.CardType::valueOf)
                .ifPresent(builder::type);

            Optional.ofNullable(rawData.get("expirationDate"))
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE))
                .ifPresent(builder::expirationDate);

            // Active status by default
            Optional.ofNullable(rawData.get("active"))
                .map(Boolean::parseBoolean)
                .or(() -> Optional.of(true))
                .ifPresent(builder::active);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 