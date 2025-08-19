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
     * @param rawData Map with client data
     * @return Optional with processed ClientDTO or empty if there are errors
     */
    public Optional<ClientDTO> processClientDto(Map<String, String> rawData) {
        return Optional.ofNullable(rawData)
            .filter(this::hasRequiredFields)
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
    
    /**
     * Validates that the raw data contains required fields
     * @param rawData Map with client data
     * @return true if required fields are present and valid
     */
    private boolean hasRequiredFields(Map<String, String> rawData) {
        String firstName = rawData.get("firstName");
        String lastName = rawData.get("lastName");
        String dni = rawData.get("dni");
        String email = rawData.get("email");
        String birthDate = rawData.get("birthDate");
        
        // First name is required and must not be empty
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        
        // Last name is required and must not be empty
        if (lastName == null || lastName.trim().isEmpty()) {
            return false;
        }
        
        // DNI is required and must not be empty
        if (dni == null || dni.trim().isEmpty()) {
            return false;
        }
        
        // Email is required and must not be empty
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Birth date is required and must be a valid date
        if (birthDate == null || birthDate.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
} 