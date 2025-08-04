package com.mibanco.modelTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.enums.AccountType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AccountType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("AccountType Enum Tests")
class AccountTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 3 values")
        void shouldHaveExactlyThreeValues() {
            // Arrange
            AccountType[] values = AccountType.values();
            
            // Act & Assert
            assertThat(values).hasSize(3);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            AccountType[] values = AccountType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    AccountType.SAVINGS,      // AHORRO
                    AccountType.CHECKING,     // CORRIENTE
                    AccountType.FIXED_TERM    // PLAZO_FIJO
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            AccountType[] values = AccountType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    AccountType.SAVINGS,      // AHORRO
                    AccountType.CHECKING,     // CORRIENTE
                    AccountType.FIXED_TERM    // PLAZO_FIJO
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
            assertThat(AccountType.SAVINGS.name()).isEqualTo("SAVINGS");      // AHORRO
            assertThat(AccountType.CHECKING.name()).isEqualTo("CHECKING");    // CORRIENTE
            assertThat(AccountType.FIXED_TERM.name()).isEqualTo("FIXED_TERM"); // PLAZO_FIJO
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(AccountType.SAVINGS.toString()).isEqualTo("SAVINGS");      // AHORRO
            assertThat(AccountType.CHECKING.toString()).isEqualTo("CHECKING");    // CORRIENTE
            assertThat(AccountType.FIXED_TERM.toString()).isEqualTo("FIXED_TERM"); // PLAZO_FIJO
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(AccountType.valueOf("SAVINGS")).isEqualTo(AccountType.SAVINGS);      // AHORRO
            assertThat(AccountType.valueOf("CHECKING")).isEqualTo(AccountType.CHECKING);    // CORRIENTE
            assertThat(AccountType.valueOf("FIXED_TERM")).isEqualTo(AccountType.FIXED_TERM); // PLAZO_FIJO
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            AccountType savings1 = AccountType.SAVINGS;      // AHORRO
            AccountType savings2 = AccountType.SAVINGS;      // AHORRO
            AccountType checking = AccountType.CHECKING;     // CORRIENTE
            
            // Act & Assert
            assertThat(savings1).isEqualTo(savings2);
            assertThat(savings1).isNotEqualTo(checking);
            assertThat(savings1 == savings2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            AccountType type = AccountType.CHECKING;  // CORRIENTE
            
            // Act - Functional switch expression
            String result = switch (type) {
                case SAVINGS -> "Savings account";      // Cuenta de ahorro
                case CHECKING -> "Checking account";    // Cuenta corriente
                case FIXED_TERM -> "Fixed term account"; // Cuenta a plazo fijo
            };
            
            // Assert
            assertThat(result).isEqualTo("Checking account");  // Cuenta corriente
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(AccountType.SAVINGS.ordinal()).isEqualTo(0);      // AHORRO
            assertThat(AccountType.CHECKING.ordinal()).isEqualTo(1);     // CORRIENTE
            assertThat(AccountType.FIXED_TERM.ordinal()).isEqualTo(2);   // PLAZO_FIJO
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            AccountType type = null;
            
            // Act & Assert
            assertThat(type).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (AccountType type : AccountType.values()) {
                counter++;
                assertThat(type).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(3);
        }
    }
} 