package com.mibanco.service;

import com.mibanco.dto.CardDTO;
import com.mibanco.model.enums.CardType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for Card operations
 */
public interface CardService {
    
    /**
     * Creates a new card from raw data
     * @param cardData Map with raw card data
     * @return Optional with created card DTO
     */
    Optional<CardDTO> createCardDto(Map<String, String> cardData);
    
    
    /**
     * Updates complete card information
     * @param cardNumber Card number to update
     * @param updates Map with raw update data
     * @return Optional with updated card DTO
     */
    Optional<CardDTO> updateMultipleFields(Long cardNumber, Map<String, Object> updates);
    
    /**
     * Gets a card by its number
     * @param cardNumber Optional with card number
     * @return Optional with card DTO
     */
    Optional<CardDTO> getCardByNumber(Optional<Long> cardNumber);
    
    /**
     * Gets all cards
     * @return Optional with list of card DTOs
     */
    Optional<List<CardDTO>> getAllCards();
    
    /**
     * Updates a card's expiration date
     * @param cardNumber Card number
     * @param newDate Optional with new expiration date
     * @return Optional with updated card DTO
     */
    Optional<CardDTO> updateExpirationDate(Long cardNumber, Optional<LocalDate> newDate);
    
    /**
     * Updates a card's active status
     * @param cardNumber Card number
     * @param newActive Optional with new status
     * @return Optional with updated card DTO
     */
    Optional<CardDTO> updateCardStatus(Long cardNumber, Optional<Boolean> newActive);
    
    /**
     * Updates a card's holder
     * @param cardNumber Card number
     * @param newHolder Optional with new holder
     * @return Optional with updated card DTO
     */
    Optional<CardDTO> updateCardHolder(Long cardNumber, Optional<CardDTO> newHolder);
    
    /**
     * Deletes a card by its number
     * @param cardNumber Optional with card number to delete
     * @return true if deleted successfully, false if not
     */
    boolean deleteCard(Optional<Long> cardNumber);

    /**
     * Deletes a card by its number and returns the deleted card
     * @param cardNumber Optional with card number to delete
     * @return Optional with deleted card DTO
     */
    Optional<CardDTO> deleteByNumber(Optional<Long> cardNumber);

    /**
     * Restores a previously deleted card
     * @param cardNumber Optional with card number to restore
     * @return Optional with restored card DTO
     */
    Optional<CardDTO> restoreCard(Optional<Long> cardNumber);

    /**
     * Gets the total number of cards
     * @return Number of cards
     */
    long countCards();

    /**
     * Sets the current user for audit operations
     * @param user Current user
     */
    void setCurrentUser(String user);

    /**
     * Searches cards by holder client ID
     * @param clientId Optional with client ID
     * @return Optional with list of client's card DTOs
     */
    Optional<List<CardDTO>> searchByHolderId(Optional<Long> clientId);

    /**
     * Searches cards by type
     * @param type Optional with card type
     * @return Optional with list of card DTOs of the specified type
     */
    Optional<List<CardDTO>> searchByType(Optional<CardType> type);

    /**
     * Gets all active cards
     * @return Optional with list of active card DTOs
     */
    Optional<List<CardDTO>> searchActive();

    /**
     * Searches cards by associated account number
     * @param accountNumber Optional with account number
     * @return Optional with list of card DTOs associated with the account
     */
    Optional<List<CardDTO>> searchByAssociatedAccount(Optional<String> accountNumber);
} 