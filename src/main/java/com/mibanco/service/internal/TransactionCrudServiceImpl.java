package com.mibanco.service.internal;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.dto.mappers.TransactionMapper;
import com.mibanco.model.Transaction;
import com.mibanco.model.enums.TransactionOperationType;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.repository.TransactionRepository;
import com.mibanco.repository.internal.RepositoryServiceLocator;
import com.mibanco.service.TransactionCrudService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Implementation of the Transaction Service
 * Extends BaseServiceImpl to inherit generic CRUD operations
 */
class TransactionCrudServiceImpl extends BaseServiceImpl<TransactionDTO, Transaction, Long, TransactionOperationType, TransactionRepository> implements TransactionCrudService {
    
    private static final TransactionRepository transactionRepository;
    private static final TransactionMapper mapper;
   
    static {
        transactionRepository = RepositoryServiceLocator.getService(TransactionRepository.class);
        mapper = new TransactionMapper();
       
    }
    
    public TransactionCrudServiceImpl() {
        super(transactionRepository, mapper);
    }
    
    @Override
    public Optional<TransactionDTO> createTransaction(Map<String, String> transactionData) {
        // Use the specialized processor to create the DTO with validations
        TransactionDtoProcessorService processor = new TransactionDtoProcessorService();
        
        return processor.processTransactionDto(transactionData)
            .flatMap(transactionDto -> saveEntity(TransactionOperationType.CREATE, Optional.of(transactionDto)));
    }
 
    @Override
    public Optional<TransactionDTO> getTransactionById(Optional<Long> id) {
        return findById(id);
    }
    
    @Override
    public Optional<List<TransactionDTO>> getAllTransactions() {
        return findAll();
    }
    
    @Override
    public Optional<List<TransactionDTO>> searchByAccount(Optional<String> accountNumber) {
        return accountNumber.flatMap(number -> 
            transactionRepository.findByAccount(Optional.of(number))
                .flatMap(transactions -> Optional.of(
                    transactions.stream()
                        .map(transaction -> mapper.toDto(Optional.of(transaction)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TransactionDTO>> searchByType(Optional<TransactionType> type) {
        return type.flatMap(t -> 
            transactionRepository.findByType(Optional.of(t))
                .flatMap(transactions -> Optional.of(
                    transactions.stream()
                        .map(transaction -> mapper.toDto(Optional.of(transaction)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TransactionDTO>> searchByDate(Optional<LocalDate> date) {
        return date.flatMap(d -> 
            transactionRepository.findByDate(Optional.of(d))
                .flatMap(transactions -> Optional.of(
                    transactions.stream()
                        .map(transaction -> mapper.toDto(Optional.of(transaction)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TransactionDTO>> searchByDateRange(Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        return startDate.flatMap(start -> 
            endDate.flatMap(end -> 
                transactionRepository.findByDateRange(Optional.of(start), Optional.of(end))
                    .flatMap(transactions -> Optional.of(
                        transactions.stream()
                            .map(transaction -> mapper.toDto(Optional.of(transaction)).orElse(null))
                            .filter(java.util.Objects::nonNull)
                            .toList()
                    ))
            )
        );
    }
    
    @Override
    public boolean deleteTransaction(Optional<Long> id) {
        return deleteById(id, TransactionOperationType.DELETE);
    }
    
    @Override
    public long countTransactions() {
        return countRecords();
    }
    
    @Override
    public void setCurrentUser(String user) {
        super.setCurrentUser(user);
    }
} 