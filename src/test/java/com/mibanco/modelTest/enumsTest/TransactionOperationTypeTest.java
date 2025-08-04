package com.mibanco.modelTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.enums.TransactionOperationType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TransactionOperationType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("TransactionOperationType Enum Tests")
class TransactionOperationTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 8 values")
        void shouldHaveExactlyEightValues() {
            // Arrange
            TransactionOperationType[] values = TransactionOperationType.values();
            
            // Act & Assert
            assertThat(values).hasSize(8);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            TransactionOperationType[] values = TransactionOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    TransactionOperationType.CREATE,         // Crear
                    TransactionOperationType.CANCEL,         // Anular
                    TransactionOperationType.REVERSE,        // Reversar
                    TransactionOperationType.VERIFY,         // Verificar
                    TransactionOperationType.AUTHORIZE,      // Autorizar
                    TransactionOperationType.REJECT,         // Rechazar
                    TransactionOperationType.UPDATE,         // Actualizar
                    TransactionOperationType.DELETE          // Eliminar
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            TransactionOperationType[] values = TransactionOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    TransactionOperationType.CREATE,         // Crear
                    TransactionOperationType.CANCEL,         // Anular
                    TransactionOperationType.REVERSE,        // Reversar
                    TransactionOperationType.VERIFY,         // Verificar
                    TransactionOperationType.AUTHORIZE,      // Autorizar
                    TransactionOperationType.REJECT,         // Rechazar
                    TransactionOperationType.UPDATE,         // Actualizar
                    TransactionOperationType.DELETE          // Eliminar
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
            assertThat(TransactionOperationType.CREATE.name()).isEqualTo("CREATE");         // Crear
            assertThat(TransactionOperationType.CANCEL.name()).isEqualTo("CANCEL");         // Anular
            assertThat(TransactionOperationType.REVERSE.name()).isEqualTo("REVERSE");       // Reversar
            assertThat(TransactionOperationType.VERIFY.name()).isEqualTo("VERIFY");         // Verificar
            assertThat(TransactionOperationType.AUTHORIZE.name()).isEqualTo("AUTHORIZE");   // Autorizar
            assertThat(TransactionOperationType.REJECT.name()).isEqualTo("REJECT");         // Rechazar
            assertThat(TransactionOperationType.UPDATE.name()).isEqualTo("UPDATE");         // Actualizar
            assertThat(TransactionOperationType.DELETE.name()).isEqualTo("DELETE");         // Eliminar
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(TransactionOperationType.CREATE.toString()).isEqualTo("CREATE");         // Crear
            assertThat(TransactionOperationType.CANCEL.toString()).isEqualTo("CANCEL");         // Anular
            assertThat(TransactionOperationType.REVERSE.toString()).isEqualTo("REVERSE");       // Reversar
            assertThat(TransactionOperationType.VERIFY.toString()).isEqualTo("VERIFY");         // Verificar
            assertThat(TransactionOperationType.AUTHORIZE.toString()).isEqualTo("AUTHORIZE");   // Autorizar
            assertThat(TransactionOperationType.REJECT.toString()).isEqualTo("REJECT");         // Rechazar
            assertThat(TransactionOperationType.UPDATE.toString()).isEqualTo("UPDATE");         // Actualizar
            assertThat(TransactionOperationType.DELETE.toString()).isEqualTo("DELETE");         // Eliminar
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(TransactionOperationType.valueOf("CREATE")).isEqualTo(TransactionOperationType.CREATE);         // Crear
            assertThat(TransactionOperationType.valueOf("CANCEL")).isEqualTo(TransactionOperationType.CANCEL);         // Anular
            assertThat(TransactionOperationType.valueOf("REVERSE")).isEqualTo(TransactionOperationType.REVERSE);       // Reversar
            assertThat(TransactionOperationType.valueOf("VERIFY")).isEqualTo(TransactionOperationType.VERIFY);         // Verificar
            assertThat(TransactionOperationType.valueOf("AUTHORIZE")).isEqualTo(TransactionOperationType.AUTHORIZE);   // Autorizar
            assertThat(TransactionOperationType.valueOf("REJECT")).isEqualTo(TransactionOperationType.REJECT);         // Rechazar
            assertThat(TransactionOperationType.valueOf("UPDATE")).isEqualTo(TransactionOperationType.UPDATE);         // Actualizar
            assertThat(TransactionOperationType.valueOf("DELETE")).isEqualTo(TransactionOperationType.DELETE);         // Eliminar
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            TransactionOperationType create1 = TransactionOperationType.CREATE;         // Crear
            TransactionOperationType create2 = TransactionOperationType.CREATE;         // Crear
            TransactionOperationType cancel = TransactionOperationType.CANCEL;          // Anular
            
            // Act & Assert
            assertThat(create1).isEqualTo(create2);
            assertThat(create1).isNotEqualTo(cancel);
            assertThat(create1 == create2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            TransactionOperationType operation = TransactionOperationType.AUTHORIZE;  // Autorizar
            
            // Act - Functional switch expression
            String result = switch (operation) {
                case CREATE -> "Transaction created";         // Transacción creada
                case CANCEL -> "Transaction cancelled";       // Transacción anulada
                case REVERSE -> "Transaction reversed";       // Transacción revertida
                case VERIFY -> "Transaction verified";        // Transacción verificada
                case AUTHORIZE -> "Transaction authorized";   // Transacción autorizada
                case REJECT -> "Transaction rejected";        // Transacción rechazada
                case UPDATE -> "Transaction updated";         // Transacción actualizada
                case DELETE -> "Transaction deleted";         // Transacción eliminada
            };
            
            // Assert
            assertThat(result).isEqualTo("Transaction authorized");  // Transacción autorizada
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(TransactionOperationType.CREATE.ordinal()).isEqualTo(0);         // Crear
            assertThat(TransactionOperationType.CANCEL.ordinal()).isEqualTo(1);         // Anular
            assertThat(TransactionOperationType.REVERSE.ordinal()).isEqualTo(2);        // Reversar
            assertThat(TransactionOperationType.VERIFY.ordinal()).isEqualTo(3);         // Verificar
            assertThat(TransactionOperationType.AUTHORIZE.ordinal()).isEqualTo(4);      // Autorizar
            assertThat(TransactionOperationType.REJECT.ordinal()).isEqualTo(5);         // Rechazar
            assertThat(TransactionOperationType.UPDATE.ordinal()).isEqualTo(6);         // Actualizar
            assertThat(TransactionOperationType.DELETE.ordinal()).isEqualTo(7);         // Eliminar
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            TransactionOperationType operation = null;
            
            // Act & Assert
            assertThat(operation).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (TransactionOperationType operation : TransactionOperationType.values()) {
                counter++;
                assertThat(operation).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(8);
        }
    }
} 