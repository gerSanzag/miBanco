package com.mibanco.repository.internal;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.dto.mappers.TransactionMapper;
import com.mibanco.model.Transaction;
import com.mibanco.model.enums.TransactionOperationType;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of Transaction repository
 * Visibility restricted to internal package
 */
class TransactionRepositoryImpl extends BaseRepositoryImpl<Transaction, Long, TransactionOperationType> implements TransactionRepository {
    
    /**
     * Constructor with package visibility
     */
    TransactionRepositoryImpl() {
        super();
    }
   
    @Override
    public Optional<List<Transaction>> findByAccount(Optional<String> accountNumber) {
        return accountNumber.flatMap(number -> 
            findAllByPredicate(transaction -> transaction.getAccountNumber().equals(number))
        );
    }
    
    @Override
    public Optional<List<Transaction>> findByType(Optional<TransactionType> type) {
        return type.flatMap(t -> 
            findAllByPredicate(transaction -> transaction.getType() == t)
        );
    }
    
    @Override
    public Optional<List<Transaction>> findByDate(Optional<LocalDate> date) {
        return date.flatMap(d -> 
            findAllByPredicate(transaction -> 
                transaction.getDate().toLocalDate().equals(d))
        );
    }
    
    @Override
    public Optional<List<Transaction>> findByDateRange(Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        return startDate.flatMap(start -> 
            endDate.flatMap(end -> 
                findAllByPredicate(transaction -> {
                    LocalDateTime transactionDate = transaction.getDate();
                    return !transactionDate.toLocalDate().isBefore(start) && 
                           !transactionDate.toLocalDate().isAfter(end);
                })
            )
        );
    }
    
    
    
    /**
     * Gets configuration for this repository
     * @return Map with configuration (path, class, ID extractor)
     */
    @Override
    protected Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", "src/main/resources/data/transaccion.json");
        config.put("classType", Transaction.class);
        config.put("idExtractor", (Function<Transaction, Long>) Transaction::getId);
        return config;
    }
    
    /**
     * Creates a new transaction with automatic ID
     * Uses DTOs to maintain entity immutability
     * Pure functional approach with Optional
     * @param transaction Transaction to create
     * @return Created transaction with new ID
     */
    @Override
    protected Transaction createWithNewId(Transaction transaction) {
        TransactionMapper mapper = new TransactionMapper();
        
        return mapper.toDtoDirect(transaction)
            .map(dto -> dto.toBuilder()
                .id(idCounter.incrementAndGet())
                .build())
            .flatMap(mapper::toEntityDirect)
            .orElseThrow(() -> new IllegalStateException("Could not process Transaction entity"));
    }
} 