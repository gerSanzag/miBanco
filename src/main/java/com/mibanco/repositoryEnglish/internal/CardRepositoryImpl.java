package com.mibanco.repositoryEnglish.internal;

import com.mibanco.dtoEnglish.CardDTO;
import com.mibanco.dtoEnglish.mappers.ClientMapper;
import com.mibanco.dtoEnglish.mappers.CardMapper;
import com.mibanco.modelEnglish.Card;
import com.mibanco.modelEnglish.enums.CardOperationType;
import com.mibanco.modelEnglish.enums.CardType;
import com.mibanco.repositoryEnglish.CardRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Implementation of Card repository
 * Visibility restricted to internal package
 */
class CardRepositoryImpl extends BaseRepositoryImpl<Card, Long, CardOperationType> implements CardRepository {
    
    /**
     * Constructor with package visibility
     */
    CardRepositoryImpl() {
        super();
    }
   
    /**
     * Specific implementation to assign new ID to Card
     * Uses DTOs to maintain entity immutability
     * Pure functional approach with Optional
     * Generates a unique 16-digit card number
     * @param card Card without assigned ID
     * @return Card with new assigned ID
     */
    @Override
    protected Card createWithNewId(Card card) {
        ClientMapper clientMapper = new ClientMapper();
        CardMapper mapper = new CardMapper(clientMapper);
        
        return mapper.toDtoDirect(card)
            .map(dto -> {
                Long number = generateUniqueCardNumber();
                return dto.toBuilder()
                    .number(number)
                    .build();
            })
            .flatMap(mapper::toEntityDirect)
            .orElseThrow(() -> new IllegalStateException("Could not process Card entity"));
    }
    
    /**
     * Generates a unique 16-digit card number
     * Uses ThreadLocalRandom for better distribution and performance
     * @return Long with 16 random digits
     */
    private Long generateUniqueCardNumber() {
        // Generate number between 1000000000000000L and 9999999999999999L (16 digits)
        long min = 1000000000000000L;
        long max = 9999999999999999L;
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
    
    /**
     * Specific implementation to get repository configuration
     * @return Map with necessary configuration
     */
    @Override
    protected Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", "src/main/resources/data/tarjeta.json");
        config.put("classType", Card.class);
        config.put("idExtractor", (Function<Card, Long>) Card::getId);
        return config;
    }
} 