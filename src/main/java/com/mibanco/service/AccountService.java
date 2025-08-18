package com.mibanco.service;

import com.mibanco.dto.AccountDTO;
import com.mibanco.model.enums.AccountType;
import com.mibanco.service.TransactionOperationsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * Service interface for Account operations
 */
public interface AccountService {
    
    // /**
    //  * Creates a new account
    //  * @param accountDTO Optional with new account data
    //  * @return Optional with created account DTO
    //  */
    // Optional<AccountDTO> saveAccount(Optional<AccountDTO> accountDTO);
    
    /**
     * Updates complete account information
     * @param accountNumber Account number to update
     * @param accountDTO Optional with new account data
     * @return Optional with updated account DTO
     */
    Optional<AccountDTO> updateMultipleFields(Long accountId, Optional<AccountDTO> accountDTO);
    
    /**
     * Gets an account by its number
     * @param accountNumber Optional with account number
     * @return Optional with account DTO
     */
    Optional<AccountDTO> getAccountByNumber(Optional<Long> accountId);
    
    /**
     * Gets all accounts
     * @return Optional with list of account DTOs
     */
    Optional<List<AccountDTO>> getAllAccounts();
    
    /**
     * Updates an account's balance
     * @param accountNumber Account number
     * @param newBalance Optional with new balance
     * @return Optional with updated account DTO
     */
    Optional<AccountDTO> updateAccountBalance(Long accountId, Optional<BigDecimal> newBalance);
    
    /**
     * Updates an account's active status
     * @param accountNumber Account number
     * @param newActive Optional with new status
     * @return Optional with updated account DTO
     */
    Optional<AccountDTO> updateAccountStatus(Long accountId, Optional<Boolean> newActive);

    /**
     * Updates an account's holder
     * @param accountId Account ID
     * @param newHolder Optional with new holder account DTO
     * @return Optional with updated account DTO
     */
    Optional<AccountDTO> updateAccountHolder(Long accountId, Optional<AccountDTO> newHolder);
    
    /**
     * Deletes an account by its number
     * @param accountNumber Optional with account number to delete
     * @return true if deleted successfully, false if not
     */
    boolean deleteAccount(Optional<Long> accountId);

    /**
     * Deletes an account by its number and returns the deleted account
     * @param accountNumber Optional with account number to delete
     * @return Optional with deleted account DTO
     */
    Optional<AccountDTO> deleteByNumber(Optional<Long> accountId);

    /**
     * Restores a previously deleted account
     * @param accountNumber Optional with account number to restore
     * @return Optional with restored account DTO
     */
    Optional<AccountDTO> restoreAccount(Optional<Long> accountId);

    /**
     * Gets the total number of accounts
     * @return Number of accounts
     */
    long countAccounts();

    /**
     * Sets the current user for audit operations
     * @param user Current user
     */
    void setCurrentUser(String user);

    /**
     * Searches accounts by holder ID
     * @param holderId Optional with holder ID
     * @return Optional with list of holder's account DTOs
     */
    Optional<List<AccountDTO>> searchByHolderId(Optional<Long> holderId);

    /**
     * Searches accounts by type
     * @param type Optional with account type
     * @return Optional with list of account DTOs of the specified type
     */
    Optional<List<AccountDTO>> searchByType(Optional<AccountType> type);

    /**
     * Gets all active accounts
     * @return Optional with list of active account DTOs
     */
    Optional<List<AccountDTO>> searchActive();

    /**
     * Creates a new account from raw data with mandatory initial balance
     * @param rawData Map with raw account data
     * @param initialAmount Initial deposit amount
     * @param transactionService Transaction service for processing initial deposit
     * @return Optional with created account DTO
     */
    Optional<AccountDTO> createAccountDto(Map<String, String> rawData, BigDecimal initialAmount, TransactionOperationsService transactionService);
} 