package com.mibanco.modelTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.enums.AccountOperationType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AccountOperationType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("AccountOperationType Enum Tests")
class AccountOperationTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 9 values")
        void shouldHaveExactlyNineValues() {
            // Arrange
            AccountOperationType[] values = AccountOperationType.values();
            
            // Act & Assert
            assertThat(values).hasSize(9);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            AccountOperationType[] values = AccountOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    AccountOperationType.CREATE,         // Crear
                    AccountOperationType.MODIFY,         // Modificar
                    AccountOperationType.DELETE,         // Eliminar
                    AccountOperationType.ACTIVATE,       // Activar
                    AccountOperationType.DEACTIVATE,     // Desactivar
                    AccountOperationType.BLOCK,          // Bloquear
                    AccountOperationType.UNBLOCK,        // Desbloquear
                    AccountOperationType.UPDATE,         // Actualizar
                    AccountOperationType.RESTORE         // Restaurar
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            AccountOperationType[] values = AccountOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    AccountOperationType.CREATE,         // Crear
                    AccountOperationType.MODIFY,         // Modificar
                    AccountOperationType.DELETE,         // Eliminar
                    AccountOperationType.ACTIVATE,       // Activar
                    AccountOperationType.DEACTIVATE,     // Desactivar
                    AccountOperationType.BLOCK,          // Bloquear
                    AccountOperationType.UNBLOCK,        // Desbloquear
                    AccountOperationType.UPDATE,         // Actualizar
                    AccountOperationType.RESTORE         // Restaurar
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
            assertThat(AccountOperationType.CREATE.name()).isEqualTo("CREATE");         // Crear
            assertThat(AccountOperationType.MODIFY.name()).isEqualTo("MODIFY");         // Modificar
            assertThat(AccountOperationType.DELETE.name()).isEqualTo("DELETE");         // Eliminar
            assertThat(AccountOperationType.ACTIVATE.name()).isEqualTo("ACTIVATE");     // Activar
            assertThat(AccountOperationType.DEACTIVATE.name()).isEqualTo("DEACTIVATE"); // Desactivar
            assertThat(AccountOperationType.BLOCK.name()).isEqualTo("BLOCK");           // Bloquear
            assertThat(AccountOperationType.UNBLOCK.name()).isEqualTo("UNBLOCK");       // Desbloquear
            assertThat(AccountOperationType.UPDATE.name()).isEqualTo("UPDATE");         // Actualizar
            assertThat(AccountOperationType.RESTORE.name()).isEqualTo("RESTORE");       // Restaurar
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(AccountOperationType.CREATE.toString()).isEqualTo("CREATE");         // Crear
            assertThat(AccountOperationType.MODIFY.toString()).isEqualTo("MODIFY");         // Modificar
            assertThat(AccountOperationType.DELETE.toString()).isEqualTo("DELETE");         // Eliminar
            assertThat(AccountOperationType.ACTIVATE.toString()).isEqualTo("ACTIVATE");     // Activar
            assertThat(AccountOperationType.DEACTIVATE.toString()).isEqualTo("DEACTIVATE"); // Desactivar
            assertThat(AccountOperationType.BLOCK.toString()).isEqualTo("BLOCK");           // Bloquear
            assertThat(AccountOperationType.UNBLOCK.toString()).isEqualTo("UNBLOCK");       // Desbloquear
            assertThat(AccountOperationType.UPDATE.toString()).isEqualTo("UPDATE");         // Actualizar
            assertThat(AccountOperationType.RESTORE.toString()).isEqualTo("RESTORE");       // Restaurar
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(AccountOperationType.valueOf("CREATE")).isEqualTo(AccountOperationType.CREATE);         // Crear
            assertThat(AccountOperationType.valueOf("MODIFY")).isEqualTo(AccountOperationType.MODIFY);         // Modificar
            assertThat(AccountOperationType.valueOf("DELETE")).isEqualTo(AccountOperationType.DELETE);         // Eliminar
            assertThat(AccountOperationType.valueOf("ACTIVATE")).isEqualTo(AccountOperationType.ACTIVATE);     // Activar
            assertThat(AccountOperationType.valueOf("DEACTIVATE")).isEqualTo(AccountOperationType.DEACTIVATE); // Desactivar
            assertThat(AccountOperationType.valueOf("BLOCK")).isEqualTo(AccountOperationType.BLOCK);           // Bloquear
            assertThat(AccountOperationType.valueOf("UNBLOCK")).isEqualTo(AccountOperationType.UNBLOCK);       // Desbloquear
            assertThat(AccountOperationType.valueOf("UPDATE")).isEqualTo(AccountOperationType.UPDATE);         // Actualizar
            assertThat(AccountOperationType.valueOf("RESTORE")).isEqualTo(AccountOperationType.RESTORE);       // Restaurar
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            AccountOperationType create1 = AccountOperationType.CREATE;         // Crear
            AccountOperationType create2 = AccountOperationType.CREATE;         // Crear
            AccountOperationType modify = AccountOperationType.MODIFY;          // Modificar
            
            // Act & Assert
            assertThat(create1).isEqualTo(create2);
            assertThat(create1).isNotEqualTo(modify);
            assertThat(create1 == create2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            AccountOperationType operation = AccountOperationType.ACTIVATE;  // Activar
            
            // Act - Functional switch expression
            String result = switch (operation) {
                case CREATE -> "Account created";         // Cuenta creada
                case MODIFY -> "Account modified";        // Cuenta modificada
                case DELETE -> "Account deleted";         // Cuenta eliminada
                case ACTIVATE -> "Account activated";     // Cuenta activada
                case DEACTIVATE -> "Account deactivated"; // Cuenta desactivada
                case BLOCK -> "Account blocked";          // Cuenta bloqueada
                case UNBLOCK -> "Account unblocked";      // Cuenta desbloqueada
                case UPDATE -> "Account updated";         // Cuenta actualizada
                case RESTORE -> "Account restored";       // Cuenta restaurada
            };
            
            // Assert
            assertThat(result).isEqualTo("Account activated");  // Cuenta activada
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(AccountOperationType.CREATE.ordinal()).isEqualTo(0);         // Crear
            assertThat(AccountOperationType.MODIFY.ordinal()).isEqualTo(1);         // Modificar
            assertThat(AccountOperationType.DELETE.ordinal()).isEqualTo(2);         // Eliminar
            assertThat(AccountOperationType.ACTIVATE.ordinal()).isEqualTo(3);       // Activar
            assertThat(AccountOperationType.DEACTIVATE.ordinal()).isEqualTo(4);     // Desactivar
            assertThat(AccountOperationType.BLOCK.ordinal()).isEqualTo(5);          // Bloquear
            assertThat(AccountOperationType.UNBLOCK.ordinal()).isEqualTo(6);        // Desbloquear
            assertThat(AccountOperationType.UPDATE.ordinal()).isEqualTo(7);         // Actualizar
            assertThat(AccountOperationType.RESTORE.ordinal()).isEqualTo(8);        // Restaurar
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            AccountOperationType operation = null;
            
            // Act & Assert
            assertThat(operation).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (AccountOperationType operation : AccountOperationType.values()) {
                counter++;
                assertThat(operation).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(9);
        }
    }
} 