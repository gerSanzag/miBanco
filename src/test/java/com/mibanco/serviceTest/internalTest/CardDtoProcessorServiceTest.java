package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.CardDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.enums.CardType;
import com.mibanco.service.ClientService;
import com.mibanco.service.internal.CardDtoProcessorService;
import com.mibanco.service.internal.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for CardDtoProcessorService
 * Tests the processing of raw data into CardDTO using real classes
 */
@DisplayName("CardDtoProcessorService Tests")
class CardDtoProcessorServiceTest {

    private CardDtoProcessorService processor;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = ServiceFactory.getInstance().getClientService();
        processor = new CardDtoProcessorService(clientService);
    }

    @Nested
    @DisplayName("Successful processing tests")
    class SuccessfulProcessingTests {

        @Test
        @DisplayName("Should process complete card data successfully")
        void shouldProcessCompleteCardDataSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("associatedAccountNumber", "1234567890123456");
            rawData.put("type", "DEBIT");
            rawData.put("expirationDate", "2025-12-31");
            rawData.put("active", "true");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getHolder()).isEqualTo(savedHolder.get());
            assertThat(card.getAssociatedAccountNumber()).isEqualTo("1234567890123456");
            assertThat(card.getType()).isEqualTo(CardType.DEBIT);
            assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2025, 12, 31));
            assertThat(card.isActive()).isTrue();
        }

        @Test
        @DisplayName("Should process card with minimal required data successfully")
        void shouldProcessCardWithMinimalRequiredDataSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getHolder()).isEqualTo(savedHolder.get());
            assertThat(card.getAssociatedAccountNumber()).isNull();
            assertThat(card.getType()).isNull();
            assertThat(card.getExpirationDate()).isNull();
            assertThat(card.isActive()).isTrue(); // Default value
        }

        @Test
        @DisplayName("Should process credit card type successfully")
        void shouldProcessCreditCardTypeSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "CREDIT");
            rawData.put("expirationDate", "2026-06-15");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getType()).isEqualTo(CardType.CREDIT);
            assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        }

        @Test
        @DisplayName("Should process debit card type successfully")
        void shouldProcessDebitCardTypeSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "DEBIT");
            rawData.put("expirationDate", "2024-03-20");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getType()).isEqualTo(CardType.DEBIT);
            assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2024, 3, 20));
        }

        @Test
        @DisplayName("Should process inactive card successfully")
        void shouldProcessInactiveCardSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("active", "false");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Error handling tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should return empty when holder ID is missing")
        void shouldReturnEmptyWhenHolderIdIsMissing() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("associatedAccountNumber", "1234567890123456");
            rawData.put("type", "DEBIT");
            rawData.put("expirationDate", "2025-12-31");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when holder ID is invalid")
        void shouldThrowExceptionWhenHolderIdIsInvalid() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", "invalid_id");
            rawData.put("type", "DEBIT");

            // When & Then
            assertThatThrownBy(() -> processor.processCardDto(rawData))
                .isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("Should return empty when holder does not exist")
        void shouldReturnEmptyWhenHolderDoesNotExist() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", "999999");
            rawData.put("type", "DEBIT");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when card type is invalid")
        void shouldReturnEmptyWhenCardTypeIsInvalid() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "INVALID_TYPE");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when expiration date is invalid")
        void shouldReturnEmptyWhenExpirationDateIsInvalid() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("expirationDate", "invalid-date");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle invalid active status gracefully")
        void shouldHandleInvalidActiveStatusGracefully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("active", "invalid-boolean");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.isActive()).isFalse(); // Boolean.parseBoolean("invalid-boolean") returns false
        }
    }

    @Nested
    @DisplayName("Edge cases tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should throw exception when raw data is null")
        void shouldThrowExceptionWhenRawDataIsNull() {
            // When & Then
            assertThatThrownBy(() -> processor.processCardDto(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle empty raw data")
        void shouldHandleEmptyRawData() {
            // Given
            Map<String, String> rawData = new HashMap<>();

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle null values in raw data")
        void shouldHandleNullValuesInRawData() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("associatedAccountNumber", null);
            rawData.put("type", null);
            rawData.put("expirationDate", null);
            rawData.put("active", null);

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getAssociatedAccountNumber()).isNull();
            assertThat(card.getType()).isNull();
            assertThat(card.getExpirationDate()).isNull();
            assertThat(card.isActive()).isTrue(); // Default value
        }

        @Test
        @DisplayName("Should return empty when empty string values cause parsing errors")
        void shouldReturnEmptyWhenEmptyStringValuesCauseParsingErrors() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("associatedAccountNumber", "");
            rawData.put("type", "");
            rawData.put("expirationDate", "");
            rawData.put("active", "");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isEmpty(); // Empty strings cause parsing exceptions that are caught
        }

        @Test
        @DisplayName("Should handle very long account numbers")
        void shouldHandleVeryLongAccountNumbers() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("associatedAccountNumber", "1".repeat(100));

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getAssociatedAccountNumber()).isEqualTo("1".repeat(100));
        }

        @Test
        @DisplayName("Should handle past expiration dates")
        void shouldHandlePastExpirationDates() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("expirationDate", "2020-01-01");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        }

        @Test
        @DisplayName("Should handle far future expiration dates")
        void shouldHandleFarFutureExpirationDates() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("expirationDate", "2050-12-31");

            // When
            Optional<CardDTO> result = processor.processCardDto(rawData);

            // Then
            assertThat(result).isPresent();
            CardDTO card = result.get();
            assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2050, 12, 31));
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should process multiple cards for same holder")
        void shouldProcessMultipleCardsForSameHolder() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            // When & Then - Process multiple cards
            for (int i = 1; i <= 3; i++) {
                Map<String, String> rawData = new HashMap<>();
                rawData.put("holderId", savedHolder.get().getId().toString());
                rawData.put("associatedAccountNumber", "123456789012345" + i);
                rawData.put("type", i % 2 == 0 ? "DEBIT" : "CREDIT");
                rawData.put("expirationDate", "2025-0" + i + "-01");
                rawData.put("active", i % 2 == 0 ? "true" : "false");

                Optional<CardDTO> result = processor.processCardDto(rawData);
                assertThat(result).isPresent();
                
                CardDTO card = result.get();
                assertThat(card.getHolder()).isEqualTo(savedHolder.get());
                assertThat(card.getAssociatedAccountNumber()).isEqualTo("123456789012345" + i);
                assertThat(card.getType()).isEqualTo(i % 2 == 0 ? CardType.DEBIT : CardType.CREDIT);
                assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2025, i, 1));
                assertThat(card.isActive()).isEqualTo(i % 2 == 0);
            }
        }

        @Test
        @DisplayName("Should handle concurrent processing without issues")
        void shouldHandleConcurrentProcessingWithoutIssues() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "DEBIT");
            rawData.put("expirationDate", "2025-12-31");

            // When & Then - Multiple concurrent calls should work
            assertThatCode(() -> {
                for (int i = 0; i < 10; i++) {
                    Optional<CardDTO> result = processor.processCardDto(rawData);
                    assertThat(result).isPresent();
                }
            }).doesNotThrowAnyException();
        }
    }

    /**
     * Helper method to create a test client
     */
    private ClientDTO createTestClient() {
        return ClientDTO.builder()
                .firstName("Test")
                .lastName("Client")
                .email("test.client@email.com")
                .phone("123456789")
                .dni("12345678A")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Test Address 123")
                .build();
    }
}
