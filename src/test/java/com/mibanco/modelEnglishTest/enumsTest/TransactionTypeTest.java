package com.mibanco.modelEnglishTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelEnglish.enums.TransactionType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TransactionType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("TransactionType Enum Tests")
class TransactionTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 5 values")
        void shouldHaveExactlyFiveValues() {
            // Arrange
            TransactionType[] values = TransactionType.values();
            
            // Act & Assert
            assertThat(values).hasSize(5);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            TransactionType[] values = TransactionType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    TransactionType.DEPOSIT,                // Depósito
                    TransactionType.WITHDRAWAL,             // Retiro
                    TransactionType.SENT_TRANSFER,          // Transferencia enviada
                    TransactionType.RECEIVED_TRANSFER,      // Transferencia recibida
                    TransactionType.SERVICE_PAYMENT         // Pago de servicio
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            TransactionType[] values = TransactionType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    TransactionType.DEPOSIT,                // Depósito
                    TransactionType.WITHDRAWAL,             // Retiro
                    TransactionType.SENT_TRANSFER,          // Transferencia enviada
                    TransactionType.RECEIVED_TRANSFER,      // Transferencia recibida
                    TransactionType.SERVICE_PAYMENT         // Pago de servicio
                );
        }
    }

    @Nested
    @DisplayName("Enum names")
    class EnumNamesTest {

        @Test
        @DisplayName("Should have correct names")
        void shouldHaveCorrectNames() {
            // Arrange & Act & Assert
            assertThat(TransactionType.DEPOSIT.name()).isEqualTo("DEPOSIT");                // Depósito
            assertThat(TransactionType.WITHDRAWAL.name()).isEqualTo("WITHDRAWAL");          // Retiro
            assertThat(TransactionType.SENT_TRANSFER.name()).isEqualTo("SENT_TRANSFER");    // Transferencia enviada
            assertThat(TransactionType.RECEIVED_TRANSFER.name()).isEqualTo("RECEIVED_TRANSFER"); // Transferencia recibida
            assertThat(TransactionType.SERVICE_PAYMENT.name()).isEqualTo("SERVICE_PAYMENT"); // Pago de servicio
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(TransactionType.DEPOSIT.toString()).isEqualTo("DEPOSIT");                // Depósito
            assertThat(TransactionType.WITHDRAWAL.toString()).isEqualTo("WITHDRAWAL");          // Retiro
            assertThat(TransactionType.SENT_TRANSFER.toString()).isEqualTo("SENT_TRANSFER");    // Transferencia enviada
            assertThat(TransactionType.RECEIVED_TRANSFER.toString()).isEqualTo("RECEIVED_TRANSFER"); // Transferencia recibida
            assertThat(TransactionType.SERVICE_PAYMENT.toString()).isEqualTo("SERVICE_PAYMENT"); // Pago de servicio
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(TransactionType.valueOf("DEPOSIT")).isEqualTo(TransactionType.DEPOSIT);                // Depósito
            assertThat(TransactionType.valueOf("WITHDRAWAL")).isEqualTo(TransactionType.WITHDRAWAL);          // Retiro
            assertThat(TransactionType.valueOf("SENT_TRANSFER")).isEqualTo(TransactionType.SENT_TRANSFER);    // Transferencia enviada
            assertThat(TransactionType.valueOf("RECEIVED_TRANSFER")).isEqualTo(TransactionType.RECEIVED_TRANSFER); // Transferencia recibida
            assertThat(TransactionType.valueOf("SERVICE_PAYMENT")).isEqualTo(TransactionType.SERVICE_PAYMENT); // Pago de servicio
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            TransactionType deposit1 = TransactionType.DEPOSIT;                // Depósito
            TransactionType deposit2 = TransactionType.DEPOSIT;                // Depósito
            TransactionType withdrawal = TransactionType.WITHDRAWAL;           // Retiro
            
            // Act & Assert
            assertThat(deposit1).isEqualTo(deposit2);
            assertThat(deposit1).isNotEqualTo(withdrawal);
            assertThat(deposit1 == deposit2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            TransactionType type = TransactionType.SENT_TRANSFER;  // Transferencia enviada
            
            // Act - Functional switch expression
            String result = switch (type) {
                case DEPOSIT -> "Deposit completed";                // Depósito realizado
                case WITHDRAWAL -> "Withdrawal completed";          // Retiro realizado
                case SENT_TRANSFER -> "Transfer sent";              // Transferencia enviada
                case RECEIVED_TRANSFER -> "Transfer received";      // Transferencia recibida
                case SERVICE_PAYMENT -> "Service payment";          // Pago de servicio
            };
            
            // Assert
            assertThat(result).isEqualTo("Transfer sent");  // Transferencia enviada
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(TransactionType.DEPOSIT.ordinal()).isEqualTo(0);                // Depósito
            assertThat(TransactionType.WITHDRAWAL.ordinal()).isEqualTo(1);             // Retiro
            assertThat(TransactionType.SENT_TRANSFER.ordinal()).isEqualTo(2);          // Transferencia enviada
            assertThat(TransactionType.RECEIVED_TRANSFER.ordinal()).isEqualTo(3);      // Transferencia recibida
            assertThat(TransactionType.SERVICE_PAYMENT.ordinal()).isEqualTo(4);        // Pago de servicio
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            TransactionType type = null;
            
            // Act & Assert
            assertThat(type).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (TransactionType type : TransactionType.values()) {
                counter++;
                assertThat(type).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(5);
        }
    }
} 