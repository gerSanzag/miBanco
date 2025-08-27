package com.mibanco.service.internal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import com.mibanco.dto.ClientDTO;

/**
 * Specialized service for processing ClientDTO creation
 * Applies the Single Responsibility Principle (SRP)
 * Responsible solely for transforming raw data into valid ClientDTOs
 * and processing client-related operations
 */
public class ClientDtoProcessorService {
    
    /**
     * Processes raw data and creates a valid ClientDTO
     * Assumes that required fields validation has been done by the controller
     * @param rawData Map with client data (already validated)
     * @return Optional with processed ClientDTO or empty if there are errors
     */
    public Optional<ClientDTO> processClientDto(Map<String, String> rawData) {
        return Optional.ofNullable(rawData)
            .flatMap(this::buildClientDTO);
    }
    
    /**
     * Builds the ClientDTO from raw data
     */
    private Optional<ClientDTO> buildClientDTO(Map<String, String> rawData) {
        try {
            ClientDTO.ClientDTOBuilder builder = ClientDTO.builder();

            // Apply functional transformations with validations
            Optional.ofNullable(rawData.get("firstName"))
                .ifPresent(builder::firstName);

            Optional.ofNullable(rawData.get("lastName"))
                .ifPresent(builder::lastName);

            Optional.ofNullable(rawData.get("dni"))
                .ifPresent(builder::dni);

            Optional.ofNullable(rawData.get("email"))
                .ifPresent(builder::email);

            Optional.ofNullable(rawData.get("phone"))
                .ifPresent(builder::phone);

            Optional.ofNullable(rawData.get("address"))
                .ifPresent(builder::address);

            // Process birth date with validation
            Optional.ofNullable(rawData.get("birthDate"))
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE))
                .ifPresent(builder::birthDate);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    

} 