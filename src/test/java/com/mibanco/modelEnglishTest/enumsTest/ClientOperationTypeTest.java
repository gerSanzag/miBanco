package com.mibanco.modelEnglishTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelEnglish.enums.ClientOperationType;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ClientOperationType enum
 * 
 * <p>This test suite verifies:
 * <ul>
 *   <li>That all enum values exist</li>
 *   <li>That names are correct</li>
 *   <li>That order is as expected</li>
 *   <li>That the enum works correctly in different contexts</li>
 * </ul>
 */
@DisplayName("ClientOperationType Enum Tests")
class ClientOperationTypeTest {

    @Nested
    @DisplayName("Enum values")
    class EnumValuesTest {

        @Test
        @DisplayName("Should have exactly 9 values")
        void shouldHaveExactlyNineValues() {
            // Arrange
            ClientOperationType[] values = ClientOperationType.values();
            
            // Act & Assert
            assertThat(values).hasSize(9);
        }

        @Test
        @DisplayName("Should contain all expected values")
        void shouldContainAllExpectedValues() {
            // Arrange
            ClientOperationType[] values = ClientOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactlyInAnyOrder(
                    ClientOperationType.CREATE,                 // Crear
                    ClientOperationType.UPDATE,                 // Actualizar
                    ClientOperationType.MODIFY,                 // Modificar
                    ClientOperationType.DELETE,                 // Eliminar
                    ClientOperationType.QUERY,                  // Consultar
                    ClientOperationType.RESTORE,                // Restaurar
                    ClientOperationType.BLOCK,                  // Bloquear
                    ClientOperationType.UNBLOCK,                // Desbloquear
                    ClientOperationType.CHANGE_CONTACT_DATA     // Cambiar datos de contacto
                );
        }

        @Test
        @DisplayName("Should have correct order")
        void shouldHaveCorrectOrder() {
            // Arrange
            ClientOperationType[] values = ClientOperationType.values();
            
            // Act & Assert
            assertThat(values)
                .containsExactly(
                    ClientOperationType.CREATE,                 // Crear
                    ClientOperationType.UPDATE,                 // Actualizar
                    ClientOperationType.MODIFY,                 // Modificar
                    ClientOperationType.DELETE,                 // Eliminar
                    ClientOperationType.QUERY,                  // Consultar
                    ClientOperationType.RESTORE,                // Restaurar
                    ClientOperationType.BLOCK,                  // Bloquear
                    ClientOperationType.UNBLOCK,                // Desbloquear
                    ClientOperationType.CHANGE_CONTACT_DATA     // Cambiar datos de contacto
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
            assertThat(ClientOperationType.CREATE.name()).isEqualTo("CREATE");                 // Crear
            assertThat(ClientOperationType.UPDATE.name()).isEqualTo("UPDATE");                 // Actualizar
            assertThat(ClientOperationType.MODIFY.name()).isEqualTo("MODIFY");                 // Modificar
            assertThat(ClientOperationType.DELETE.name()).isEqualTo("DELETE");                 // Eliminar
            assertThat(ClientOperationType.QUERY.name()).isEqualTo("QUERY");                   // Consultar
            assertThat(ClientOperationType.RESTORE.name()).isEqualTo("RESTORE");               // Restaurar
            assertThat(ClientOperationType.BLOCK.name()).isEqualTo("BLOCK");                   // Bloquear
            assertThat(ClientOperationType.UNBLOCK.name()).isEqualTo("UNBLOCK");               // Desbloquear
            assertThat(ClientOperationType.CHANGE_CONTACT_DATA.name()).isEqualTo("CHANGE_CONTACT_DATA"); // Cambiar datos de contacto
        }

        @Test
        @DisplayName("Should have toString() equal to name")
        void shouldHaveToStringEqualToName() {
            // Arrange & Act & Assert
            assertThat(ClientOperationType.CREATE.toString()).isEqualTo("CREATE");                 // Crear
            assertThat(ClientOperationType.UPDATE.toString()).isEqualTo("UPDATE");                 // Actualizar
            assertThat(ClientOperationType.MODIFY.toString()).isEqualTo("MODIFY");                 // Modificar
            assertThat(ClientOperationType.DELETE.toString()).isEqualTo("DELETE");                 // Eliminar
            assertThat(ClientOperationType.QUERY.toString()).isEqualTo("QUERY");                   // Consultar
            assertThat(ClientOperationType.RESTORE.toString()).isEqualTo("RESTORE");               // Restaurar
            assertThat(ClientOperationType.BLOCK.toString()).isEqualTo("BLOCK");                   // Bloquear
            assertThat(ClientOperationType.UNBLOCK.toString()).isEqualTo("UNBLOCK");               // Desbloquear
            assertThat(ClientOperationType.CHANGE_CONTACT_DATA.toString()).isEqualTo("CHANGE_CONTACT_DATA"); // Cambiar datos de contacto
        }
    }

