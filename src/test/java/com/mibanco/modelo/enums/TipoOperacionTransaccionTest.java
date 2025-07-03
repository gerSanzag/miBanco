package com.mibanco.modelo.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoOperacionTransaccion
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoOperacionTransaccion Enum Tests")
class TipoOperacionTransaccionTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 8 valores")
        void deberiaTenerExactamenteOchoValores() {
            // Arrange
            TipoOperacionTransaccion[] valores = TipoOperacionTransaccion.values();
            
            // Act & Assert
            assertThat(valores).hasSize(8);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoOperacionTransaccion[] valores = TipoOperacionTransaccion.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoOperacionTransaccion.CREAR,
                    TipoOperacionTransaccion.ANULAR,
                    TipoOperacionTransaccion.REVERSAR,
                    TipoOperacionTransaccion.VERIFICAR,
                    TipoOperacionTransaccion.AUTORIZAR,
                    TipoOperacionTransaccion.RECHAZAR,
                    TipoOperacionTransaccion.ACTUALIZAR,
                    TipoOperacionTransaccion.ELIMINAR
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoOperacionTransaccion[] valores = TipoOperacionTransaccion.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoOperacionTransaccion.CREAR,
                    TipoOperacionTransaccion.ANULAR,
                    TipoOperacionTransaccion.REVERSAR,
                    TipoOperacionTransaccion.VERIFICAR,
                    TipoOperacionTransaccion.AUTORIZAR,
                    TipoOperacionTransaccion.RECHAZAR,
                    TipoOperacionTransaccion.ACTUALIZAR,
                    TipoOperacionTransaccion.ELIMINAR
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
            assertThat(TipoOperacionTransaccion.CREAR.name()).isEqualTo("CREAR");
            assertThat(TipoOperacionTransaccion.ANULAR.name()).isEqualTo("ANULAR");
            assertThat(TipoOperacionTransaccion.REVERSAR.name()).isEqualTo("REVERSAR");
            assertThat(TipoOperacionTransaccion.VERIFICAR.name()).isEqualTo("VERIFICAR");
            assertThat(TipoOperacionTransaccion.AUTORIZAR.name()).isEqualTo("AUTORIZAR");
            assertThat(TipoOperacionTransaccion.RECHAZAR.name()).isEqualTo("RECHAZAR");
            assertThat(TipoOperacionTransaccion.ACTUALIZAR.name()).isEqualTo("ACTUALIZAR");
            assertThat(TipoOperacionTransaccion.ELIMINAR.name()).isEqualTo("ELIMINAR");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionTransaccion.CREAR.toString()).isEqualTo("CREAR");
            assertThat(TipoOperacionTransaccion.ANULAR.toString()).isEqualTo("ANULAR");
            assertThat(TipoOperacionTransaccion.REVERSAR.toString()).isEqualTo("REVERSAR");
            assertThat(TipoOperacionTransaccion.VERIFICAR.toString()).isEqualTo("VERIFICAR");
            assertThat(TipoOperacionTransaccion.AUTORIZAR.toString()).isEqualTo("AUTORIZAR");
            assertThat(TipoOperacionTransaccion.RECHAZAR.toString()).isEqualTo("RECHAZAR");
            assertThat(TipoOperacionTransaccion.ACTUALIZAR.toString()).isEqualTo("ACTUALIZAR");
            assertThat(TipoOperacionTransaccion.ELIMINAR.toString()).isEqualTo("ELIMINAR");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionTransaccion.valueOf("CREAR")).isEqualTo(TipoOperacionTransaccion.CREAR);
            assertThat(TipoOperacionTransaccion.valueOf("ANULAR")).isEqualTo(TipoOperacionTransaccion.ANULAR);
            assertThat(TipoOperacionTransaccion.valueOf("REVERSAR")).isEqualTo(TipoOperacionTransaccion.REVERSAR);
            assertThat(TipoOperacionTransaccion.valueOf("VERIFICAR")).isEqualTo(TipoOperacionTransaccion.VERIFICAR);
            assertThat(TipoOperacionTransaccion.valueOf("AUTORIZAR")).isEqualTo(TipoOperacionTransaccion.AUTORIZAR);
            assertThat(TipoOperacionTransaccion.valueOf("RECHAZAR")).isEqualTo(TipoOperacionTransaccion.RECHAZAR);
            assertThat(TipoOperacionTransaccion.valueOf("ACTUALIZAR")).isEqualTo(TipoOperacionTransaccion.ACTUALIZAR);
            assertThat(TipoOperacionTransaccion.valueOf("ELIMINAR")).isEqualTo(TipoOperacionTransaccion.ELIMINAR);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoOperacionTransaccion crear1 = TipoOperacionTransaccion.CREAR;
            TipoOperacionTransaccion crear2 = TipoOperacionTransaccion.CREAR;
            TipoOperacionTransaccion anular = TipoOperacionTransaccion.ANULAR;
            
            // Act & Assert
            assertThat(crear1).isEqualTo(crear2);
            assertThat(crear1).isNotEqualTo(anular);
            assertThat(crear1 == crear2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch statements")
        void deberiaPoderUsarEnSwitchStatements() {
            // Arrange
            TipoOperacionTransaccion operacion = TipoOperacionTransaccion.AUTORIZAR;
            String resultado = "";
            
            // Act
            switch (operacion) {
                case CREAR:
                    resultado = "Transacción creada";
                    break;
                case ANULAR:
                    resultado = "Transacción anulada";
                    break;
                case REVERSAR:
                    resultado = "Transacción revertida";
                    break;
                case VERIFICAR:
                    resultado = "Transacción verificada";
                    break;
                case AUTORIZAR:
                    resultado = "Transacción autorizada";
                    break;
                case RECHAZAR:
                    resultado = "Transacción rechazada";
                    break;
                case ACTUALIZAR:
                    resultado = "Transacción actualizada";
                    break;
                case ELIMINAR:
                    resultado = "Transacción eliminada";
                    break;
            }
            
            // Assert
            assertThat(resultado).isEqualTo("Transacción autorizada");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionTransaccion.CREAR.ordinal()).isEqualTo(0);
            assertThat(TipoOperacionTransaccion.ANULAR.ordinal()).isEqualTo(1);
            assertThat(TipoOperacionTransaccion.REVERSAR.ordinal()).isEqualTo(2);
            assertThat(TipoOperacionTransaccion.VERIFICAR.ordinal()).isEqualTo(3);
            assertThat(TipoOperacionTransaccion.AUTORIZAR.ordinal()).isEqualTo(4);
            assertThat(TipoOperacionTransaccion.RECHAZAR.ordinal()).isEqualTo(5);
            assertThat(TipoOperacionTransaccion.ACTUALIZAR.ordinal()).isEqualTo(6);
            assertThat(TipoOperacionTransaccion.ELIMINAR.ordinal()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoOperacionTransaccion operacion = null;
            
            // Act & Assert
            assertThat(operacion).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoOperacionTransaccion operacion : TipoOperacionTransaccion.values()) {
                contador++;
                assertThat(operacion).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(8);
        }
    }
}
