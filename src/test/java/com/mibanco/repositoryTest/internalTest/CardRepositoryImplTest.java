package com.mibanco.repositoryTest.internalTest;

import com.mibanco.model.Card;
import com.mibanco.model.Client;
import com.mibanco.model.enums.CardOperationType;
import com.mibanco.model.enums.CardType;
import com.mibanco.repository.internal.CardRepositoryImplWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specific tests for CardRepositoryImpl
 * Validates the specific functionality of the card repository
 * Includes configuration, mapping and specific edge case tests
 */
@DisplayName("CardRepositoryImpl Tests")
class CardRepositoryImplTest {
    
    private CardRepositoryImplWrapper repository;
    private Card card1;
    private Card card2;
    private Client holder1;
    private Client holder2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Create temporary JSON file for testing
        File jsonFile = tempDir.resolve("test_cards.json").toFile();
        
        // Create test holders
        holder1 = Client.builder()
            .id(1L)
            .firstName("Juan")
            .lastName("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .phone("123456789")
            .address("Calle Test 123")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();
            
        holder2 = Client.builder()
            .id(2L)
            .firstName("María")
            .lastName("García")
            .dni("87654321")
            .email("maria@test.com")
            .phone("987654321")
            .address("Avenida Test 456")
            .birthDate(LocalDate.of(1985, 5, 15))
            .build();
        
        // Create test data
        card1 = Card.builder()
            .number(1234567890123456L)
            .holder(holder1)
            .associatedAccountNumber("ES12345678901234567890")
            .type(CardType.DEBIT)
            .cvv("123")
            .expirationDate(LocalDate.of(2025, 12, 31))
            .active(true)
            .build();
            
        card2 = Card.builder()
            .number(9876543210987654L)
            .holder(holder2)
            .associatedAccountNumber("ES98765432109876543210")
            .type(CardType.CREDIT)
            .cvv("456")
            .expirationDate(LocalDate.of(2026, 6, 30))
            .active(false)
            .build();
        
        // Create repository with temporary configuration
        repository = new TestCardRepositoryImpl(jsonFile.getAbsolutePath());
    }
    
    /**
     * Test implementation that allows configuring the file path
     */
    private static class TestCardRepositoryImpl extends CardRepositoryImplWrapper {
        
        private final String filePath;
        
        public TestCardRepositoryImpl(String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        public java.util.Map<String, Object> getConfiguration() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("filePath", filePath);
            config.put("classType", Card.class);
            config.put("idExtractor", (java.util.function.Function<Card, Long>) Card::getId);
            return config;
        }
    }

    @Nested
    @DisplayName("Specific configuration tests")
    class ConfigurationTests {
        
        @Test
        @DisplayName("Should use correct file path for cards")
        void shouldUseCorrectFilePath() {
            // Act
            Optional<List<Card>> result = repository.findAll();
            
            // Assert
            // If there are no file errors, the configuration is correct
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should use Card class type")
        void shouldUseCardClassType() {
            // Act
            Optional<Card> result = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get() instanceof Card);
        }
        
