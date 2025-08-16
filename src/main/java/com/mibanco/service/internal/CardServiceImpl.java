package com.mibanco.service.internal;

import com.mibanco.dto.CardDTO;
import com.mibanco.dto.mappers.CardMapper;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Card;
import com.mibanco.model.enums.CardOperationType;
import com.mibanco.model.enums.CardType;
import com.mibanco.repository.CardRepository;
import com.mibanco.repository.internal.RepositoryFactory;
import com.mibanco.service.CardService;
import com.mibanco.util.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Card service implementation
 * Extends BaseServiceImpl to inherit generic CRUD operations
 */
class CardServiceImpl extends BaseServiceImpl<CardDTO, Card, Long, CardOperationType, CardRepository> implements CardService {
    
    private static final CardRepository cardRepository;
    private static final CardMapper mapper;
    private static final ClientMapper clientMapper;
    private final CardOperationType updateType = CardOperationType.UPDATE;
    
    static {
        cardRepository = RepositoryFactory.getInstance().getCardRepository();
        clientMapper = new ClientMapper();
        mapper = new CardMapper(clientMapper);
    }
    
    public CardServiceImpl() {
        super(cardRepository, mapper);
    }
    
    @Override
    public Optional<CardDTO> createCardDto(Map<String, String> cardData) {
        try {
            // Use specialized processor to create DTO with validations
            CardDtoProcessorService processor = new CardDtoProcessorService(
                new ClientServiceImpl()
            );
            
            return processor.processCardDto(cardData)
                .flatMap(cardDto -> {
                    // Validate unique card number before saving
                    validateUniqueCardNumber(cardDto);
                    return saveEntity(CardOperationType.CREATE, Optional.of(cardDto));
                });
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Helper method to validate unique card number
     * @param dto Card DTO to validate
     * @throws ValidationException if the card number already exists
     */
    private void validateUniqueCardNumber(CardDTO dto) {
        // Only validate if the number is not null (basic validation already done in view)
        if (dto.getNumber() != null) {
            Optional<Card> existingCard = cardRepository.findByPredicate(
                card -> dto.getNumber().equals(card.getNumber())
            );
            
            if (existingCard.isPresent()) {
                throw new ValidationException("A card with number already exists: " + dto.getNumber());
            }
        }
    }
    
    
    
    @Override
    public Optional<CardDTO> updateMultipleFields(Long cardNumber, Optional<CardDTO> cardDTO) {
        Optional<CardDTO> updateMultipleFields = update(
            cardNumber,
            cardDTO,
            updateType,
            (existingCard, newCard) -> existingCard.withUpdates(
                Optional.ofNullable(newCard.getExpirationDate()),
                Optional.ofNullable(newCard.isActive())
            )
        );
        saveEntity(updateType, updateMultipleFields);
        return updateMultipleFields;
    }
    
    @Override
    public Optional<CardDTO> getCardByNumber(Optional<Long> cardNumber) {
        return cardNumber.flatMap(number -> 
            cardRepository.findById(Optional.of(number))
                .flatMap(card -> mapper.toDto(Optional.of(card)))
        );
    }
    
    @Override
    public Optional<List<CardDTO>> getAllCards() {
        return findAll();
    }
    
    @Override
    public Optional<CardDTO> updateExpirationDate(Long cardNumber, Optional<LocalDate> newDate) {
        Optional<CardDTO> updateDate = updateField(
            cardNumber,
            newDate,
            CardDTO::getExpirationDate,
            CardDTO::withExpirationDate
        );
        saveEntity(updateType, updateDate);
        return updateDate;
    }
    
    @Override
    public Optional<CardDTO> updateCardStatus(Long cardNumber, Optional<Boolean> newActive) {
        Optional<CardDTO> updateStatus = updateField(
            cardNumber,
            newActive,
            CardDTO::isActive,
            CardDTO::withActive 
        );
        saveEntity(updateType, updateStatus);
        return updateStatus;
    }
    
    @Override
    public Optional<CardDTO> updateCardHolder(Long cardNumber, Optional<CardDTO> newHolder) {
        Optional<CardDTO> updateHolder = newHolder.flatMap(dto -> 
                Optional.of(dto.getHolder())   
                .flatMap(holder -> updateField(
                    cardNumber,
                    Optional.of(holder),
                    CardDTO::getHolder,
                    (card, newHolderEntity) -> card.toBuilder().holder(holder).build()
                ))
        );
        saveEntity(updateType, updateHolder);
        return updateHolder;
    }
    
    @Override
    public boolean deleteCard(Optional<Long> cardNumber) {
        return deleteById(cardNumber, CardOperationType.DELETE);
    }
    
    @Override
    public Optional<CardDTO> deleteByNumber(Optional<Long> cardNumber) {
        return cardNumber.flatMap(number -> 
            cardRepository.deleteById(Optional.of(number), CardOperationType.DELETE)
                .flatMap(card -> mapper.toDto(Optional.of(card)))
        );
    }
    
    @Override
    public Optional<CardDTO> restoreCard(Optional<Long> cardNumber) {
        return restore(cardNumber, CardOperationType.MODIFY);
    }
    
        @Override
    public long countCards() {
        return countRecords();
    }
    
    @Override
    public void setCurrentUser(String user) {
        super.setCurrentUser(user);
    }
    
    @Override
    public Optional<List<CardDTO>> searchByHolderId(Optional<Long> clientId) {
        return clientId.flatMap(id -> 
            cardRepository.findAllByPredicate(card -> card.getHolder().getId().equals(id))
                .flatMap(cards -> Optional.of(
                    cards.stream()
                        .map(card -> mapper.toDto(Optional.of(card)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<CardDTO>> searchByType(Optional<CardType> type) {
        return type.flatMap(t -> 
            cardRepository.findAllByPredicate(card -> card.getType().equals(t))
                .flatMap(cards -> Optional.of(
                    cards.stream()
                        .map(card -> mapper.toDto(Optional.of(card)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<CardDTO>> searchActive() {
        return cardRepository.findAllByPredicate(card -> card.isActive())
            .flatMap(cards -> Optional.of(
                cards.stream()
                    .map(card -> mapper.toDto(Optional.of(card)).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList()
            ));
    }
    
    @Override
    public Optional<List<CardDTO>> searchByAssociatedAccount(Optional<String> accountNumber) {
        return accountNumber.flatMap(number -> 
            cardRepository.findAllByPredicate(card -> card.getAssociatedAccountNumber().equals(number))
                .flatMap(cards -> Optional.of(
                    cards.stream()
                        .map(card -> mapper.toDto(Optional.of(card)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
} 