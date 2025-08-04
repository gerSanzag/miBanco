package com.mibanco.modelTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.enums.CardType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CardType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("CardType Enum Tests")
class CardTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 2 values")
        void shouldHaveExactlyTwoValues() {
            // Arrange
            CardType[] values = CardType.values();
            
            // Act & Assert
            assertThat(values).hasSize(2);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            CardType[] values = CardType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    CardType.DEBIT,      // Débito
                    CardType.CREDIT      // Crédito
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            CardType[] values = CardType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    CardType.DEBIT,      // Débito
                    CardType.CREDIT      // Crédito
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
            assertThat(CardType.DEBIT.name()).isEqualTo("DEBIT");      // Débito
            assertThat(CardType.CREDIT.name()).isEqualTo("CREDIT");    // Crédito
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(CardType.DEBIT.toString()).isEqualTo("DEBIT");      // Débito
            assertThat(CardType.CREDIT.toString()).isEqualTo("CREDIT");    // Crédito
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(CardType.valueOf("DEBIT")).isEqualTo(CardType.DEBIT);      // Débito
            assertThat(CardType.valueOf("CREDIT")).isEqualTo(CardType.CREDIT);    // Crédito
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            CardType debit1 = CardType.DEBIT;      // Débito
            CardType debit2 = CardType.DEBIT;      // Débito
            CardType credit = CardType.CREDIT;     // Crédito
            
            // Act & Assert
            assertThat(debit1).isEqualTo(debit2);
            assertThat(debit1).isNotEqualTo(credit);
            assertThat(debit1 == debit2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            CardType type = CardType.CREDIT;  // Crédito
            
            // Act - Functional switch expression
            String result = switch (type) {
                case DEBIT -> "Debit card";      // Tarjeta de débito
                case CREDIT -> "Credit card";    // Tarjeta de crédito
            };
            
            // Assert
            assertThat(result).isEqualTo("Credit card");  // Tarjeta de crédito
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(CardType.DEBIT.ordinal()).isEqualTo(0);      // Débito
            assertThat(CardType.CREDIT.ordinal()).isEqualTo(1);     // Crédito
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            CardType type = null;
            
            // Act & Assert
            assertThat(type).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (CardType type : CardType.values()) {
                counter++;
                assertThat(type).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(2);
        }
    }
} 