    @Nested
    @DisplayName("Enum functionality")
    class EnumFunctionalityTest {

        @Test
        @DisplayName("Should be able to get value by name")
        void shouldBeAbleToGetValueByName() {
            // Arrange & Act & Assert
            assertThat(ClientOperationType.valueOf("CREATE")).isEqualTo(ClientOperationType.CREATE);                 // Crear
            assertThat(ClientOperationType.valueOf("UPDATE")).isEqualTo(ClientOperationType.UPDATE);                 // Actualizar
            assertThat(ClientOperationType.valueOf("MODIFY")).isEqualTo(ClientOperationType.MODIFY);                 // Modificar
            assertThat(ClientOperationType.valueOf("DELETE")).isEqualTo(ClientOperationType.DELETE);                 // Eliminar
            assertThat(ClientOperationType.valueOf("QUERY")).isEqualTo(ClientOperationType.QUERY);                   // Consultar
            assertThat(ClientOperationType.valueOf("RESTORE")).isEqualTo(ClientOperationType.RESTORE);               // Restaurar
            assertThat(ClientOperationType.valueOf("BLOCK")).isEqualTo(ClientOperationType.BLOCK);                   // Bloquear
            assertThat(ClientOperationType.valueOf("UNBLOCK")).isEqualTo(ClientOperationType.UNBLOCK);               // Desbloquear
            assertThat(ClientOperationType.valueOf("CHANGE_CONTACT_DATA")).isEqualTo(ClientOperationType.CHANGE_CONTACT_DATA); // Cambiar datos de contacto
        }

        @Test
        @DisplayName("Should be able to compare values")
        void shouldBeAbleToCompareValues() {
            // Arrange
            ClientOperationType create1 = ClientOperationType.CREATE;                 // Crear
            ClientOperationType create2 = ClientOperationType.CREATE;                 // Crear
            ClientOperationType update = ClientOperationType.UPDATE;                 // Actualizar
            
            // Act & Assert
            assertThat(create1).isEqualTo(create2);
            assertThat(create1).isNotEqualTo(update);
            assertThat(create1 == create2).isTrue(); // Reference comparison
        }

        @Test
        @DisplayName("Should be able to use in functional switch expressions")
        void shouldBeAbleToUseInFunctionalSwitchExpressions() {
            // Arrange
            ClientOperationType operation = ClientOperationType.BLOCK;  // Bloquear
            
            // Act - Functional switch expression
            String result = switch (operation) {
                case CREATE -> "Client created";                 // Cliente creado
                case UPDATE -> "Client updated";                 // Cliente actualizado
                case MODIFY -> "Client modified";                // Cliente modificado
                case DELETE -> "Client deleted";                 // Cliente eliminado
                case QUERY -> "Client queried";                  // Cliente consultado
                case RESTORE -> "Client restored";               // Cliente restaurado
                case BLOCK -> "Client blocked";                  // Cliente bloqueado
                case UNBLOCK -> "Client unblocked";              // Cliente desbloqueado
                case CHANGE_CONTACT_DATA -> "Contact data changed"; // Datos de contacto cambiados
            };
            
            // Assert
            assertThat(result).isEqualTo("Client blocked");  // Cliente bloqueado
        }

        @Test
        @DisplayName("Should be able to get correct ordinal")
        void shouldBeAbleToGetCorrectOrdinal() {
            // Arrange & Act & Assert
            assertThat(ClientOperationType.CREATE.ordinal()).isEqualTo(0);                 // Crear
            assertThat(ClientOperationType.UPDATE.ordinal()).isEqualTo(1);                 // Actualizar
            assertThat(ClientOperationType.MODIFY.ordinal()).isEqualTo(2);                 // Modificar
            assertThat(ClientOperationType.DELETE.ordinal()).isEqualTo(3);                 // Eliminar
            assertThat(ClientOperationType.QUERY.ordinal()).isEqualTo(4);                  // Consultar
            assertThat(ClientOperationType.RESTORE.ordinal()).isEqualTo(5);                // Restaurar
            assertThat(ClientOperationType.BLOCK.ordinal()).isEqualTo(6);                  // Bloquear
            assertThat(ClientOperationType.UNBLOCK.ordinal()).isEqualTo(7);                // Desbloquear
            assertThat(ClientOperationType.CHANGE_CONTACT_DATA.ordinal()).isEqualTo(8);    // Cambiar datos de contacto
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle null values correctly")
        void shouldHandleNullValuesCorrectly() {
            // Arrange
            ClientOperationType operation = null;
            
            // Act & Assert
            assertThat(operation).isNull();
        }

        @Test
        @DisplayName("Should be able to iterate over all values")
        void shouldBeAbleToIterateOverAllValues() {
            // Arrange
            int counter = 0;
            
            // Act
            for (ClientOperationType operation : ClientOperationType.values()) {
                counter++;
                assertThat(operation).isNotNull();
            }
            
            // Assert
            assertThat(counter).isEqualTo(9);
        }
    }
} 