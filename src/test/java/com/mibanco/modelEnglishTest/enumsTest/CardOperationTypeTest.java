package com.mibanco.modelEnglishTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelEnglish.enums.CardOperationType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CardOperationType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("CardOperationType Enum Tests")
class CardOperationTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 10 values")
        void shouldHaveExactlyTenValues() {
            // Arrange
            CardOperationType[] values = CardOperationType.values();
            
            // Act & Assert
            assertThat(values).hasSize(10);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            CardOperationType[] values = CardOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    CardOperationType.CREATE,             // Crear
                    CardOperationType.MODIFY,             // Modificar
                    CardOperationType.DELETE,             // Eliminar
                    CardOperationType.ACTIVATE,           // Activar
                    CardOperationType.DEACTIVATE,         // Desactivar
                    CardOperationType.BLOCK,              // Bloquear
                    CardOperationType.UNBLOCK,            // Desbloquear
                    CardOperationType.INCREASE_LIMIT,     // Aumentar límite
                    CardOperationType.REDUCE_LIMIT,       // Reducir límite
                    CardOperationType.UPDATE              // Actualizar
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            CardOperationType[] values = CardOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    CardOperationType.CREATE,             // Crear
                    CardOperationType.MODIFY,             // Modificar
                    CardOperationType.DELETE,             // Eliminar
                    CardOperationType.ACTIVATE,           // Activar
                    CardOperationType.DEACTIVATE,         // Desactivar
                    CardOperationType.BLOCK,              // Bloquear
                    CardOperationType.UNBLOCK,            // Desbloquear
                    CardOperationType.INCREASE_LIMIT,     // Aumentar límite
                    CardOperationType.REDUCE_LIMIT,       // Reducir límite
                    CardOperationType.UPDATE              // Actualizar
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
            assertThat(CardOperationType.CREATE.name()).isEqualTo("CREATE");             // Crear
            assertThat(CardOperationType.MODIFY.name()).isEqualTo("MODIFY");             // Modificar
            assertThat(CardOperationType.DELETE.name()).isEqualTo("DELETE");             // Eliminar
            assertThat(CardOperationType.ACTIVATE.name()).isEqualTo("ACTIVATE");         // Activar
            assertThat(CardOperationType.DEACTIVATE.name()).isEqualTo("DEACTIVATE");     // Desactivar
            assertThat(CardOperationType.BLOCK.name()).isEqualTo("BLOCK");               // Bloquear
            assertThat(CardOperationType.UNBLOCK.name()).isEqualTo("UNBLOCK");           // Desbloquear
            assertThat(CardOperationType.INCREASE_LIMIT.name()).isEqualTo("INCREASE_LIMIT"); // Aumentar límite
            assertThat(CardOperationType.REDUCE_LIMIT.name()).isEqualTo("REDUCE_LIMIT"); // Reducir límite
            assertThat(CardOperationType.UPDATE.name()).isEqualTo("UPDATE");             // Actualizar
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(CardOperationType.CREATE.toString()).isEqualTo("CREATE");             // Crear
            assertThat(CardOperationType.MODIFY.toString()).isEqualTo("MODIFY");             // Modificar
            assertThat(CardOperationType.DELETE.toString()).isEqualTo("DELETE");             // Eliminar
            assertThat(CardOperationType.ACTIVATE.toString()).isEqualTo("ACTIVATE");         // Activar
            assertThat(CardOperationType.DEACTIVATE.toString()).isEqualTo("DEACTIVATE");     // Desactivar
            assertThat(CardOperationType.BLOCK.toString()).isEqualTo("BLOCK");               // Bloquear
            assertThat(CardOperationType.UNBLOCK.toString()).isEqualTo("UNBLOCK");           // Desbloquear
            assertThat(CardOperationType.INCREASE_LIMIT.toString()).isEqualTo("INCREASE_LIMIT"); // Aumentar límite
            assertThat(CardOperationType.REDUCE_LIMIT.toString()).isEqualTo("REDUCE_LIMIT"); // Reducir límite
            assertThat(CardOperationType.UPDATE.toString()).isEqualTo("UPDATE");             // Actualizar
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(CardOperationType.valueOf("CREATE")).isEqualTo(CardOperationType.CREATE);             // Crear
            assertThat(CardOperationType.valueOf("MODIFY")).isEqualTo(CardOperationType.MODIFY);             // Modificar
            assertThat(CardOperationType.valueOf("DELETE")).isEqualTo(CardOperationType.DELETE);             // Eliminar
            assertThat(CardOperationType.valueOf("ACTIVATE")).isEqualTo(CardOperationType.ACTIVATE);         // Activar
            assertThat(CardOperationType.valueOf("DEACTIVATE")).isEqualTo(CardOperationType.DEACTIVATE);     // Desactivar
            assertThat(CardOperationType.valueOf("BLOCK")).isEqualTo(CardOperationType.BLOCK);               // Bloquear
            assertThat(CardOperationType.valueOf("UNBLOCK")).isEqualTo(CardOperationType.UNBLOCK);           // Desbloquear
            assertThat(CardOperationType.valueOf("INCREASE_LIMIT")).isEqualTo(CardOperationType.INCREASE_LIMIT); // Aumentar límite
            assertThat(CardOperationType.valueOf("REDUCE_LIMIT")).isEqualTo(CardOperationType.REDUCE_LIMIT); // Reducir límite
            assertThat(CardOperationType.valueOf("UPDATE")).isEqualTo(CardOperationType.UPDATE);             // Actualizar
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            CardOperationType create1 = CardOperationType.CREATE;             // Crear
            CardOperationType create2 = CardOperationType.CREATE;             // Crear
            CardOperationType modify = CardOperationType.MODIFY;              // Modificar
            
            // Act & Assert
            assertThat(create1).isEqualTo(create2);
            assertThat(create1).isNotEqualTo(modify);
            assertThat(create1 == create2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            CardOperationType operation = CardOperationType.INCREASE_LIMIT;  // Aumentar límite
            
            // Act - Functional switch expression
            String result = switch (operation) {
                case CREATE -> "Card created";             // Tarjeta creada
                case MODIFY -> "Card modified";            // Tarjeta modificada
                case DELETE -> "Card deleted";             // Tarjeta eliminada
                case ACTIVATE -> "Card activated";         // Tarjeta activada
                case DEACTIVATE -> "Card deactivated";     // Tarjeta desactivada
                case BLOCK -> "Card blocked";              // Tarjeta bloqueada
                case UNBLOCK -> "Card unblocked";          // Tarjeta desbloqueada
                case INCREASE_LIMIT -> "Limit increased";  // Límite aumentado
                case REDUCE_LIMIT -> "Limit reduced";      // Límite reducido
                case UPDATE -> "Card updated";             // Tarjeta actualizada
            };
            
            // Assert
            assertThat(result).isEqualTo("Limit increased");  // Límite aumentado
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(CardOperationType.CREATE.ordinal()).isEqualTo(0);             // Crear
            assertThat(CardOperationType.MODIFY.ordinal()).isEqualTo(1);             // Modificar
            assertThat(CardOperationType.DELETE.ordinal()).isEqualTo(2);             // Eliminar
            assertThat(CardOperationType.ACTIVATE.ordinal()).isEqualTo(3);           // Activar
            assertThat(CardOperationType.DEACTIVATE.ordinal()).isEqualTo(4);         // Desactivar
            assertThat(CardOperationType.BLOCK.ordinal()).isEqualTo(5);              // Bloquear
            assertThat(CardOperationType.UNBLOCK.ordinal()).isEqualTo(6);            // Desbloquear
            assertThat(CardOperationType.INCREASE_LIMIT.ordinal()).isEqualTo(7);     // Aumentar límite
            assertThat(CardOperationType.REDUCE_LIMIT.ordinal()).isEqualTo(8);       // Reducir límite
            assertThat(CardOperationType.UPDATE.ordinal()).isEqualTo(9);             // Actualizar
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            CardOperationType operation = null;
            
            // Act & Assert
            assertThat(operation).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (CardOperationType operation : CardOperationType.values()) {
                counter++;
                assertThat(operation).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(10);
        }
    }
} 