package com.mibanco.modelo.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoTarjeta
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoTarjeta Enum Tests")
class TipoTarjetaTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 2 valores")
        void deberiaTenerExactamenteDosValores() {
            // Arrange
            TipoTarjeta[] valores = TipoTarjeta.values();
            
            // Act & Assert
            assertThat(valores).hasSize(2);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoTarjeta[] valores = TipoTarjeta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoTarjeta.DEBITO,
                    TipoTarjeta.CREDITO
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoTarjeta[] valores = TipoTarjeta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoTarjeta.DEBITO,
                    TipoTarjeta.CREDITO
                );
        }
    }

    @Nested
    @DisplayName("Nombres del enum")
    class NombresEnumTest {

        @Test
        @DisplayName("Debería tener nombres correctos")
        void deberiaTenerNombresCorrectos() {
            // Arrange & Act & Assert
            assertThat(TipoTarjeta.DEBITO.name()).isEqualTo("DEBITO");
            assertThat(TipoTarjeta.CREDITO.name()).isEqualTo("CREDITO");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoTarjeta.DEBITO.toString()).isEqualTo("DEBITO");
            assertThat(TipoTarjeta.CREDITO.toString()).isEqualTo("CREDITO");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoTarjeta.valueOf("DEBITO")).isEqualTo(TipoTarjeta.DEBITO);
            assertThat(TipoTarjeta.valueOf("CREDITO")).isEqualTo(TipoTarjeta.CREDITO);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoTarjeta debito1 = TipoTarjeta.DEBITO;
            TipoTarjeta debito2 = TipoTarjeta.DEBITO;
            TipoTarjeta credito = TipoTarjeta.CREDITO;
            
            // Act & Assert
            assertThat(debito1).isEqualTo(debito2);
            assertThat(debito1).isNotEqualTo(credito);
            assertThat(debito1 == debito2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch statements")
        void deberiaPoderUsarEnSwitchStatements() {
            // Arrange
            TipoTarjeta tipo = TipoTarjeta.CREDITO;
            String resultado = "";
            
            // Act
            switch (tipo) {
                case DEBITO:
                    resultado = "Tarjeta de débito";
                    break;
                case CREDITO:
                    resultado = "Tarjeta de crédito";
                    break;
            }
            
            // Assert
            assertThat(resultado).isEqualTo("Tarjeta de crédito");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoTarjeta.DEBITO.ordinal()).isEqualTo(0);
            assertThat(TipoTarjeta.CREDITO.ordinal()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoTarjeta tipo = null;
            
            // Act & Assert
            assertThat(tipo).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoTarjeta tipo : TipoTarjeta.values()) {
                contador++;
                assertThat(tipo).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(2);
        }
    }
}
