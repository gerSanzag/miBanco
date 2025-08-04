package com.mibanco.serviceEnglish;

import com.mibanco.dtoEnglish.TransactionDTO;
import com.mibanco.modelEnglish.enums.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for Transaction operations
 */
public interface TransactionCrudService {
    
    /**
     * Creates a new transaction with raw data and automatic ID generation
     * @param transactionData Map with transaction data (accountNumber, destinationAccountNumber, type, amount, description)
     * @return Optional with created transaction DTO
     */
    Optional<TransactionDTO> createTransaction(Map<String, String> transactionData);
    
    /**
     * Gets a transaction by its ID
     * @param id Optional with transaction ID
     * @return Optional with transaction DTO
     */
    Optional<TransactionDTO> getTransactionById(Optional<Long> id);
    
    /**
     * Gets all transactions
     * @return Optional with list of transaction DTOs
     */
    Optional<List<TransactionDTO>> getAllTransactions();
    
    /**
     * Searches transactions by account number
     * @param accountNumber Optional with account number
     * @return Optional with list of transaction DTOs
     */
    Optional<List<TransactionDTO>> searchByAccount(Optional<String> accountNumber);
    
    /**
     * Searches transactions by type
     * @param type Optional with transaction type
     * @return Optional with list of transaction DTOs
     */
    Optional<List<TransactionDTO>> searchByType(Optional<TransactionType> type);
    
    /**
     * Searches transactions by date
     * @param date Optional with transaction date
     * @return Optional with list of transaction DTOs
     */
    Optional<List<TransactionDTO>> searchByDate(Optional<LocalDate> date);
    
    /**
     * Searches transactions in a date range
     * @param startDate Optional with start date
     * @param endDate Optional with end date
     * @return Optional with list of transaction DTOs
     */
    Optional<List<TransactionDTO>> searchByDateRange(Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
 
    
    /**
     * Deletes a transaction by its ID
     * @param id Optional with transaction ID to delete
     * @return true if deleted successfully, false if not
     */
    boolean deleteTransaction(Optional<Long> id);
    
    /**
     * Gets the total number of transactions
     * @return Number of transactions
     */
    long countTransactions();
    
    /**
     * Sets the current user for audit operations
     * @param user Current user
     */
    void setCurrentUser(String user);

  
    
} 