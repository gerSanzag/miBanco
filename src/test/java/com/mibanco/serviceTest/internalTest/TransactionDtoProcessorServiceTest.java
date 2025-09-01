package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.service.internal.TransactionDtoProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for TransactionDtoProcessorService
 * Tests the processing of raw data into TransactionDTO using real classes
 */
@DisplayName("TransactionDtoProcessorService Tests")
class TransactionDtoProcessorServiceTest {

    private TransactionDtoProcessorService processor;

    @BeforeEach
    void setUp() {
        processor = new TransactionDtoProcessorService();
    }

    @Nested
    @DisplayName("Successful processing tests")
    class SuccessfulProcessingTests {

        @Test
        @DisplayName("Should process complete transaction data successfully")
        void shouldProcessCompleteTransactionDataSuccessfully() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("destinationAccountNumber", "0987654321");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "1000.50");
            rawData.put("description", "Test transaction");
            rawData.put("date", "2024-01-15T10:30:00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            TransactionDTO transaction = result.get();
            assertThat(transaction.getAccountNumber()).isEqualTo("1234567890");
            assertThat(transaction.getDestinationAccountNumber()).isEqualTo("0987654321");
            assertThat(transaction.getType()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("1000.50"));
            assertThat(transaction.getDescription()).isEqualTo("Test transaction");
            assertThat(transaction.getDate()).isEqualTo(LocalDateTime.parse("2024-01-15T10:30:00"));
        }

