package com.mibanco.repository;

import com.mibanco.model.Card;
import com.mibanco.model.enums.CardOperationType;
import com.mibanco.model.enums.CardType;
import com.mibanco.repository.util.BaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for Card repository
 * Extends the base interface to inherit generic CRUD operations
 */
public interface CardRepository extends BaseRepository<Card, Long, CardOperationType> {
    
    
} 