package com.mibanco.repositoryEnglish;

import com.mibanco.modelEnglish.Card;
import com.mibanco.modelEnglish.enums.CardOperationType;
import com.mibanco.modelEnglish.enums.CardType;
import com.mibanco.repositoryEnglish.util.BaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for Card repository
 * Extends the base interface to inherit generic CRUD operations
 */
public interface CardRepository extends BaseRepository<Card, Long, CardOperationType> {
    
    
} 