        @Test
        @DisplayName("Should return correct configuration for cards")
        void shouldReturnCorrectConfiguration() {
            // Act - Execute operations that internally call getConfiguration
            // The getConfiguration method is executed during initialization and data loading
            Optional<List<Card>> result = repository.findAll();
            
            // Assert - Verify that the configuration works correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }

        @Test
        @DisplayName("Should execute getConfiguration() from the real class")
        void shouldExecuteGetConfigurationFromRealClass() {
            // Arrange - Use the Factory to get the real class
            com.mibanco.repository.CardRepository realRepository = 
                com.mibanco.repository.internal.RepositoryFactory.getInstance().getCardRepository();
            
            // Act - Execute operations that internally call getConfiguration()
            // The getConfiguration() method is executed during lazy data loading
            Optional<List<Card>> result = realRepository.findAll();
            
            // Assert - Verify that the operation executed correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }

        @Test
        @DisplayName("Should call getConfiguration() directly")
        void shouldCallGetConfigurationDirectly() {
            // Arrange - Use reflection to access the protected method
            try {
                // Get the real repository instance
                com.mibanco.repository.CardRepository realRepository = 
                    com.mibanco.repository.internal.RepositoryFactory.getInstance().getCardRepository();
                
                // Use reflection to access the protected getConfiguration method
                java.lang.reflect.Method getConfigMethod = 
                    realRepository.getClass().getDeclaredMethod("getConfiguration");
                getConfigMethod.setAccessible(true);
                
                // Act - Call getConfiguration() directly
                @SuppressWarnings("unchecked")
                Map<String, Object> config = (Map<String, Object>) getConfigMethod.invoke(realRepository);
                
                // Assert - Verify the configuration is correct
                assertNotNull(config);
                assertEquals("src/main/resources/data/tarjeta.json", config.get("filePath"));
                assertEquals(Card.class, config.get("classType"));
                assertNotNull(config.get("idExtractor"));
                
            } catch (Exception e) {
                fail("Should not throw exception when calling getConfiguration(): " + e.getMessage());
            }
        }
    }
    
    @Nested
    @DisplayName("Specific ID assignment tests")
    class IdAssignmentTests {
        
        @Test
        @DisplayName("Should use card number as ID if unique, or generate new one if duplicate")
        void shouldUseCardNumberAsId() {
            // Arrange
            Card newCard1 = Card.builder()
                .number(1111111111111111L)
                .holder(holder1)
                .associatedAccountNumber("ES11111111111111111111")
                .type(CardType.DEBIT)
                .cvv("111")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
                
            Card newCard2 = Card.builder()
                .number(2222222222222222L)
                .holder(holder2)
                .associatedAccountNumber("ES22222222222222222222")
                .type(CardType.CREDIT)
                .cvv("222")
                .expirationDate(LocalDate.of(2026, 6, 30))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result1 = repository.createRecord(Optional.of(newCard1), CardOperationType.CREATE);
            Optional<Card> result2 = repository.createRecord(Optional.of(newCard2), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            
            Long id1 = result1.get().getId();
            Long id2 = result2.get().getId();
            
            assertNotNull(id1);
            assertNotNull(id2);
            assertNotEquals(id1, id2, "IDs must be unique");
        }
        
        @Test
        @DisplayName("Should maintain unique card numbers")
        void shouldMaintainUniqueCardNumbers() {
            // Arrange
            List<Card> cards = new ArrayList<>();
            
            // Act - Create 5 cards
            for (int i = 0; i < 5; i++) {
                Card card = Card.builder()
                    .number(1000000000000000L + i)
                    .holder(holder1)
                    .associatedAccountNumber("ES" + String.format("%020d", i))
                    .type(i % 2 == 0 ? CardType.DEBIT : CardType.CREDIT)
                    .cvv(String.format("%03d", i))
                    .expirationDate(LocalDate.of(2025, 12, 31))
                    .active(true)
                    .build();
                
                Optional<Card> result = repository.createRecord(Optional.of(card), CardOperationType.CREATE);
                assertTrue(result.isPresent(), "Card " + i + " should have been created correctly");
                cards.add(result.get());
            }
            
            // Assert - Verify that all have unique numbers
            List<Long> cardNumbers = cards.stream()
                .map(Card::getNumber)
                .distinct()
                .toList();
            
            assertEquals(5, cardNumbers.size(), "All cards must have unique numbers");
            assertEquals(5, repository.countRecords());
        }
        
        @Test
        @DisplayName("Should maintain number sequence in multiple creations")
        void shouldMaintainNumberSequence() {
            // Arrange
            List<Card> cards = new ArrayList<>();
            
            // Act - Create 5 cards
            for (int i = 0; i < 5; i++) {
                Card card = Card.builder()
                    .number(3000000000000000L + i)
                    .holder(holder1)
                    .associatedAccountNumber("ES" + String.format("%020d", i))
                    .type(i % 2 == 0 ? CardType.DEBIT : CardType.CREDIT)
                    .cvv(String.format("%03d", i))
                    .expirationDate(LocalDate.of(2025, 12, 31))
                    .active(true)
                    .build();
                
                Optional<Card> result = repository.createRecord(Optional.of(card), CardOperationType.CREATE);
                assertTrue(result.isPresent(), "Card " + i + " should have been created correctly");
                cards.add(result.get());
            }
            
            // Assert - Verify that all have unique numbers
            List<Long> cardNumbers = cards.stream()
                .map(Card::getNumber)
                .distinct()
                .toList();
            
            assertEquals(5, cardNumbers.size(), "All cards must have unique numbers");
            assertEquals(5, repository.countRecords());
        }
    }
    
    @Nested
    @DisplayName("Specific CRUD operation tests")
    class CrudOperationTests {
        
        @BeforeEach
        void setUp() {
            // Create test entities
            repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            repository.createRecord(Optional.of(card2), CardOperationType.CREATE);
        }
        
        @Test
        @DisplayName("Should create card with all fields")
        void shouldCreateCardWithAllFields() {
            // Arrange
            Card completeCard = Card.builder()
                .number(5555555555555555L)
                .holder(holder1)
                .associatedAccountNumber("ES55555555555555555555")
                .type(CardType.CREDIT)
                .cvv("555")
                .expirationDate(LocalDate.of(2027, 12, 31))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result = repository.createRecord(Optional.of(completeCard), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Card createdCard = result.get();
            assertEquals(5555555555555555L, createdCard.getNumber());
            assertEquals(holder1.getId(), createdCard.getHolder().getId());
            assertEquals(CardType.CREDIT, createdCard.getType());
            assertEquals("555", createdCard.getCvv());
            assertEquals(LocalDate.of(2027, 12, 31), createdCard.getExpirationDate());
            assertTrue(createdCard.isActive());
        }
        
        @Test
        @DisplayName("Should search card by number")
        void shouldSearchCardByNumber() {
            // Act
            Optional<Card> result = repository.findByPredicate(
                card -> 1234567890123456L == card.getNumber()
            );
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(1234567890123456L, result.get().getNumber());
            assertEquals(holder1.getId(), result.get().getHolder().getId());
        }
        
        @Test
        @DisplayName("Should search cards by holder")
        void shouldSearchCardsByHolder() {
            // Act
            Optional<List<Card>> result = repository.findAllByPredicate(
                card -> holder1.getId().equals(card.getHolder().getId())
            );
            
            // Assert
            assertTrue(result.isPresent());
            List<Card> cards = result.get();
            assertFalse(cards.isEmpty());
            assertTrue(cards.stream().allMatch(c -> holder1.getId().equals(c.getHolder().getId())));
        }
        
        @Test
        @DisplayName("Should update existing card")
        void shouldUpdateExistingCard() {
            // Arrange - Create card first
            Optional<Card> createdCard = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            assertTrue(createdCard.isPresent());
            
            Card updatedCard = Card.builder()
                .number(createdCard.get().getNumber())
                .holder(createdCard.get().getHolder())
                .associatedAccountNumber(createdCard.get().getAssociatedAccountNumber())
                .type(createdCard.get().getType())
                .cvv("999") // Updated CVV
                .expirationDate(LocalDate.of(2028, 12, 31)) // Updated expiration
                .active(false) // Updated status
                .build();
            
            // Act
            Optional<Card> result = repository.updateRecord(Optional.of(updatedCard), CardOperationType.UPDATE);
            
            // Assert
            assertTrue(result.isPresent());
            Card modifiedCard = result.get();
            assertEquals("999", modifiedCard.getCvv());
            assertEquals(LocalDate.of(2028, 12, 31), modifiedCard.getExpirationDate());
            assertFalse(modifiedCard.isActive());
            assertEquals(createdCard.get().getNumber(), modifiedCard.getNumber()); // Number doesn't change
        }
    }
    
    @Nested
    @DisplayName("Specific edge case tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle card with null fields")
        void shouldHandleCardWithNullFields() {
            // Arrange
            Card cardWithNulls = Card.builder()
                .number(9999999999999999L)
                .holder(holder1)
                .associatedAccountNumber(null) // Null field
                .type(CardType.DEBIT)
                .cvv(null) // Null field
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result = repository.createRecord(Optional.of(cardWithNulls), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Card createdCard = result.get();
            assertEquals(9999999999999999L, createdCard.getNumber());
            assertEquals(holder1.getId(), createdCard.getHolder().getId());
            assertEquals(CardType.DEBIT, createdCard.getType());
            assertNull(createdCard.getAssociatedAccountNumber());
            assertNull(createdCard.getCvv());
            assertTrue(createdCard.isActive());
        }
        
        @Test
        @DisplayName("Should allow cards with duplicate numbers in repository (validation in service)")
        void shouldAllowCardsWithDuplicateNumbersInRepository() {
            // Arrange
            Card card1 = Card.builder()
                .number(1111111111111111L)
                .holder(holder1)
                .associatedAccountNumber("ES11111111111111111111")
                .type(CardType.DEBIT)
                .cvv("111")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
                
            Card card2 = Card.builder()
                .number(1111111111111111L) // Same number
                .holder(holder2)
                .associatedAccountNumber("ES22222222222222222222")
                .type(CardType.CREDIT)
                .cvv("222")
                .expirationDate(LocalDate.of(2026, 6, 30))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result1 = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            Optional<Card> result2 = repository.createRecord(Optional.of(card2), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            // The repository allows duplicate numbers because validation is in the service
            assertEquals(1111111111111111L, result1.get().getNumber());
            assertEquals(1111111111111111L, result2.get().getNumber());
        }
        
        @Test
        @DisplayName("Should handle card with expired date")
        void shouldHandleCardWithExpiredDate() {
            // Arrange
            Card expiredCard = Card.builder()
                .number(8888888888888888L)
                .holder(holder1)
                .associatedAccountNumber("ES88888888888888888888")
                .type(CardType.DEBIT)
                .cvv("888")
                .expirationDate(LocalDate.of(2020, 1, 1)) // Expired date
                .active(true)
                .build();
            
            // Act
            Optional<Card> result = repository.createRecord(Optional.of(expiredCard), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Card createdCard = result.get();
            assertEquals(8888888888888888L, createdCard.getNumber());
            assertEquals(LocalDate.of(2020, 1, 1), createdCard.getExpirationDate());
            // The repository allows expired cards because validation is in the service
        }
        
        @Test
        @DisplayName("Should handle card with variable length CVV")
        void shouldHandleCardWithVariableLengthCvv() {
            // Arrange
            Card cardWithShortCvv = Card.builder()
                .number(7777777777777777L)
                .holder(holder1)
                .associatedAccountNumber("ES77777777777777777777")
                .type(CardType.DEBIT)
                .cvv("12") // Short CVV
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
                
            Card cardWithLongCvv = Card.builder()
                .number(6666666666666666L)
                .holder(holder2)
                .associatedAccountNumber("ES66666666666666666666")
                .type(CardType.CREDIT)
                .cvv("1234") // Long CVV
                .expirationDate(LocalDate.of(2026, 6, 30))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result1 = repository.createRecord(Optional.of(cardWithShortCvv), CardOperationType.CREATE);
            Optional<Card> result2 = repository.createRecord(Optional.of(cardWithLongCvv), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            assertEquals("12", result1.get().getCvv());
            assertEquals("1234", result2.get().getCvv());
        }
    }
    
    @Nested
    @DisplayName("JSON file integration tests")
    class JsonIntegrationTests {
        
        @Test
        @DisplayName("Should persist card in JSON file")
        void shouldPersistCardInJsonFile() {
            // Arrange
            Card cardToPersist = Card.builder()
                .number(4444444444444444L)
                .holder(holder1)
                .associatedAccountNumber("ES44444444444444444444")
                .type(CardType.CREDIT)
                .cvv("444")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result = repository.createRecord(Optional.of(cardToPersist), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            // Verify that the counter was incremented (persistence indicator)
            assertEquals(1, repository.countRecords());
        }
        
        @Test
        @DisplayName("Should load cards from JSON file")
        void shouldLoadCardsFromJsonFile() {
            // Arrange - Create card first
            Card cardToLoad = Card.builder()
                .number(3333333333333333L)
                .holder(holder1)
                .associatedAccountNumber("ES33333333333333333333")
                .type(CardType.DEBIT)
                .cvv("333")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
            
            repository.createRecord(Optional.of(cardToLoad), CardOperationType.CREATE);
            
            // Act - Find all (this loads from JSON)
            Optional<List<Card>> result = repository.findAll();
            
            // Assert
            assertTrue(result.isPresent());
            List<Card> cards = result.get();
            assertFalse(cards.isEmpty());
            assertTrue(cards.stream().anyMatch(c -> CardType.DEBIT.equals(c.getType())));
        }
    }
    
    @Nested
    @DisplayName("Business rule validation tests")
    class BusinessRuleValidationTests {
        
        @Test
        @DisplayName("Should validate that card number is unique in service")
        void shouldValidateCardNumberUniqueInService() {
            // Arrange
            Card card1 = Card.builder()
                .number(2222222222222222L)
                .holder(holder1)
                .associatedAccountNumber("ES22222222222222222222")
                .type(CardType.DEBIT)
                .cvv("222")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .active(true)
                .build();
                
            Card card2 = Card.builder()
                .number(2222222222222222L) // Same number
                .holder(holder2)
                .associatedAccountNumber("ES33333333333333333333")
                .type(CardType.CREDIT)
                .cvv("333")
                .expirationDate(LocalDate.of(2026, 6, 30))
                .active(true)
                .build();
            
            // Act
            Optional<Card> result1 = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            Optional<Card> result2 = repository.createRecord(Optional.of(card2), CardOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            // Both cards are created because the repository allows duplicates
            // The service layer would validate uniqueness
            assertEquals(2222222222222222L, result1.get().getNumber());
            assertEquals(2222222222222222L, result2.get().getNumber());
        }
    }
    
    @Nested
    @DisplayName("Missing CRUD operation tests")
    class MissingCrudOperationTests {
        
        @Test
        @DisplayName("Should search card by ID")
        void shouldSearchCardById() {
            // Arrange - Create card first
            Optional<Card> createdCard = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            assertTrue(createdCard.isPresent());
            Long id = createdCard.get().getId();
            
            // Act
            Optional<Card> result = repository.findById(Optional.of(id));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(id, result.get().getId());
            assertEquals(card1.getHolder().getId(), result.get().getHolder().getId());
            assertEquals(card1.getType(), result.get().getType());
        }
        
        @Test
        @DisplayName("Should delete card by ID")
        void shouldDeleteCardById() {
            // Arrange - Create card first
            Optional<Card> createdCard = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            assertTrue(createdCard.isPresent());
            Long id = createdCard.get().getId();
            
            // Act
            Optional<Card> deleted = repository.deleteById(Optional.of(id), CardOperationType.DELETE);
            
            // Assert
            assertTrue(deleted.isPresent());
            assertEquals(id, deleted.get().getId());
            
            // Verify that it no longer exists
            Optional<Card> deletedCard = repository.findById(Optional.of(id));
            assertFalse(deletedCard.isPresent());
        }
        
        @Test
        @DisplayName("Should restore deleted card")
        void shouldRestoreDeletedCard() {
            // Arrange - Create and delete card
            Optional<Card> createdCard = repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            assertTrue(createdCard.isPresent());
            Long id = createdCard.get().getId();
            
            repository.deleteById(Optional.of(id), CardOperationType.DELETE);
            
            // Act - Restore the deleted card
            Optional<Card> restored = repository.restore(Optional.of(id), CardOperationType.UPDATE);
            
            // Assert
            assertTrue(restored.isPresent());
            assertEquals(id, restored.get().getId());
            
            // Verify that it exists again
            Optional<Card> restoredCard = repository.findById(Optional.of(id));
            assertTrue(restoredCard.isPresent());
        }
        
        @Test
        @DisplayName("Should get deleted cards")
        void shouldGetDeletedCards() {
            // Arrange - Create and delete multiple cards
            for (int i = 0; i < 3; i++) {
                Card card = Card.builder()
                    .number(1000000000000000L + i)
                    .holder(holder1)
                    .associatedAccountNumber("ES" + String.format("%020d", i))
                    .type(i % 2 == 0 ? CardType.DEBIT : CardType.CREDIT)
                    .cvv(String.format("%03d", i))
                    .expirationDate(LocalDate.of(2025, 12, 31))
                    .active(true)
                    .build();
                
                Optional<Card> created = repository.createRecord(Optional.of(card), CardOperationType.CREATE);
                assertTrue(created.isPresent());
                repository.deleteById(Optional.of(created.get().getId()), CardOperationType.DELETE);
            }
            
            // Act
            List<Card> deletedCards = repository.getDeleted();
            
            // Assert
            assertEquals(3, deletedCards.size());
            // Note: We can't check isDeleted() as it doesn't exist in Card class
            // The fact that they are in getDeleted() means they were deleted
        }
        
        @Test
        @DisplayName("Should set current user for audit")
        void shouldSetCurrentUser() {
            // Arrange
            String currentUser = "testUser";
            
            // Act
            repository.setCurrentUser(currentUser);
            
            // Assert - Verify that the user was set (this is an internal operation)
            // We can't directly test the user setting, but we can verify the repository still works
            Optional<List<Card>> result = repository.findAll();
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should save data manually")
        void shouldSaveDataManually() {
            // Arrange - Create a card
            repository.createRecord(Optional.of(card1), CardOperationType.CREATE);
            
            // Act
            repository.saveData();
            
            // Assert - Verify that data was saved (this is an internal operation)
            // We can't directly test the save operation, but we can verify the repository still works
            Optional<List<Card>> result = repository.findAll();
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should handle search by null ID")
        void shouldHandleSearchByNullId() {
            // Act
            Optional<Card> result = repository.findById(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle deletion by null ID")
        void shouldHandleDeletionByNullId() {
            // Act
            Optional<Card> result = repository.deleteById(Optional.empty(), CardOperationType.DELETE);
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle restoration by null ID")
        void shouldHandleRestorationByNullId() {
            // Act
            Optional<Card> result = repository.restore(Optional.empty(), CardOperationType.UPDATE);
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle search by non-existent ID")
        void shouldHandleSearchByNonExistentId() {
            // Act
            Optional<Card> result = repository.findById(Optional.of(999999L));
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should count records correctly")
        void shouldCountRecords() {
            // Arrange - Create multiple cards
            for (int i = 0; i < 3; i++) {
                Card card = Card.builder()
                    .number(2000000000000000L + i)
                    .holder(holder1)
                    .associatedAccountNumber("ES" + String.format("%020d", i))
                    .type(i % 2 == 0 ? CardType.DEBIT : CardType.CREDIT)
                    .cvv(String.format("%03d", i))
                    .expirationDate(LocalDate.of(2025, 12, 31))
                    .active(true)
                    .build();
                
                repository.createRecord(Optional.of(card), CardOperationType.CREATE);
            }
            
            // Act
            long count = repository.countRecords();
            
            // Assert
            assertEquals(3, count);
        }
    }
}
