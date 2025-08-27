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
     * Assumes that required fields validation has been done by the controller
     * @param rawData Map with transaction data (already validated)
     * @return Optional with the processed TransactionDTO or empty if there are errors
     */
    public Optional<TransactionDTO> processTransactionDto(Map<String, String> rawData) {
        return Optional.ofNullable(rawData)
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
    

} 