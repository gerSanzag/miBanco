package com.mibanco.service.internal;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Service specialized in processing TransactionDTO creation
 * Applies the Single Responsibility Principle (SRP)
 * It is responsible solely for transforming raw data into valid TransactionDTOs
 * and processing transaction-related operations
 */
public class TransactionDtoProcessorService {
    
    /**
     * Processes raw data and creates a valid TransactionDTO
     * @param rawData Map with transaction data
     * @return Optional with the processed TransactionDTO or empty if there are errors
     */
    public Optional<TransactionDTO> processTransactionDto(Map<String, String> rawData) {
        return Optional.ofNullable(rawData)
            .filter(this::hasRequiredFields)
            .flatMap(this::buildTransactionDTO);
    }
    
    /**
     * Builds the TransactionDTO from raw data
     */
    private Optional<TransactionDTO> buildTransactionDTO(Map<String, String> rawData) {
        try {
            TransactionDTO.TransactionDTOBuilder builder = TransactionDTO.builder();

            // Extract and validate data from Map with functional transformations
            Optional.ofNullable(rawData.get("accountNumber"))
                .ifPresent(builder::accountNumber);

            Optional.ofNullable(rawData.get("destinationAccountNumber"))
                .ifPresent(builder::destinationAccountNumber);

            Optional.ofNullable(rawData.get("type"))
                .map(TransactionType::valueOf)
                .ifPresent(builder::type);

            Optional.ofNullable(rawData.get("amount"))
                .map(BigDecimal::new)
                .ifPresent(builder::amount);

            // Default description if not provided
            Optional.ofNullable(rawData.get("description"))
                .or(() -> Optional.of(""))
                .ifPresent(builder::description);

            // Automatic date if not provided
            Optional.ofNullable(rawData.get("date"))
                .map(LocalDateTime::parse)
                .or(() -> Optional.of(LocalDateTime.now()))
                .ifPresent(builder::date);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Validates that the raw data contains required fields
     * @param rawData Map with transaction data
     * @return true if required fields are present and valid
     */
    private boolean hasRequiredFields(Map<String, String> rawData) {
        String accountNumber = rawData.get("accountNumber");
        String type = rawData.get("type");
        String amount = rawData.get("amount");
        
        // Account number is required and must not be empty
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }
        
        // Type is required and must be a valid enum value
        if (type == null || type.trim().isEmpty()) {
            return false;
        }
        
        try {
            TransactionType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        
        // Amount is required and must be a valid number
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        
        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            return false;
        }
        
        return true;
    }
} 