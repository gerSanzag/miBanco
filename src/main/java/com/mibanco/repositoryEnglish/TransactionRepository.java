package com.mibanco.repositoryEnglish;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mibanco.modelEnglish.Transaction;
import com.mibanco.modelEnglish.enums.TransactionOperationType;
import com.mibanco.modelEnglish.enums.TransactionType;
import com.mibanco.repositoryEnglish.util.BaseRepository;

/**
 * Interface for Transaction CRUD operations
 * Extends the base interface to inherit generic CRUD operations
 * Includes specific methods for transaction searches
 */
public interface TransactionRepository extends BaseRepository<Transaction, Long, TransactionOperationType> {
    
    /**
     * Searches transactions by account number
     * @param accountNumber Optional with the account number
     * @return Optional with list of account transactions
     */
    Optional<List<Transaction>> findByAccount(Optional<String> accountNumber);
    
    /**
     * Searches transactions by type
     * @param type Optional with the transaction type
     * @return Optional with list of transactions of the specified type
     */
    Optional<List<Transaction>> findByType(Optional<TransactionType> type);
    
    /**
     * Searches transactions by date
     * @param date Optional with the transaction date
     * @return Optional with list of transactions of the specified date
     */
    Optional<List<Transaction>> findByDate(Optional<LocalDate> date);
    
    /**
     * Searches transactions in a date range
     * @param startDate Optional with the start date
     * @param endDate Optional with the end date
     * @return Optional with list of transactions in the specified range
     */
    Optional<List<Transaction>> findByDateRange(Optional<LocalDate> startDate, Optional<LocalDate> endDate);
} 