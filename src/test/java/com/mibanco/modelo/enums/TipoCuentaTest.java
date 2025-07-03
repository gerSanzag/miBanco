package com.mibanco.modelo.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoCuenta
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoCuenta Enum Tests")
class TipoCuentaTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 3 valores")
        void deberiaTenerExactamenteTresValores() {
            // Arrange
            TipoCuenta[] valores = TipoCuenta.values();
            
            // Act & Assert
            assertThat(valores).hasSize(3);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoCuenta[] valores = TipoCuenta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoCuenta.AHORRO,
                    TipoCuenta.CORRIENTE,
                    TipoCuenta.PLAZO_FIJO
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoCuenta[] valores = TipoCuenta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoCuenta.AHORRO,
                    TipoCuenta.CORRIENTE,
                    TipoCuenta.PLAZO_FIJO
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
            assertThat(TipoCuenta.AHORRO.name()).isEqualTo("AHORRO");
            assertThat(TipoCuenta.CORRIENTE.name()).isEqualTo("CORRIENTE");
            assertThat(TipoCuenta.PLAZO_FIJO.name()).isEqualTo("PLAZO_FIJO");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoCuenta.AHORRO.toString()).isEqualTo("AHORRO");
            assertThat(TipoCuenta.CORRIENTE.toString()).isEqualTo("CORRIENTE");
            assertThat(TipoCuenta.PLAZO_FIJO.toString()).isEqualTo("PLAZO_FIJO");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoCuenta.valueOf("AHORRO")).isEqualTo(TipoCuenta.AHORRO);
            assertThat(TipoCuenta.valueOf("CORRIENTE")).isEqualTo(TipoCuenta.CORRIENTE);
            assertThat(TipoCuenta.valueOf("PLAZO_FIJO")).isEqualTo(TipoCuenta.PLAZO_FIJO);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoCuenta ahorro1 = TipoCuenta.AHORRO;
            TipoCuenta ahorro2 = TipoCuenta.AHORRO;
            TipoCuenta corriente = TipoCuenta.CORRIENTE;
            
            // Act & Assert
            assertThat(ahorro1).isEqualTo(ahorro2);
            assertThat(ahorro1).isNotEqualTo(corriente);
            assertThat(ahorro1 == ahorro2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch statements")
        void deberiaPoderUsarEnSwitchStatements() {
            // Arrange
            TipoCuenta tipo = TipoCuenta.CORRIENTE;
            String resultado = "";
            
            // Act
            switch (tipo) {
                case AHORRO:
                    resultado = "Cuenta de ahorro";
                    break;
                case CORRIENTE:
                    resultado = "Cuenta corriente";
                    break;
                case PLAZO_FIJO:
                    resultado = "Cuenta a plazo fijo";
                    break;
            }
            
            // Assert
            assertThat(resultado).isEqualTo("Cuenta corriente");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoCuenta.AHORRO.ordinal()).isEqualTo(0);
            assertThat(TipoCuenta.CORRIENTE.ordinal()).isEqualTo(1);
            assertThat(TipoCuenta.PLAZO_FIJO.ordinal()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoCuenta tipo = null;
            
            // Act & Assert
            assertThat(tipo).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoCuenta tipo : TipoCuenta.values()) {
                contador++;
                assertThat(tipo).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(3);
        }
    }
}
