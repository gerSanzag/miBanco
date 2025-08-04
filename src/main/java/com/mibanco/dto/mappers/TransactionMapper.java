package com.mibanco.dto.mappers;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.model.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper implementation for Transaction using functional approach
 * No automatic ID generation (handled by the repository)
 */
public class TransactionMapper implements Mapper<Transaction, TransactionDTO> {

    /**
     * Converts a Transaction to TransactionDTO
     * Strictly functional implementation with Optional
     */
    @Override
    public Optional<TransactionDTO> toDto(Optional<Transaction> transactionOpt) {
        return transactionOpt.map(transaction -> TransactionDTO.builder()
            .id(transaction.getId()) // id
            .accountNumber(transaction.getAccountNumber()) // numeroCuenta
            .destinationAccountNumber(transaction.getDestinationAccountNumber()) // numeroCuentaDestino
            .type(transaction.getType()) // tipo
            .amount(transaction.getAmount()) // monto
            .date(transaction.getDate()) // fecha
            .description(transaction.getDescription()) // descripcion
            .build());
    }

    /**
     * Converts a TransactionDTO to Transaction
     * Strictly functional implementation with Optional
     * No automatic ID generation (handled by the repository)
     */
    @Override
    public Optional<Transaction> toEntity(Optional<TransactionDTO> dtoOpt) {
        return dtoOpt.map(dto -> Transaction.builder()
            .id(dto.getId()) // id
            .accountNumber(dto.getAccountNumber()) // numeroCuenta
            .destinationAccountNumber(dto.getDestinationAccountNumber()) // numeroCuentaDestino
            .type(dto.getType()) // tipo
            .amount(dto.getAmount()) // monto
            .date(dto.getDate()) // fecha
            .description(dto.getDescription()) // descripcion
            .build());
    }
   
    /**
     * Overload that accepts Transaction directly
     * For convenience in contexts not using Optional
     */
    public Optional<TransactionDTO> toDtoDirect(Transaction transaction) {
        return toDto(Optional.ofNullable(transaction));
    }
    
    /**
     * Overload that accepts TransactionDTO directly
     * For convenience in contexts not using Optional
     */
    public Optional<Transaction> toEntityDirect(TransactionDTO dto) {
        return toEntity(Optional.ofNullable(dto));
    }
    
    /**
     * Converts a list of Transaction to a list of TransactionDTO
     * Using functional programming with streams
     * @return Optional with the list of DTOs or Optional.empty() if input is null
     */
    public Optional<List<TransactionDTO>> toDtoList(Optional<List<Transaction>> transactions) {
        return transactions.map(list -> list.stream()
                        .map(transaction -> toDto(Optional.of(transaction)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<Transaction> directly
     */
    public Optional<List<TransactionDTO>> toDtoList(List<Transaction> transactions) {
        return toDtoList(Optional.ofNullable(transactions));
    }
    
    /**
     * Converts a list of TransactionDTO to a list of Transaction
     * Using functional programming with streams
     * @return Optional with the list of entities or Optional.empty() if input is null
     */
    public Optional<List<Transaction>> toEntityList(Optional<List<TransactionDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> toEntity(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<TransactionDTO> directly
     */
    public Optional<List<Transaction>> toEntityList(List<TransactionDTO> dtos) {
        return toEntityList(Optional.ofNullable(dtos));
    }
} 