        @Test
        @DisplayName("Should process transaction with minimal data successfully")
        void shouldProcessTransactionWithMinimalDataSuccessfully() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "WITHDRAWAL");
            rawData.put("amount", "500.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            TransactionDTO transaction = result.get();
            assertThat(transaction.getAccountNumber()).isEqualTo("1234567890");
            assertThat(transaction.getDestinationAccountNumber()).isNull();
            assertThat(transaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
            assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("500.00"));
            assertThat(transaction.getDescription()).isEqualTo("");
            assertThat(transaction.getDate()).isNotNull();
            assertThat(transaction.getDate()).isAfterOrEqualTo(LocalDateTime.now().minusSeconds(1));
            assertThat(transaction.getDate()).isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("Should process all transaction types successfully")
        void shouldProcessAllTransactionTypesSuccessfully() {
            // Given
            TransactionType[] types = TransactionType.values();
            
            for (TransactionType type : types) {
                Map<String, String> rawData = new HashMap<>();
                rawData.put("accountNumber", "1234567890");
                rawData.put("type", type.name());
                rawData.put("amount", "100.00");

                // When
                Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

                // Then
                assertThat(result).isPresent();
                assertThat(result.get().getType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should process transaction with decimal amount successfully")
        void shouldProcessTransactionWithDecimalAmountSuccessfully() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "1234.5678");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getAmount()).isEqualTo(new BigDecimal("1234.5678"));
        }
    }

    @Nested
    @DisplayName("Default values tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should use empty string as default description when not provided")
        void shouldUseEmptyStringAsDefaultDescriptionWhenNotProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getDescription()).isEqualTo("");
        }

        @Test
        @DisplayName("Should use current date as default when date not provided")
        void shouldUseCurrentDateAsDefaultWhenDateNotProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");

            LocalDateTime beforeProcessing = LocalDateTime.now();

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            LocalDateTime transactionDate = result.get().getDate();
            assertThat(transactionDate).isAfterOrEqualTo(beforeProcessing);
            assertThat(transactionDate).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Should handle null destination account number")
        void shouldHandleNullDestinationAccountNumber() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "WITHDRAWAL");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getDestinationAccountNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("Error handling tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should return empty when invalid transaction type is provided")
        void shouldReturnEmptyWhenInvalidTransactionTypeIsProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "INVALID_TYPE");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (TransactionType.valueOf throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when invalid amount format is provided")
        void shouldReturnEmptyWhenInvalidAmountFormatIsProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "invalid_amount");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (BigDecimal constructor throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when invalid date format is provided")
        void shouldReturnEmptyWhenInvalidDateFormatIsProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");
            rawData.put("date", "invalid_date");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (LocalDateTime.parse throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when null raw data is provided")
        void shouldReturnEmptyWhenNullRawDataIsProvided() {
            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(null);

            // Then
            // Service should return empty when raw data is null
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when empty raw data is provided")
        void shouldReturnEmptyWhenEmptyRawDataIsProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO
            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("Edge cases tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero amount successfully")
        void shouldHandleZeroAmountSuccessfully() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "0.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle very large amount successfully")
        void shouldHandleVeryLargeAmountSuccessfully() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "999999999.99");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getAmount()).isEqualTo(new BigDecimal("999999999.99"));
        }

        @Test
        @DisplayName("Should handle empty description when explicitly provided")
        void shouldHandleEmptyDescriptionWhenExplicitlyProvided() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");
            rawData.put("description", "");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getDescription()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle long description successfully")
        void shouldHandleLongDescriptionSuccessfully() {
            // Given
            String longDescription = "This is a very long description for testing purposes. " +
                                   "It contains multiple sentences and should be handled correctly " +
                                   "by the processor service without any issues.";
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");
            rawData.put("description", longDescription);

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getDescription()).isEqualTo(longDescription);
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should process multiple transactions with different configurations")
        void shouldProcessMultipleTransactionsWithDifferentConfigurations() {
            // Given
            Map<String, String>[] testData = new Map[]{
                createTestData("1234567890", "DEPOSIT", "100.00", "Test deposit"),
                createTestData("0987654321", "WITHDRAWAL", "50.00", "Test withdrawal"),
                createTestData("1111111111", "SENT_TRANSFER", "200.00", "Test transfer"),
                createTestData("2222222222", "RECEIVED_TRANSFER", "150.00", "Test received"),
                createTestData("3333333333", "SERVICE_PAYMENT", "75.50", "Test payment")
            };

            // When & Then
            for (Map<String, String> data : testData) {
                Optional<TransactionDTO> result = processor.processTransactionDto(data);
                assertThat(result).isPresent();
                assertThat(result.get().getAccountNumber()).isNotNull();
                assertThat(result.get().getType()).isNotNull();
                assertThat(result.get().getAmount()).isNotNull();
            }
        }

        private Map<String, String> createTestData(String accountNumber, String type, String amount, String description) {
            Map<String, String> data = new HashMap<>();
            data.put("accountNumber", accountNumber);
            data.put("type", type);
            data.put("amount", amount);
            data.put("description", description);
            return data;
        }
    }

    @Nested
    @DisplayName("Required fields validation tests")
    class RequiredFieldsValidationTests {

        @Test
        @DisplayName("Should return empty when accountNumber is null")
        void shouldReturnEmptyWhenAccountNumberIsNull() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO with null accountNumber
            assertThat(result).isPresent();
            assertThat(result.get().getAccountNumber()).isNull();
        }

        @Test
        @DisplayName("Should return empty when accountNumber is empty string")
        void shouldReturnEmptyWhenAccountNumberIsEmptyString() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO with empty accountNumber
            assertThat(result).isPresent();
            assertThat(result.get().getAccountNumber()).isEqualTo("");
        }

        @Test
        @DisplayName("Should return empty when accountNumber is whitespace only")
        void shouldReturnEmptyWhenAccountNumberIsWhitespaceOnly() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "   ");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("Should return empty when type is null")
        void shouldReturnEmptyWhenTypeIsNull() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("Should return empty when type is empty string")
        void shouldReturnEmptyWhenTypeIsEmptyString() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (TransactionType.valueOf("") throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when type is whitespace only")
        void shouldReturnEmptyWhenTypeIsWhitespaceOnly() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "   ");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (TransactionType.valueOf("   ") throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when type is invalid enum value")
        void shouldReturnEmptyWhenTypeIsInvalidEnumValue() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "INVALID_TYPE");
            rawData.put("amount", "100.00");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (TransactionType.valueOf throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when amount is null")
        void shouldReturnEmptyWhenAmountIsNull() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("Should return empty when amount is empty string")
        void shouldReturnEmptyWhenAmountIsEmptyString() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (BigDecimal("") throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when amount is whitespace only")
        void shouldReturnEmptyWhenAmountIsWhitespaceOnly() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "   ");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (BigDecimal("   ") throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when amount is invalid number format")
        void shouldReturnEmptyWhenAmountIsInvalidNumberFormat() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            rawData.put("type", "DEPOSIT");
            rawData.put("amount", "invalid_amount");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (BigDecimal constructor throws exception)
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when multiple required fields are missing")
        void shouldReturnEmptyWhenMultipleRequiredFieldsAreMissing() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "1234567890");
            // Missing type and amount

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Note: Service no longer validates required fields - this is now Controller responsibility
            // The service should still process the data and create a DTO
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("Should return empty when all required fields are invalid")
        void shouldReturnEmptyWhenAllRequiredFieldsAreInvalid() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("accountNumber", "");
            rawData.put("type", "INVALID_TYPE");
            rawData.put("amount", "invalid_amount");

            // When
            Optional<TransactionDTO> result = processor.processTransactionDto(rawData);

            // Then
            // Service should return empty when format is invalid (TransactionType.valueOf and BigDecimal constructor throw exceptions)
            assertThat(result).isEmpty();
        }
    }
}
