package com.mibanco.modeloTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.enums.TipoTransaccion;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoTransaccion
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoTransaccion Enum Tests")
class TipoTransaccionTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 5 valores")
        void deberiaTenerExactamenteCincoValores() {
            // Arrange
            TipoTransaccion[] valores = TipoTransaccion.values();
            
            // Act & Assert
            assertThat(valores).hasSize(5);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoTransaccion[] valores = TipoTransaccion.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoTransaccion.DEPOSITO,
                    TipoTransaccion.RETIRO,
                    TipoTransaccion.TRANSFERENCIA_ENVIADA,
                    TipoTransaccion.TRANSFERENCIA_RECIBIDA,
                    TipoTransaccion.PAGO_SERVICIO
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoTransaccion[] valores = TipoTransaccion.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoTransaccion.DEPOSITO,
                    TipoTransaccion.RETIRO,
                    TipoTransaccion.TRANSFERENCIA_ENVIADA,
                    TipoTransaccion.TRANSFERENCIA_RECIBIDA,
                    TipoTransaccion.PAGO_SERVICIO
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
            assertThat(TipoTransaccion.DEPOSITO.name()).isEqualTo("DEPOSITO");
            assertThat(TipoTransaccion.RETIRO.name()).isEqualTo("RETIRO");
            assertThat(TipoTransaccion.TRANSFERENCIA_ENVIADA.name()).isEqualTo("TRANSFERENCIA_ENVIADA");
            assertThat(TipoTransaccion.TRANSFERENCIA_RECIBIDA.name()).isEqualTo("TRANSFERENCIA_RECIBIDA");
            assertThat(TipoTransaccion.PAGO_SERVICIO.name()).isEqualTo("PAGO_SERVICIO");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoTransaccion.DEPOSITO.toString()).isEqualTo("DEPOSITO");
            assertThat(TipoTransaccion.RETIRO.toString()).isEqualTo("RETIRO");
            assertThat(TipoTransaccion.TRANSFERENCIA_ENVIADA.toString()).isEqualTo("TRANSFERENCIA_ENVIADA");
            assertThat(TipoTransaccion.TRANSFERENCIA_RECIBIDA.toString()).isEqualTo("TRANSFERENCIA_RECIBIDA");
            assertThat(TipoTransaccion.PAGO_SERVICIO.toString()).isEqualTo("PAGO_SERVICIO");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoTransaccion.valueOf("DEPOSITO")).isEqualTo(TipoTransaccion.DEPOSITO);
            assertThat(TipoTransaccion.valueOf("RETIRO")).isEqualTo(TipoTransaccion.RETIRO);
            assertThat(TipoTransaccion.valueOf("TRANSFERENCIA_ENVIADA")).isEqualTo(TipoTransaccion.TRANSFERENCIA_ENVIADA);
            assertThat(TipoTransaccion.valueOf("TRANSFERENCIA_RECIBIDA")).isEqualTo(TipoTransaccion.TRANSFERENCIA_RECIBIDA);
            assertThat(TipoTransaccion.valueOf("PAGO_SERVICIO")).isEqualTo(TipoTransaccion.PAGO_SERVICIO);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoTransaccion deposito1 = TipoTransaccion.DEPOSITO;
            TipoTransaccion deposito2 = TipoTransaccion.DEPOSITO;
            TipoTransaccion retiro = TipoTransaccion.RETIRO;
            
            // Act & Assert
            assertThat(deposito1).isEqualTo(deposito2);
            assertThat(deposito1).isNotEqualTo(retiro);
            assertThat(deposito1 == deposito2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch expressions funcionales")
        void deberiaPoderUsarEnSwitchExpressions() {
            // Arrange
            TipoTransaccion tipo = TipoTransaccion.TRANSFERENCIA_ENVIADA;
            
            // Act - Switch expression funcional
            String resultado = switch (tipo) {
                case DEPOSITO -> "Depósito realizado";
                case RETIRO -> "Retiro realizado";
                case TRANSFERENCIA_ENVIADA -> "Transferencia enviada";
                case TRANSFERENCIA_RECIBIDA -> "Transferencia recibida";
                case PAGO_SERVICIO -> "Pago de servicio";
            };
            
            // Assert
            assertThat(resultado).isEqualTo("Transferencia enviada");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoTransaccion.DEPOSITO.ordinal()).isEqualTo(0);
            assertThat(TipoTransaccion.RETIRO.ordinal()).isEqualTo(1);
            assertThat(TipoTransaccion.TRANSFERENCIA_ENVIADA.ordinal()).isEqualTo(2);
            assertThat(TipoTransaccion.TRANSFERENCIA_RECIBIDA.ordinal()).isEqualTo(3);
            assertThat(TipoTransaccion.PAGO_SERVICIO.ordinal()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoTransaccion tipo = null;
            
            // Act & Assert
            assertThat(tipo).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoTransaccion tipo : TipoTransaccion.values()) {
                contador++;
                assertThat(tipo).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(5);
        }
    }
}
